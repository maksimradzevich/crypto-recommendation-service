package com.xm.crypto_recommendation_service.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.xm.crypto_recommendation_service.model.CurrencyStatistics;
import com.xm.crypto_recommendation_service.model.PriceTick;
import com.xm.crypto_recommendation_service.repository.PriceTickRepository;

class CurrencyServiceTest {

    private final PriceTickRepository priceTickRepository = mock();

    private final CurrencyService currencyService = new CurrencyService(new String[]{"ETH", "BTC", "LTC"}, priceTickRepository);

    @Test
    void findStatistics_statisticsFound() {
        String currency = "BTC";

        PriceTick minimum = new PriceTick(LocalDateTime.now(), currency, BigDecimal.ZERO);
        PriceTick maximum = new PriceTick(LocalDateTime.now(), currency, BigDecimal.TEN);
        PriceTick oldest = new PriceTick(LocalDateTime.MIN, currency, BigDecimal.TEN);
        PriceTick newest = new PriceTick(LocalDateTime.MAX, currency, BigDecimal.TEN);

        when(priceTickRepository.findMinimum(currency)).thenReturn(Optional.of(minimum));
        when(priceTickRepository.findMaximum(currency)).thenReturn(Optional.of(maximum));
        when(priceTickRepository.findOldest(currency)).thenReturn(Optional.of(oldest));
        when(priceTickRepository.findNewest(currency)).thenReturn(Optional.of(newest));

        CurrencyStatistics expected = new CurrencyStatistics(minimum, maximum, oldest, newest);

        CurrencyStatistics actual = currencyService.findStatistics(currency);

        assertEquals(expected, actual);
    }

    @Test
    void getCurrenciesSortedByNormalizedRangeDesc_allCurrenciesHaveNormalizedRange_returnsCurrenciesSortedByRange() {
        Map<String, BigDecimal> currencyToMinimumValue = Map.of(
                "BTC", BigDecimal.TWO,
                "ETH", BigDecimal.ONE,
                "LTC", new BigDecimal(3)
        );

        Map<String, BigDecimal> currencyToMaximumValue = Map.of(
                "BTC", BigDecimal.TEN,
                "ETH", BigDecimal.TEN,
                "LTC", BigDecimal.TEN
        );

        currencyToMinimumValue.forEach((currency, value) -> {
            Optional<PriceTick> priceTick = Optional.of(new PriceTick(LocalDateTime.now(), currency, value));
            when(priceTickRepository.findMinimum(currency)).thenReturn(priceTick);
        });

        currencyToMaximumValue.forEach((currency, value) -> {
            Optional<PriceTick> priceTick = Optional.of(new PriceTick(LocalDateTime.now(), currency, value));
            when(priceTickRepository.findMaximum(currency)).thenReturn(priceTick);
        });

        List<String> expected = List.of("ETH", "BTC", "LTC");

        List<String> actual = currencyService.getCurrenciesSortedByNormalizedRangeDesc();

        assertEquals(expected, actual);
    }

    @Test
    void findCurrencyWithHighestNormalizedRangeForDate_allCurrenciesHaveNormalizedRange_returnsCurrencyWithHighestRange() {
        Map<String, BigDecimal> currencyToMinimumValue = Map.of(
                "BTC", BigDecimal.TWO,
                "ETH", BigDecimal.ONE,
                "LTC", new BigDecimal(3)
        );
        Map<String, BigDecimal> currencyToMaximumValue = Map.of(
                "BTC", BigDecimal.TEN,
                "ETH", BigDecimal.TEN,
                "LTC", BigDecimal.TEN
        );
        LocalDateTime now = LocalDateTime.now();
        LocalDate today = LocalDate.now();

        currencyToMinimumValue.forEach((currency, value) -> {
            Optional<PriceTick> priceTick = Optional.of(new PriceTick(now, currency, value));
            when(priceTickRepository.findMinimumForDate(currency, today)).thenReturn(priceTick);
        });

        currencyToMaximumValue.forEach((currency, value) -> {
            Optional<PriceTick> priceTick = Optional.of(new PriceTick(now, currency, value));
            when(priceTickRepository.findMaximumForDate(currency, today)).thenReturn(priceTick);
        });

        Optional<String> actual = currencyService.findCurrencyWithHighestNormalizedRangeForDate(today);

        assertEquals("ETH", actual.get());
    }
}