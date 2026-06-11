/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

package com.loanhub.controller;

import com.loanhub.entity.LoanDocument;
import com.loanhub.service.LoanDocumentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST endpoints for application supporting documents.
 */
@RestController
@RequestMapping("/documents")
@RequiredArgsConstructor
public class LoanDocumentController {

    private final LoanDocumentService service;

    @GetMapping
    public ResponseEntity<List<LoanDocument>> getAllDocuments() {
        return ResponseEntity.ok(service.getAllDocuments());
    }

    @GetMapping("/application/{applicationId}")
    public ResponseEntity<List<LoanDocument>> getByApplication(@PathVariable String applicationId) {
        return ResponseEntity.ok(service.getByApplicationId(applicationId));
    }

    @PostMapping
    public ResponseEntity<LoanDocument> create(@Valid @RequestBody LoanDocument document) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(document));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<LoanDocument> updateStatus(
            @PathVariable String id,
            @RequestParam String status) {
        return service.updateStatus(id, status)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }
}
