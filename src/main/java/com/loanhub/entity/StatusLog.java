/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

package com.loanhub.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Embeddable workflow milestone entry stored against a {@link LoanApplication}.
 */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StatusLog {

    @Column(name = "status", length = 50)
    private String status;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "comment", length = 1000)
    private String comment;
}
