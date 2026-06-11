/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

package com.loanhub.repository;

import com.loanhub.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for {@link User} profile records.
 */
@Repository
public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findByEmailIgnoreCase(String email);
}
