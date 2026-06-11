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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * JPA Entity capturing client application datasets and criteria audits.
 */
@Entity
@Table(name = "loan_applications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoanApplication {

    @Id
    @Column(length = 50)
    private String id;

    @NotBlank(message = "User ID must be specified")
    @Column(name = "user_id", nullable = false, length = 50)
    private String userId;

    @NotBlank(message = "Product ID must be specified")
    @Column(name = "product_id", nullable = false, length = 50)
    private String productId;

    @NotNull(message = "Funding amount must be configured")
    @DecimalMin(value = "0.0", inclusive = false)
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @NotNull(message = "Tenure count must be configured")
    @Column(nullable = false)
    private Integer tenure;

    @NotBlank(message = "Work flow status is required")
    @Column(nullable = false, length = 50)
    private String status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Step 1: Personal Details
    @NotBlank(message = "Full registered name is required")
    @Column(name = "full_name", nullable = false)
    private String fullName;

    @NotBlank(message = "Date of Birth is required")
    @Column(name = "dob", nullable = false, length = 20)
    private String dob;

    @NotBlank(message = "PAN card reference is required")
    @Column(nullable = false, length = 15)
    private String pan;

    @NotBlank(message = "Aadhaar validation index is required")
    @Column(nullable = false, length = 20)
    private String aadhaar;

    // Step 2: Employment Verification
    @NotBlank(message = "Company Name matches required")
    @Column(name = "company_name", nullable = false)
    private String companyName;

    @NotBlank(message = "Employment category is required")
    @Column(name = "employment_type", nullable = false, length = 30)
    private String employmentType;

    @NotNull(message = "Net income is required")
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal salary;

    @NotNull(message = "Verified experience is required")
    @Column(nullable = false)
    private Double experience;

    // Step 3: Liability profiles
    @Column(name = "existing_emi", precision = 15, scale = 2)
    private BigDecimal existingEmi;

    @Column(name = "monthly_expenses", precision = 15, scale = 2)
    private BigDecimal monthlyExpenses;

    @NotBlank(message = "Target routing Account is required")
    @Column(name = "bank_account", nullable = false)
    private String bankAccount;

    @Column(name = "credit_score")
    private Integer creditScore;

    // Workflow audit trail (submitted -> review -> verification -> decision -> disbursed)
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "loan_application_logs", joinColumns = @JoinColumn(name = "application_id"))
    @OrderColumn(name = "log_index")
    private List<StatusLog> logs = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
