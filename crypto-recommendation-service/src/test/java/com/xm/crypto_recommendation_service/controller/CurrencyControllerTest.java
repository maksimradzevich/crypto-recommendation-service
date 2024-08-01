package com.xm.crypto_recommendation_service.controller;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.xm.crypto_recommendation_service.model.CurrencyStatistics;
import com.xm.crypto_recommendation_service.service.CurrencyService;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class CurrencyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper mapper = new ObjectMapper();

    @MockBean
    private CurrencyService currencyService;

    @Test
    void getCurrencyStatistics_validPayload_returnedStatistics() throws Exception {

        CurrencyStatistics expected = new CurrencyStatistics(null, null, null, null);
        when(currencyService.findStatistics("BTC")).thenReturn(expected);

        MvcResult result = mockMvc.perform(get("/currency/BTC/statistics"))
                                  .andExpect(status().isOk())
                                  .andReturn();

        String json = result.getResponse().getContentAsString();
        CurrencyStatistics actual = mapper.readValue(json, CurrencyStatistics.class);

        assertEquals(expected, actual);
    }

    @Test
    void getCurrencyStatistics_invalidPayload_statusBadRequest() throws Exception {
        mockMvc.perform(get("/currency/NOT_A_CURRENCY/statistics"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getCurrenciesSortedByNormalizedRange_returnedSortedCurrencies() throws Exception {
        List<String> expected = List.of("ETH", "BTC", "LTC");

        when(currencyService.getCurrenciesSortedByNormalizedRangeDesc()).thenReturn(expected);

        MvcResult result = mockMvc.perform(get("/currency/sorted-by-normalized-range"))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        List<String> actual = mapper.readValue(json, List.class);

        assertEquals(expected, actual);
    }

    @Test
    void getCurrencyWithHighestNormalizedRangeForDate_returnedCurrencyWithHighestRange() throws Exception {
        when(currencyService.findCurrencyWithHighestNormalizedRangeForDate(LocalDate.of(2022, 1, 1)))
                .thenReturn(Optional.of("BTC"));
        mockMvc.perform(get("/currency/highest-normalized-range/2022-01-01"))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().string(containsString("BTC")));
    }

    @Test
    void getCurrencyStatistics_nothingFound_statusNotFound() throws Exception {
        when(currencyService.findCurrencyWithHighestNormalizedRangeForDate(LocalDate.of(2022, 1, 1)))
                .thenReturn(Optional.empty());
        mockMvc.perform(get("/currency/highest-normalized-range/2022-01-01"))
                .andExpect(status().isNotFound());
    }
}