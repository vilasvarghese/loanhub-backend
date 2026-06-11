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
 * JPA Entity for review discussion logs on a loan application.
 */
@Entity
@Table(name = "loan_comments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoanComment {

    @Id
    @Column(length = 64)
    private String id;

    @NotBlank(message = "Application reference is required")
    @Column(name = "application_id", nullable = false, length = 50)
    private String applicationId;

    @NotBlank(message = "Author name is required")
    @Column(name = "author_name", nullable = false)
    private String authorName;

    @Column(name = "author_role", length = 20)
    private String authorRole;

    @NotBlank(message = "Comment text is required")
    @Column(nullable = false, length = 2000)
    private String text;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }
}
