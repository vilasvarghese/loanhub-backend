/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

package com.loanhub.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * JPA Entity for an EMI repayment installment in a loan's schedule.
 */
@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    @Id
    @Column(length = 64)
    private String id;

    @NotBlank(message = "Application reference is required")
    @Column(name = "application_id", nullable = false, length = 50)
    private String applicationId;

    @NotNull(message = "EMI amount is required")
    @Column(name = "emi_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal emiAmount;

    /** ISO date (yyyy-MM-dd) of the installment. */
    @Column(name = "payment_date", length = 20)
    private String paymentDate;

    /** paid | pending | overdue */
    @Column(nullable = false, length = 20)
    private String status = "pending";
}
