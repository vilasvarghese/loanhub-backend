/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

package com.loanhub.controller;

import com.loanhub.entity.LoanComment;
import com.loanhub.service.LoanCommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST endpoints for review discussion comments.
 */
@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class LoanCommentController {

    private final LoanCommentService service;

    @GetMapping
    public ResponseEntity<List<LoanComment>> getAllComments() {
        return ResponseEntity.ok(service.getAllComments());
    }

    @GetMapping("/application/{applicationId}")
    public ResponseEntity<List<LoanComment>> getByApplication(@PathVariable String applicationId) {
        return ResponseEntity.ok(service.getByApplicationId(applicationId));
    }

    @PostMapping
    public ResponseEntity<LoanComment> create(@Valid @RequestBody LoanComment comment) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(comment));
    }
}
