package com.xm.crypto_recommendation_service.model;

/**
 * The CurrencyStatistics class represents the statistics of a currency, including the minimum, maximum, oldest, and newest price ticks.
 */
public record CurrencyStatistics(PriceTick min, PriceTick max, PriceTick oldest, PriceTick newest) { }
