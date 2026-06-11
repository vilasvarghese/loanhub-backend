/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

package com.loanhub.service;

import com.loanhub.entity.LoanComment;
import com.loanhub.repository.LoanCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service layer for review discussion comments.
 */
@Service
@RequiredArgsConstructor
public class LoanCommentService {

    private final LoanCommentRepository repository;

    public List<LoanComment> getAllComments() {
        return repository.findAll();
    }

    public List<LoanComment> getByApplicationId(String applicationId) {
        return repository.findByApplicationId(applicationId);
    }

    @Transactional
    public LoanComment create(LoanComment comment) {
        if (comment.getId() == null) {
            comment.setId("comment_" + System.currentTimeMillis() + "_" + System.nanoTime());
        }
        if (comment.getCreatedAt() == null) {
            comment.setCreatedAt(LocalDateTime.now());
        }
        return repository.save(comment);
    }
}
