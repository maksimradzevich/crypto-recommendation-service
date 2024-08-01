package com.xm.crypto_recommendation_service.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

/**
 * The CryptoCurrency annotation is used to validate if a given value is a valid cryptocurrency symbol.
 * It is used in conjunction with the {@link CurrencyValidator} class.
 */
@Documented
@Constraint(validatedBy = CurrencyValidator.class)
@Target( { ElementType.PARAMETER, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface CryptoCurrency {
    String message() default "Invalid crypto currency symbol";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
