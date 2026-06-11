/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

package com.loanhub.dto;

import jakarta.validation.constraints.NotBlank;

/** Email/password login payload. */
public record LoginRequest(
        @NotBlank(message = "Email is required") String email,
        @NotBlank(message = "Password is required") String password) {
}
