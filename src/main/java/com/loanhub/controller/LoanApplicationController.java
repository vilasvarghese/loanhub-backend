/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

package com.loanhub.controller;

import com.loanhub.entity.LoanApplication;
import com.loanhub.service.LoanApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller mapping REST endpoints for client applications operations.
 */
@RestController
@RequestMapping("/applications")
@RequiredArgsConstructor
public class LoanApplicationController {

    private final LoanApplicationService service;

    /**
     * Get list of all submitted applications (Officer and Admin profiles audit flow list).
     */
    @GetMapping
    public ResponseEntity<List<LoanApplication>> getAllApplications() {
        return ResponseEntity.ok(service.getAllApplications());
    }

    /**
     * Get specific applications folder parameters.
     */
    @GetMapping("/{id}")
    public ResponseEntity<LoanApplication> getApplicationById(@PathVariable String id) {
        return service.getApplicationById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    /**
     * Get loan histories linked with specific customer IDs.
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<LoanApplication>> getApplicationsByUserId(@PathVariable String userId) {
        return ResponseEntity.ok(service.getApplicationsByUserId(userId));
    }

    /**
     * File a new application.
     */
    @PostMapping
    public ResponseEntity<LoanApplication> createApplication(@Valid @RequestBody LoanApplication application) {
        LoanApplication created = service.createApplication(application);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Transition milestones (Officer decision logic).
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<LoanApplication> updateStatus(
            @PathVariable String id,
            @RequestParam String status,
            @RequestParam(required = false) String comment) {
        return service.updateApplicationStatus(id, status, comment)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }
}
