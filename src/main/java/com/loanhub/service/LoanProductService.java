/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

package com.loanhub.service;

import com.loanhub.entity.LoanProduct;
import com.loanhub.repository.LoanProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Enterprise Service Layer for handling configured interest rates catalogs and marketing offers.
 */
@Service
@RequiredArgsConstructor
public class LoanProductService {

    private final LoanProductRepository repository;

    /**
     * Retrieve all loan items.
     */
    public List<LoanProduct> getAllProducts() {
        return repository.findAll();
    }

    /**
     * Retrieve matching classifications.
     */
    public List<LoanProduct> getProductsByType(String type) {
        return repository.findByType(type);
    }

    /**
     * Fetch products by their unique descriptor ID.
     */
    public Optional<LoanProduct> getProductById(String id) {
        return repository.findById(id);
    }

    /**
     * Insert fresh offerings into databases.
     */
    @Transactional
    public LoanProduct saveProduct(LoanProduct product) {
        if (product.getId() == null) {
            product.setId("prod_" + System.currentTimeMillis());
        }
        return repository.save(product);
    }

    /**
     * Update active base interest rate parameters.
     */
    @Transactional
    public Optional<LoanProduct> updateInterestRate(String id, java.math.BigDecimal newRate) {
        return repository.findById(id).map(prod -> {
            prod.setInterestRate(newRate);
            return repository.save(prod);
        });
    }
}
