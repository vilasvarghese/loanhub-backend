/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

package com.loanhub.controller;

import com.loanhub.entity.LoanProduct;
import com.loanhub.service.LoanProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * Controller mapping REST endpoints for Loan Catalog products.
 */
@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class LoanProductController {

    private final LoanProductService service;

    /**
     * Get all actively published product catalogs.
     */
    @GetMapping
    public ResponseEntity<List<LoanProduct>> getAllProducts() {
        return ResponseEntity.ok(service.getAllProducts());
    }

    /**
     * Get products matching type identifiers (personal, home, business, gold).
     */
    @GetMapping("/type/{type}")
    public ResponseEntity<List<LoanProduct>> getProductsByType(@PathVariable String type) {
        return ResponseEntity.ok(service.getProductsByType(type));
    }

    /**
     * Get matching detailed info.
     */
    @GetMapping("/{id}")
    public ResponseEntity<LoanProduct> getProductById(@PathVariable String id) {
        return service.getProductById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    /**
     * Add new products cards.
     */
    @PostMapping
    public ResponseEntity<LoanProduct> createProduct(@Valid @RequestBody LoanProduct product) {
        LoanProduct created = service.saveProduct(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Update active interest rates p.a. (Admin role operations).
     */
    @PatchMapping("/{id}/rate")
    public ResponseEntity<LoanProduct> updateRate(
            @PathVariable String id,
            @RequestParam BigDecimal rate) {
        return service.updateInterestRate(id, rate)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }
}
