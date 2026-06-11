/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

package com.loanhub.dto;

import com.loanhub.entity.User;

/** Returned on successful login/registration: the signed JWT plus the user profile. */
public record AuthResponse(String token, User user) {
}
