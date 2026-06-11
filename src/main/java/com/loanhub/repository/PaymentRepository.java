/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

package com.loanhub.repository;

import com.loanhub.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for {@link Payment} installment records.
 */
@Repository
public interface PaymentRepository extends JpaRepository<Payment, String> {

    List<Payment> findByApplicationId(String applicationId);
}
