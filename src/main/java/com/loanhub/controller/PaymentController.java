/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

package com.loanhub.controller;

import com.loanhub.entity.Payment;
import com.loanhub.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST endpoints for EMI repayment installments.
 */
@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService service;

    @GetMapping
    public ResponseEntity<List<Payment>> getAllPayments() {
        return ResponseEntity.ok(service.getAllPayments());
    }

    @GetMapping("/application/{applicationId}")
    public ResponseEntity<List<Payment>> getByApplication(@PathVariable String applicationId) {
        return ResponseEntity.ok(service.getByApplicationId(applicationId));
    }

    @PatchMapping("/{id}/pay")
    public ResponseEntity<Payment> markPaid(@PathVariable String id) {
        return service.markPaid(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }
}
