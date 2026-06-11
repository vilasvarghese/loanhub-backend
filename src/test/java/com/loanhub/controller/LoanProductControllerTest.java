/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

package com.loanhub.controller;

import com.loanhub.entity.LoanProduct;
import com.loanhub.service.LoanProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// Focused MVC slice: security is excluded and filters disabled so we test the controller
// contract directly. Endpoint authorization rules are covered by the security configuration
// and the service-layer tests.
@WebMvcTest(controllers = LoanProductController.class,
        excludeAutoConfiguration = {SecurityAutoConfiguration.class, OAuth2ResourceServerAutoConfiguration.class})
@AutoConfigureMockMvc(addFilters = false)
class LoanProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LoanProductService service;

    private LoanProduct sample() {
        LoanProduct p = new LoanProduct();
        p.setId("prod_1");
        p.setName("Wedding Special Personal Loan");
        p.setType("personal");
        p.setInterestRate(new BigDecimal("10.5"));
        return p;
    }

    @Test
    void getAllProducts_returnsList() throws Exception {
        when(service.getAllProducts()).thenReturn(List.of(sample()));

        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("prod_1"))
                .andExpect(jsonPath("$[0].type").value("personal"));
    }

    @Test
    void getProductById_missing_returns404() throws Exception {
        when(service.getProductById("nope")).thenReturn(Optional.empty());

        mockMvc.perform(get("/products/nope"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateRate_returnsUpdatedProduct() throws Exception {
        LoanProduct updated = sample();
        updated.setInterestRate(new BigDecimal("9.25"));
        when(service.updateInterestRate(eq("prod_1"), eq(new BigDecimal("9.25")))).thenReturn(Optional.of(updated));

        mockMvc.perform(patch("/products/prod_1/rate").param("rate", "9.25"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.interestRate").value(9.25));
    }
}
