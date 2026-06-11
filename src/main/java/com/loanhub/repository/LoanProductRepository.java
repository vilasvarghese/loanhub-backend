/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

package com.loanhub.repository;

import com.loanhub.entity.LoanProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for direct database transactional operations supporting LoanProduct.
 */
@Repository
public interface LoanProductRepository extends JpaRepository<LoanProduct, String> {
    
    /**
     * Filter products matching category classifications (e.g. personal, home, business).
     */
    List<LoanProduct> findByType(String type);
}
