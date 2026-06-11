/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

package com.loanhub.repository;

import com.loanhub.entity.LoanDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for {@link LoanDocument} records.
 */
@Repository
public interface LoanDocumentRepository extends JpaRepository<LoanDocument, String> {

    List<LoanDocument> findByApplicationId(String applicationId);
}
