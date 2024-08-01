package com.xm.crypto_recommendation_service.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.xm.crypto_recommendation_service.model.PriceTick;

@SpringBootTest
@ActiveProfiles("test")
class PriceTickRepositoryTest {

    @Autowired
    private PriceTickRepository priceTickRepository;

    @Test
    void findMinimum_minFound() {
        Optional<PriceTick> actual = priceTickRepository.findMinimum("BTC");
        assertEquals(new BigDecimal("33276.59"), actual.get().price());
    }

    @Test
    void findMinimumForDate_minFound() {
        Optional<PriceTick> actual = priceTickRepository.findMinimumForDate("BTC", LocalDate.of(2022, 1, 1));
        assertEquals(new BigDecimal("46813.21"), actual.get().price());
    }

    @Test
    void findMaximum_maxFound() {
        Optional<PriceTick> actual = priceTickRepository.findMaximum("BTC");
        assertEquals(new BigDecimal("47722.66"), actual.get().price());
    }

    @Test
    void findMaximumForDate_maxFound() {
        Optional<PriceTick> actual = priceTickRepository.findMaximumForDate("BTC", LocalDate.of(2022, 1, 1));
        assertEquals(new BigDecimal("47143.98"), actual.get().price());
    }

    @Test
    void findOldest_oldestFound() {
        Optional<PriceTick> actual = priceTickRepository.findOldest("BTC");
        assertEquals(LocalDateTime.of(2022, 1, 1 , 4, 0), actual.get().time());
    }

    @Test
    void findNewest_newestFound() {
        Optional<PriceTick> actual = priceTickRepository.findNewest("BTC");
        assertEquals(LocalDateTime.of(2022, 1, 31 , 20, 0), actual.get().time());
    }
}