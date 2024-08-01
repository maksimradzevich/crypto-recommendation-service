package com.xm.crypto_recommendation_service.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * The PriceTick class represents a price tick, including the time, currency, and price.
 */
public record PriceTick(LocalDateTime time, String currency, BigDecimal price){}
