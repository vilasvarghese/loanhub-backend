/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

package com.loanhub.service;

import com.loanhub.entity.LoanApplication;
import com.loanhub.entity.Payment;
import com.loanhub.repository.LoanApplicationRepository;
import com.loanhub.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoanApplicationServiceTest {

    @Mock
    private LoanApplicationRepository applicationRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private LoanApplicationService service;

    private LoanApplication app;

    @BeforeEach
    void setUp() {
        app = new LoanApplication();
        app.setId("app_test");
        app.setUserId("user_1");
        app.setProductId("prod_1");
        app.setAmount(BigDecimal.valueOf(120000));
        app.setTenure(12);
        app.setSalary(BigDecimal.valueOf(125000));
    }

    @Test
    void createApplication_forcesSubmittedStatusAndOpeningLog() {
        when(applicationRepository.save(any(LoanApplication.class))).thenAnswer(inv -> inv.getArgument(0));

        // Even if a client tries to pre-set 'approved', creation must reset to 'submitted'.
        app.setStatus("approved");
        LoanApplication created = service.createApplication(app);

        assertThat(created.getStatus()).isEqualTo("submitted");
        assertThat(created.getLogs()).hasSize(1);
        assertThat(created.getLogs().get(0).getStatus()).isEqualTo("submitted");
        assertThat(created.getCreditScore()).isEqualTo(820); // salary >= 100k
    }

    @Test
    void updateApplicationStatus_appendsAuditLog() {
        when(applicationRepository.findById("app_test")).thenReturn(Optional.of(app));
        when(applicationRepository.save(any(LoanApplication.class))).thenAnswer(inv -> inv.getArgument(0));

        Optional<LoanApplication> result = service.updateApplicationStatus("app_test", "under_review", "Assigned");

        assertThat(result).isPresent();
        assertThat(result.get().getStatus()).isEqualTo("under_review");
        assertThat(result.get().getLogs()).hasSize(1);
        assertThat(result.get().getLogs().get(0).getComment()).isEqualTo("Assigned");
        verify(paymentRepository, never()).save(any());
    }

    @Test
    void updateApplicationStatus_onDisbursal_generatesEmiSchedule() {
        when(applicationRepository.findById("app_test")).thenReturn(Optional.of(app));
        when(applicationRepository.save(any(LoanApplication.class))).thenAnswer(inv -> inv.getArgument(0));
        when(paymentRepository.findByApplicationId("app_test")).thenReturn(Collections.emptyList());

        service.updateApplicationStatus("app_test", "disbursed", "Funds released");

        ArgumentCaptor<Payment> captor = ArgumentCaptor.forClass(Payment.class);
        verify(paymentRepository, times(3)).save(captor.capture());
        List<Payment> generated = captor.getAllValues();
        // 120000 / 12 = 10000 per installment
        assertThat(generated).allSatisfy(p -> {
            assertThat(p.getApplicationId()).isEqualTo("app_test");
            assertThat(p.getEmiAmount()).isEqualByComparingTo("10000");
            assertThat(p.getStatus()).isEqualTo("pending");
        });
    }

    @Test
    void updateApplicationStatus_onDisbursal_doesNotDuplicateExistingSchedule() {
        when(applicationRepository.findById("app_test")).thenReturn(Optional.of(app));
        when(applicationRepository.save(any(LoanApplication.class))).thenAnswer(inv -> inv.getArgument(0));
        when(paymentRepository.findByApplicationId("app_test")).thenReturn(List.of(new Payment()));

        service.updateApplicationStatus("app_test", "disbursed", null);

        verify(paymentRepository, never()).save(any());
    }

    @Test
    void updateApplicationStatus_unknownId_returnsEmpty() {
        when(applicationRepository.findById("missing")).thenReturn(Optional.empty());

        assertThat(service.updateApplicationStatus("missing", "approved", null)).isEmpty();
    }
}
