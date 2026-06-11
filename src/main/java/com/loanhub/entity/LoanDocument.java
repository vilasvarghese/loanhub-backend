/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

package com.loanhub.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * JPA Entity for supporting documents uploaded against a loan application.
 */
@Entity
@Table(name = "loan_documents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoanDocument {

    @Id
    @Column(length = 64)
    private String id;

    @NotBlank(message = "Application reference is required")
    @Column(name = "application_id", nullable = false, length = 50)
    private String applicationId;

    @NotBlank(message = "File name is required")
    @Column(name = "file_name", nullable = false)
    private String fileName;

    @NotBlank(message = "Document type is required")
    @Column(name = "document_type", nullable = false, length = 40)
    private String documentType;

    @Column(name = "file_url", columnDefinition = "TEXT")
    private String fileUrl;

    @Column(name = "uploaded_at", nullable = false)
    private LocalDateTime uploadedAt;

    /** pending | verified | rejected */
    @Column(nullable = false, length = 20)
    private String status = "pending";

    @Column(length = 1000)
    private String comment;

    @PrePersist
    protected void onCreate() {
        if (this.uploadedAt == null) {
            this.uploadedAt = LocalDateTime.now();
        }
    }
}
