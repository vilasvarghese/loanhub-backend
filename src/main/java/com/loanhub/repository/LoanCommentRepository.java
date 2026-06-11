/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

package com.loanhub.repository;

import com.loanhub.entity.LoanComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for {@link LoanComment} records.
 */
@Repository
public interface LoanCommentRepository extends JpaRepository<LoanComment, String> {

    List<LoanComment> findByApplicationId(String applicationId);
}
