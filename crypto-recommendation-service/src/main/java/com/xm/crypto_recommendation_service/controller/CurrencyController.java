package com.xm.crypto_recommendation_service.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.xm.crypto_recommendation_service.model.CurrencyStatistics;
import com.xm.crypto_recommendation_service.service.CurrencyService;
import com.xm.crypto_recommendation_service.validation.CryptoCurrency;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;

/**
 * The CurrencyController class handles API endpoints related to currency statistics.
 */
@RestController
@AllArgsConstructor
@RequestMapping("/currency")
@Tag(name = "Currency API", description = "Retrieve currency statistics")
public class CurrencyController {

    private final CurrencyService currencyService;

    /**
     * Retrieves currency statistics for a given currency symbol.
     *
     * @param currency The currency symbol. Must be a valid crypto currency symbol.
     * @return The currency statistics object containing the minimum, maximum, oldest, and newest price ticks.
     * @throws IllegalArgumentException if the currency symbol is invalid.
     */
    @Operation(summary = "Find currency statistics by currency symbol")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found currency statistics"),
            @ApiResponse(responseCode = "403", description = "Invalid crypto currency symbol")
    })
    @GetMapping("/{currency}/statistics")
    public CurrencyStatistics getCurrencyStatistics(@PathVariable @CryptoCurrency String currency) {
        return currencyService.findStatistics(currency);
    }

    /**
     * Retrieves a list of currencies sorted in descending order by their normalized range.
     *
     * @return A list of currency symbols sorted by their normalized range in descending order.
     */
    @Operation(summary = "Find currencies sorted descending by normalized range")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found sorted currencies"),
    })
    @GetMapping("/sorted-by-normalized-range")
    public List<String> getCurrenciesSortedByNormalizedRange() {
        return currencyService.getCurrenciesSortedByNormalizedRangeDesc();
    }

    /**
     * Retrieves the currency with the highest normalized range for the specified date.
     *
     * @param date The date for which to find the currency with the highest normalized range.
     * @return The ResponseEntity object with the currency symbol if found, or a ResponseEntity with status 404 if not found.
     */
    @Operation(summary = "Find currency with highest normalized range for selected day")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found currency with highest normalized range for selected day"),
            @ApiResponse(responseCode = "404", description = "No currency with highest normalized range found for selected day")
    })
    @GetMapping("/highest-normalized-range/{date}")
    public ResponseEntity<String> getCurrencyWithHighestNormalizedRangeForDate(@PathVariable LocalDate date) {
        return currencyService.findCurrencyWithHighestNormalizedRangeForDate(date)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

}
