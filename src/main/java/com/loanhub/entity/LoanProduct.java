/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

package com.loanhub.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * JPA Entity capturing general Loan product details.
 */
@Entity
@Table(name = "loan_products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoanProduct {

    @Id
    @Column(length = 50)
    private String id;

    @NotBlank(message = "Product marketing title is required")
    @Column(nullable = false, unique = true)
    private String name;

    @NotBlank(message = "Loan type classification is required")
    @Column(nullable = false, length = 30)
    private String type;

    @NotNull(message = "Interest Rate per annum is required")
    @DecimalMin("0.0")
    @Column(name = "interest_rate", nullable = false, precision = 5, scale = 2)
    private BigDecimal interestRate;

    @NotNull(message = "Minimum grantable amount is required")
    @Column(name = "min_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal minAmount;

    @NotNull(message = "Maximum grantable cap amount is required")
    @Column(name = "max_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal maxAmount;

    @NotNull(message = "Target Tenure is required")
    @Column(nullable = false)
    private Integer tenure;

    @NotNull(message = "Processing fee percentage is required")
    @Column(name = "processing_fee", nullable = false, precision = 5, scale = 2)
    private BigDecimal processingFee;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "loan_product_eligibility", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "criterion", length = 255)
    private List<String> eligibilityCriteria = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "loan_product_documents", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "document_name", length = 255)
    private List<String> documentsRequired = new ArrayList<>();
}
