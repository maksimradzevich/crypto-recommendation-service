package com.xm.crypto_recommendation_service.validation;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.AllArgsConstructor;

/**
 * The CurrencyValidator class is a custom validator that checks if the given currency symbol
 * is present in the list of valid currencies.
 */
@Component
@AllArgsConstructor
public class CurrencyValidator implements ConstraintValidator<CryptoCurrency, String> {

    @Value("${currencies}")
    private String[] currencies;

    @Override
    public void initialize(CryptoCurrency cryptoCurrency) {
    }

    @Override
    public boolean isValid(String currency, ConstraintValidatorContext cxt) {
        return Arrays.asList(currencies).contains(currency);
    }

}
