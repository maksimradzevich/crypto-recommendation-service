package com.xm.crypto_recommendation_service.validation;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class CurrencyValidatorTest {

    private final CurrencyValidator validator = new CurrencyValidator(new String[]{"BTC", "ETH"});

    @Test
    void isValid_returnsTrue() {
        assertTrue(validator.isValid("BTC", null));
    }

    @Test
    void isValid_returnsFalse() {
        assertFalse(validator.isValid("AAA", null));
    }
}