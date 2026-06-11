/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

package com.loanhub.service;

import com.loanhub.entity.LoanDocument;
import com.loanhub.repository.LoanDocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service layer for application supporting documents.
 */
@Service
@RequiredArgsConstructor
public class LoanDocumentService {

    private final LoanDocumentRepository repository;

    public List<LoanDocument> getAllDocuments() {
        return repository.findAll();
    }

    public List<LoanDocument> getByApplicationId(String applicationId) {
        return repository.findByApplicationId(applicationId);
    }

    @Transactional
    public LoanDocument create(LoanDocument document) {
        if (document.getId() == null) {
            document.setId("doc_" + System.currentTimeMillis() + "_" + System.nanoTime());
        }
        if (document.getUploadedAt() == null) {
            document.setUploadedAt(LocalDateTime.now());
        }
        if (document.getStatus() == null) {
            document.setStatus("pending");
        }
        return repository.save(document);
    }

    @Transactional
    public Optional<LoanDocument> updateStatus(String id, String status) {
        return repository.findById(id).map(doc -> {
            doc.setStatus(status);
            return repository.save(doc);
        });
    }
}
