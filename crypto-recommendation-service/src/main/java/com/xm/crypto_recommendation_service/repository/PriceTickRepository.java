package com.xm.crypto_recommendation_service.repository;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.util.ResourceUtils;

import com.xm.crypto_recommendation_service.model.PriceTick;

/**
 * The PriceTickRepository class is a repository for accessing and querying price tick data.
 * It provides methods for finding the minimum, maximum, newest, and oldest price ticks
 * for a given currency, as well as for a given currency and date.
 * <p>
 * It uses a CSV file as the data source, with the following format:
 * - timestamp: the timestamp of the price tick (long value representing milliseconds since the epoch)
 * - symbol: the currency symbol
 * - price: the price of the currency
 * <p>
 * The repository uses Apache Commons CSV library for reading the CSV file.
 * The storage path of the CSV file is configured using the "storage-path" property.
 *
 */
@Repository
public class PriceTickRepository {

    private static final String[] CSV_HEADER = {"timestamp", "symbol", "price"};
    private static final String CSV_SOURCE_PATH = "%s_values.csv";

    @Value("${storage-path}")
    private String storagePath;

    private final CSVFormat CSV_FORMAT = CSVFormat.DEFAULT.builder()
            .setHeader(CSV_HEADER)
            .setSkipHeaderRecord(true)
            .build();

    /**
     * Finds the minimum price tick for the given currency.
     *
     * @param currency the currency to search for
     * @return an Optional containing the minimum price tick, or empty if not found
     */
    public Optional<PriceTick> findMinimum(String currency) {
        return findOneByFilter(
                currency,
                ticks -> ticks.min(getPriceComparator())
        );
    }

    /**
     * Finds the minimum price tick for the given currency and date.
     *
     * @param currency the currency to search for
     * @param date the date to search for
     * @return an Optional containing the minimum price tick for the given currency and date, or empty if not found
     */
    public Optional<PriceTick> findMinimumForDate(String currency, LocalDate date) {
        return findOneByFilter(
                currency,
                ticks -> ticks.filter(record -> isDateEqual(date, record))
                                  .min(getPriceComparator())
        );
    }

    /**
     * Finds the maximum price tick for the given currency.
     *
     * @param currency the currency to search for
     * @return an Optional containing the maximum price tick, or empty if not found
     */
    public Optional<PriceTick> findMaximum(String currency) {
        return findOneByFilter(
                currency,
                ticks -> ticks.max(getPriceComparator())
        );
    }

    /**
     * Finds the maximum price tick for the given currency and date.
     *
     * @param currency the currency to search for
     * @param date the date to search for
     * @return an Optional containing the maximum price tick for the given currency and date, or empty if not found
     */
    public Optional<PriceTick> findMaximumForDate(String currency, LocalDate date) {
        return findOneByFilter(
                currency,
                ticks -> ticks.filter(record -> isDateEqual(date, record))
                                  .max(getPriceComparator())
        );
    }

    /**
     * Finds the newest price tick for the given currency.
     *
     * @param currency the currency to search for
     * @return an Optional containing the newest price tick, or empty if not found
     */
    public Optional<PriceTick> findNewest(String currency) {
        return findOneByFilter(
                currency,
                ticks -> ticks.max(getTimestampComparator())
        );
    }

    /**
     * Finds the oldest price tick for the given currency.
     *
     * @param currency the currency to search for
     * @return an Optional containing the oldest price tick, or empty if not found
     */
    public Optional<PriceTick> findOldest(String currency) {
        return findOneByFilter(
                currency,
                ticks -> ticks.min(getTimestampComparator())
        );
    }

    /**
     * Checks if the given date is equal to the date in the provided PriceTick object.
     *
     * @param date The date to compare.
     * @param priceTick The PriceTick object containing the date to compare against.
     * @return {@code true} if the dates are equal, {@code false} otherwise.
     */
    private boolean isDateEqual(LocalDate date, PriceTick priceTick) {
        return priceTick.time().toLocalDate().equals(date);
    }

    private Comparator<PriceTick> getTimestampComparator() {
        return Comparator.comparing(PriceTick::time);
    }

    private Comparator<PriceTick> getPriceComparator() {
        return Comparator.comparing(PriceTick::price);
    }

    /**
     * Converts a timestamp to a LocalDateTime object using UTC time zone.
     *
     * @param timestamp the timestamp to be converted
     * @return the LocalDateTime object representing the converted timestamp
     */
    private static LocalDateTime mapTimestampToDate(String timestamp) {
        Instant instant = Instant.ofEpochMilli(Long.parseLong(timestamp));
        return LocalDateTime.ofInstant(instant, ZoneId.of("UTC"));
    }

    /**
     * Finds a single PriceTick object by applying a filter to a stream of PriceTick objects.
     *
     * @param currency The currency to search for.
     * @param filter   The filter to apply to the stream of PriceTick objects.
     * @return Optional containing a PriceTick object if found, otherwise empty.
     * @throws RuntimeException if an error occurs while reading the file.
     */
    private Optional<PriceTick> findOneByFilter(String currency, Function<Stream<PriceTick>, Optional<PriceTick>> filter) {
        try(FileReader fileReader = getFileReader(currency)) {
            CSVParser csvRecords = CSV_FORMAT.parse(fileReader);
            Stream<PriceTick> priceTickStream = StreamSupport.stream(csvRecords.spliterator(), false)
                                                             .map(this::mapCSVRecordToPriceTick);
            return filter.apply(priceTickStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Retrieves a FileReader for the specified currency.
     *
     * @param currency the currency for which to retrieve the FileReader
     * @return a FileReader object for reading the values of the specified currency
     * @throws FileNotFoundException if the file for the specified currency is not found
     */
    private FileReader getFileReader(String currency) throws FileNotFoundException {
        String source = CSV_SOURCE_PATH.formatted(currency);
        File file;
        if (storagePath.startsWith("classpath")) {
            file = ResourceUtils.getFile(storagePath + File.separator + source);
        } else {
            file = new File(storagePath, source);
        }
        return new FileReader(file);
    }

    /**
     * Maps a CSV record to a PriceTick object.
     *
     * @param csvRecord the CSV record to be mapped
     * @return the mapped PriceTick object
     */
    private PriceTick mapCSVRecordToPriceTick(CSVRecord csvRecord) {
        return new PriceTick(
                mapTimestampToDate(csvRecord.get("timestamp")),
                csvRecord.get("symbol"),
                new BigDecimal(csvRecord.get("price")));
    }

}
