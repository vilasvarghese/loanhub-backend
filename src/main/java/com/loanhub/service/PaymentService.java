/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

package com.loanhub.service;

import com.loanhub.entity.Payment;
import com.loanhub.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service layer for EMI repayment installments.
 */
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository repository;

    public List<Payment> getAllPayments() {
        return repository.findAll();
    }

    public List<Payment> getByApplicationId(String applicationId) {
        return repository.findByApplicationId(applicationId);
    }

    @Transactional
    public Optional<Payment> markPaid(String id) {
        return repository.findById(id).map(payment -> {
            payment.setStatus("paid");
            return repository.save(payment);
        });
    }
}
