/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

package com.loanhub.repository;

import com.loanhub.entity.LoanApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for direct database transactional operations supporting LoanApplication.
 */
@Repository
public interface LoanApplicationRepository extends JpaRepository<LoanApplication, String> {
    
    /**
     * Retrieve all applications submitted by a specific user.
     */
    List<LoanApplication> findByUserId(String userId);

    /**
     * Filter applications matching specific business status.
     */
    List<LoanApplication> findByStatus(String status);
}
