/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

package com.loanhub.service;

import com.loanhub.entity.LoanApplication;
import com.loanhub.entity.Payment;
import com.loanhub.entity.StatusLog;
import com.loanhub.repository.LoanApplicationRepository;
import com.loanhub.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Enterprise Service Layer for handling Loan applications, risk parameters checks, and status audits.
 *
 * <p>This service owns the application workflow: it appends an audit log entry on every
 * status transition and, on disbursal, generates the initial EMI repayment schedule.</p>
 */
@Service
@RequiredArgsConstructor
public class LoanApplicationService {

    private static final String STATUS_SUBMITTED = "submitted";
    private static final String STATUS_DISBURSED = "disbursed";
    private static final int GENERATED_INSTALLMENTS = 3;

    private final LoanApplicationRepository repository;
    private final PaymentRepository paymentRepository;

    public List<LoanApplication> getAllApplications() {
        return repository.findAll();
    }

    public Optional<LoanApplication> getApplicationById(String id) {
        return repository.findById(id);
    }

    public List<LoanApplication> getApplicationsByUserId(String userId) {
        return repository.findByUserId(userId);
    }

    /**
     * Submit/Register a fresh loan file. The status is forced to {@code submitted} and an
     * opening audit log entry is recorded server-side.
     */
    @Transactional
    public LoanApplication createApplication(LoanApplication application) {
        LocalDateTime now = LocalDateTime.now();
        if (application.getId() == null) {
            application.setId("app_" + System.currentTimeMillis());
        }
        application.setStatus(STATUS_SUBMITTED);
        application.setCreatedAt(now);
        application.setUpdatedAt(now);
        if (application.getCreditScore() == null) {
            application.setCreditScore(deriveCreditScore(application.getSalary()));
        }
        application.getLogs().add(new StatusLog(STATUS_SUBMITTED, now, "Application digital folder filed."));
        return repository.save(application);
    }

    /**
     * Update status milestones. Appends an audit log entry and, on disbursal, generates the
     * initial EMI schedule for the application.
     *
     * @param comment optional note recorded against the audit log entry
     */
    @Transactional
    public Optional<LoanApplication> updateApplicationStatus(String id, String status, String comment) {
        return repository.findById(id).map(app -> {
            LocalDateTime now = LocalDateTime.now();
            app.setStatus(status);
            app.setUpdatedAt(now);
            app.getLogs().add(new StatusLog(status, now, comment));
            LoanApplication saved = repository.save(app);

            if (STATUS_DISBURSED.equalsIgnoreCase(status) && paymentRepository.findByApplicationId(id).isEmpty()) {
                generateSchedule(saved);
            }
            return saved;
        });
    }

    /**
     * Derive a rough indicative credit score from monthly salary. Mirrors the prototype's
     * client-side heuristic so seeded and freshly-submitted applications stay consistent.
     */
    private Integer deriveCreditScore(BigDecimal salary) {
        if (salary == null) {
            return 680;
        }
        if (salary.compareTo(BigDecimal.valueOf(100000)) >= 0) {
            return 820;
        }
        if (salary.compareTo(BigDecimal.valueOf(60000)) >= 0) {
            return 760;
        }
        return 680;
    }

    private void generateSchedule(LoanApplication app) {
        if (app.getTenure() == null || app.getTenure() == 0 || app.getAmount() == null) {
            return;
        }
        BigDecimal emi = app.getAmount()
                .divide(BigDecimal.valueOf(app.getTenure()), 0, RoundingMode.HALF_UP);
        LocalDate base = LocalDate.now();
        for (int i = 0; i < GENERATED_INSTALLMENTS; i++) {
            Payment payment = new Payment();
            payment.setId("pay_" + System.currentTimeMillis() + "_" + i);
            payment.setApplicationId(app.getId());
            payment.setEmiAmount(emi);
            payment.setPaymentDate(base.plusMonths(i + 1L).toString());
            payment.setStatus("pending");
            paymentRepository.save(payment);
        }
    }
}
