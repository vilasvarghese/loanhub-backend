/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

package com.loanhub.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Self-service registration payload. The role is never accepted here — new accounts are
 * always created as customers.
 */
public record RegisterRequest(
        @NotBlank(message = "Name is required") String name,
        @NotBlank(message = "Email is required") @Email(message = "Email must be valid") String email,
        String mobile,
        String pan,
        @NotBlank(message = "Password is required")
        @Size(min = 6, message = "Password must be at least 6 characters") String password) {
}
