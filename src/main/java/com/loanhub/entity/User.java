/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

package com.loanhub.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * JPA Entity for registered user accounts and profiles.
 *
 * <p>Credentials are verified by this backend: {@link #passwordHash} holds a BCrypt hash and
 * is never serialized to clients. The {@code id} is a server-generated identifier.</p>
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @Column(length = 64)
    private String id;

    @NotBlank(message = "Name is required")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Column(nullable = false, unique = true)
    private String email;

    /** BCrypt password hash. Write-only: never exposed in API responses. */
    @JsonIgnore
    @Column(name = "password_hash")
    private String passwordHash;

    @Column(length = 20)
    private String mobile;

    @Column(length = 15)
    private String pan;

    /** One of: customer, officer, admin. Never trusted from the client for privileged roles. */
    @NotBlank(message = "Role is required")
    @Column(nullable = false, length = 20)
    private String role;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // Serialized as "isActive" to match the front-end contract (Lombok's isActive() getter
    // would otherwise expose it as "active").
    @JsonProperty("isActive")
    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    @Column(name = "credit_score")
    private Integer creditScore;

    @Column(name = "has_completed_profile", nullable = false)
    private boolean hasCompletedProfile = false;

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }
}
