package com.xm.crypto_recommendation_service.service;

import static java.util.Map.Entry.comparingByValue;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.xm.crypto_recommendation_service.model.PriceTick;
import com.xm.crypto_recommendation_service.model.CurrencyStatistics;
import com.xm.crypto_recommendation_service.repository.PriceTickRepository;

import lombok.AllArgsConstructor;

/**
 * The CurrencyService class is responsible for performing operations related to currency statistics.
 */
@Service
@AllArgsConstructor
public class CurrencyService {

    @Value("${currencies}")
    private String[] currencies;

    private final PriceTickRepository priceTickRepository;

    /**
     * Retrieves currency statistics for a given currency symbol.
     *
     * @param currency The currency symbol. Must be a valid cryptocurrency symbol.
     * @return The currency statistics object containing the minimum, maximum, oldest, and newest price ticks.
     */
    public CurrencyStatistics findStatistics(String currency) {
        return new CurrencyStatistics(
                priceTickRepository.findMinimum(currency).orElse(null),
                priceTickRepository.findMaximum(currency).orElse(null),
                priceTickRepository.findOldest(currency).orElse(null),
                priceTickRepository.findNewest(currency).orElse(null)
        );
    }

    /**
     * Retrieves a list of currencies sorted in descending order by their normalized range.
     *
     * @return A list of currency symbols sorted by their normalized range in descending order.
     */
    public List<String> getCurrenciesSortedByNormalizedRangeDesc() {
        return Arrays.stream(currencies)
                .map(currency -> new AbstractMap.SimpleEntry<>(currency, getNormalizedRange(currency)))
                .filter(entry -> entry.getValue().isPresent())
                .sorted(Comparator.comparing(entry -> entry.getValue().get(), Comparator.reverseOrder()))
                .map(Entry::getKey)
                .collect(Collectors.toList());
    }

    /**
     * Finds the currency with the highest normalized range for the specified date.
     *
     * @param date The date for which to find the currency with the highest normalized range.
     * @return An optional string value representing the currency symbol if found, or an empty optional if not found.
     */
    public Optional<String> findCurrencyWithHighestNormalizedRangeForDate(LocalDate date) {
        return Arrays.stream(currencies)
                .map(currency -> new AbstractMap.SimpleEntry<>(currency, getNormalizedRangeForDate(currency, date)))
                .filter(entry -> entry.getValue().isPresent())
                .max(Comparator.comparing(entry -> entry.getValue().get()))
                .map(Map.Entry::getKey);
    }

    /**
     * Retrieves the normalized range for a given currency.
     *
     * @param currency The currency symbol. Must be a valid cryptocurrency symbol.
     * @return An Optional object containing the normalized range if both maximum and minimum price ticks are found, otherwise an empty Optional.
     */
    private Optional<BigDecimal> getNormalizedRange(String currency) {
        Optional<PriceTick> maximum = priceTickRepository.findMaximum(currency);
        Optional<PriceTick> minimum = priceTickRepository.findMinimum(currency);
        if (maximum.isPresent() && minimum.isPresent()) {
            return Optional.of(calculateNormalizedRange(maximum.get(), minimum.get()));
        } else {
            return Optional.empty();
        }
    }

    /**
     * Retrieves the normalized range for a given currency and date.
     *
     * @param currency The currency symbol. Must be a valid cryptocurrency symbol.
     * @param date The date for which to retrieve the normalized range.
     * @return An Optional object containing the normalized range if both maximum and minimum price ticks are found, otherwise an empty Optional.
     */
    private Optional<BigDecimal> getNormalizedRangeForDate(String currency, LocalDate date) {
        Optional<PriceTick> maximum = priceTickRepository.findMaximumForDate(currency, date);
        Optional<PriceTick> minimum = priceTickRepository.findMinimumForDate(currency, date);
        if (maximum.isPresent() && minimum.isPresent()) {
            return Optional.of(calculateNormalizedRange(maximum.get(), minimum.get()));
        } else {
            return Optional.empty();
        }
    }

    /**
     * Calculates the normalized range between two price ticks.
     *
     * @param maximum The maximum price tick.
     * @param minimum The minimum price tick.
     * @return The calculated normalized range.
     */
    private static BigDecimal calculateNormalizedRange(PriceTick maximum, PriceTick minimum) {
        BigDecimal maximumPrice = maximum.price();
        BigDecimal minimumPrice = minimum.price();
        return maximumPrice
                .subtract(minimumPrice)
                .divide(minimumPrice, 2, RoundingMode.HALF_UP);
    }
}
