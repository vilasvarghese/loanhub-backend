/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

package com.loanhub.service;

import com.loanhub.entity.User;
import com.loanhub.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service layer for user profiles. Registration lives in {@code AuthService}; this service
 * covers reads, profile updates, and the admin block/unblock action. Role changes are never
 * permitted through these paths.
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;

    public List<User> getAllUsers() {
        return repository.findAll();
    }

    public Optional<User> getUserById(String id) {
        return repository.findById(id);
    }

    public Optional<User> getUserByEmail(String email) {
        return repository.findByEmailIgnoreCase(email);
    }

    /**
     * Update mutable profile fields. Role changes are not permitted through this path.
     */
    @Transactional
    public Optional<User> updateProfile(String id, User updates) {
        return repository.findById(id).map(existing -> {
            existing.setName(updates.getName());
            existing.setMobile(updates.getMobile());
            existing.setPan(updates.getPan());
            if (updates.getCreditScore() != null) {
                existing.setCreditScore(updates.getCreditScore());
            }
            existing.setHasCompletedProfile(updates.isHasCompletedProfile());
            return repository.save(existing);
        });
    }

    /**
     * Admin operation: flip the active flag (block/unblock an account).
     */
    @Transactional
    public Optional<User> toggleActive(String id) {
        return repository.findById(id).map(user -> {
            user.setActive(!user.isActive());
            return repository.save(user);
        });
    }
}
