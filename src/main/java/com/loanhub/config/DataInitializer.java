/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

package com.loanhub.config;

import com.loanhub.entity.*;
import com.loanhub.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Seeds demo data (users, products, applications, documents, comments, payments) on first
 * boot so the database mirrors the prototype's original fixtures. Idempotent: seeding is
 * skipped when data already exists. Demo accounts share the password {@code password123}.
 */
@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private static final String DEMO_PASSWORD = "password123";

    private final UserRepository userRepository;
    private final LoanProductRepository productRepository;
    private final LoanApplicationRepository applicationRepository;
    private final LoanDocumentRepository documentRepository;
    private final LoanCommentRepository commentRepository;
    private final PaymentRepository paymentRepository;
    private final PasswordEncoder passwordEncoder;

    @org.springframework.context.annotation.Bean
    CommandLineRunner seed() {
        return args -> {
            if (userRepository.count() > 0) {
                return; // already seeded
            }
            seedUsers();
            seedProducts();
            seedApplications();
            seedDocuments();
            seedComments();
            seedPayments();
        };
    }

    private void seedUsers() {
        userRepository.saveAll(List.of(
                user("user_1", "Raj Nayan", "raj.nayan@scaler.com", "9876543210", "ABCDE1234F",
                        "customer", LocalDateTime.parse("2026-05-01T08:00:00"), true, 780, true),
                user("user_2", "Officer Amit Sharma", "officer@hdfcland.com", "9876543211", "FGHIJ5678K",
                        "officer", LocalDateTime.parse("2026-04-10T11:00:00"), true, null, true),
                user("user_3", "Admin Priya Singh", "admin@hdfcland.com", "9876543212", "LMNOP9012Q",
                        "admin", LocalDateTime.parse("2026-04-01T09:00:00"), true, null, true)
        ));
    }

    private void seedProducts() {
        productRepository.saveAll(List.of(
                product("prod_1", "Wedding Special Personal Loan", "personal", "10.5", 50000, 1500000, 36, "1.5",
                        "Fulfill your dream wedding plans with instant, secure financial support. High tenure options and attractive rates.",
                        List.of("Age: 21-60 years", "Min monthly salary: ₹25,000", "Salaried employees only"),
                        List.of("Aadhaar Card", "PAN Card", "Last 3 Months Salary Slips", "Last 6 Months Bank Statement")),
                product("prod_2", "Medical Emergency Loan", "personal", "11.0", 20000, 500000, 12, "1.0",
                        "Instant disbursement for urgent medical treatments, hospitalization expenses, and pharmacy dues.",
                        List.of("Age: 18-65 years", "Min monthly income: ₹20,000", "Salaried or Self-Employed"),
                        List.of("Aadhaar Card", "PAN Card", "Last 3 Months Salary Slips or IT Return", "Medical Bills/Hospital Quote")),
                product("prod_3", "Dream Home Purchase Loan", "home", "8.4", 1500000, 50000000, 240, "0.5",
                        "Make your family dream come true. Flexible interest rates with easy tracking and tax deductions benefits.",
                        List.of("Age: 21-65 years", "Min monthly income: ₹40,000", "Co-applicant recommended"),
                        List.of("Aadhaar & PAN Card", "Income Tax Returns (2 years)", "Property Sale Agreement", "Property Chain Documents")),
                product("prod_4", "Home Renovation Loan", "home", "8.75", 200000, 2500000, 120, "0.75",
                        "Upgrade your living space, renovate kitchen or restructure the structural elements of your property.",
                        List.of("Age: 21-60 years", "Property ownership proof required", "Stable employment history"),
                        List.of("Aadhaar Card", "Property Tax Receipts", "Renovation Cost Estimate", "Last 6 Months Bank Statement")),
                product("prod_5", "MSME Business Capital Loan", "business", "12.5", 500000, 20000000, 60, "2.0",
                        "Boost your business growth, purchase equipment, replenish inventory, and fulfill immediate supplier payables.",
                        List.of("Business vintage >= 3 years", "Annual turnover >= ₹20 Lakhs", "Positive GST filing trends"),
                        List.of("Aadhaar & PAN of Promoters", "Business Registration Proof", "1 Year GST Filings", "1 Year Bank Statement")),
                product("prod_6", "Startup Growth Funding Loan", "business", "14.0", 200000, 5000000, 36, "2.5",
                        "Unsecured working capital lines specifically configured for growing startup entities with proven business model.",
                        List.of("Business Vintage >= 1 year", "Incubated or Tech-startups preferred", "Co-founder personal guarantee"),
                        List.of("Company PAN Card", "Latest Audited Books", "Pitch Deck / Growth Projections", "Aadhaar of Director")),
                product("prod_7", "Premium Car Purchase Loan", "vehicle", "8.9", 100000, 10000000, 84, "1.0",
                        "Fast approvals and up to 100% on-road funding for premium high-performance civilian and commercial cars.",
                        List.of("Age: 21-65 years", "Min annual income: ₹3,00,000", "Clear CIBIL history"),
                        List.of("Aadhaar & PAN Card", "Car Proforma Invoice", "Last 3 Months Salary Slip or ITR")),
                product("prod_8", "Instant Gold Overdraft Loan", "gold", "7.99", 10000, 2500000, 12, "0.5",
                        "Unlock the value of your gold jewelry instantly with zero foreclosure charges and hourly evaluation credits.",
                        List.of("Age: 18-70 years", "Gold purity: 18-24 Karats", "No income proof required"),
                        List.of("Aadhaar Card", "PAN Card", "Gold evaluation verification receipt (Done in-branch)"))
        ));
    }

    private void seedApplications() {
        LoanApplication app1 = application("app_1", "user_1", "prod_1", 300000, 24, "under_review",
                LocalDateTime.parse("2026-05-20T10:00:00"), LocalDateTime.parse("2026-05-21T14:30:00"),
                "Raj Nayan", "1995-12-15", "ABCDE1234F", "123456789012",
                "Scaler Technologies Inc", "salaried", 125000, 5.0, 15000, 40000, "1209384756", 780);
        app1.setLogs(new java.util.ArrayList<>(List.of(
                new StatusLog("submitted", LocalDateTime.parse("2026-05-20T10:00:00"), "Application received online."),
                new StatusLog("under_review", LocalDateTime.parse("2026-05-21T14:30:00"), "Assigned to Officer Amit Sharma.")
        )));

        LoanApplication app2 = application("app_2", "user_1", "prod_8", 150000, 12, "disbursed",
                LocalDateTime.parse("2026-05-02T10:00:00"), LocalDateTime.parse("2026-05-03T11:00:00"),
                "Raj Nayan", "1995-12-15", "ABCDE1234F", "123456789012",
                "Scaler Technologies Inc", "salaried", 125000, 5.0, 15000, 40000, "1209384756", 780);
        app2.setLogs(new java.util.ArrayList<>(List.of(
                new StatusLog("submitted", LocalDateTime.parse("2026-05-02T10:00:00"), "Gold loan request submitted."),
                new StatusLog("under_review", LocalDateTime.parse("2026-05-02T11:00:00"), "Purity evaluated at 22k."),
                new StatusLog("document_verification", LocalDateTime.parse("2026-05-02T14:00:00"), "Documents verified."),
                new StatusLog("credit_check", LocalDateTime.parse("2026-05-02T16:00:00"), "Credit check verified; low risk score."),
                new StatusLog("approved", LocalDateTime.parse("2026-05-03T09:00:00"), "Loan approved by System."),
                new StatusLog("disbursed", LocalDateTime.parse("2026-05-03T11:00:00"), "Amount credited online to account ending in 56.")
        )));

        applicationRepository.saveAll(List.of(app1, app2));
    }

    private void seedDocuments() {
        documentRepository.saveAll(List.of(
                document("doc_1", "app_1", "raj_nayan_pan.jpg", "pan",
                        "https://images.unsplash.com/photo-1554415707-6e8cfc93fe23?q=80&w=300&auto=format&fit=crop",
                        LocalDateTime.parse("2026-05-20T10:02:00")),
                document("doc_2", "app_1", "raj_nayan_aadhaar.jpg", "aadhaar",
                        "https://images.unsplash.com/photo-1543269865-cbf427effbad?q=80&w=300&auto=format&fit=crop",
                        LocalDateTime.parse("2026-05-20T10:03:00"))
        ));
    }

    private void seedComments() {
        LoanComment c = new LoanComment();
        c.setId("comment_1");
        c.setApplicationId("app_1");
        c.setAuthorName("Officer Amit Sharma");
        c.setAuthorRole("officer");
        c.setText("Income is stable and salary accounts match. Verify Aadhaar photo matching PAN verification.");
        c.setCreatedAt(LocalDateTime.parse("2026-05-21T14:35:00"));
        commentRepository.save(c);
    }

    private void seedPayments() {
        paymentRepository.saveAll(List.of(
                payment("pay_1", "app_2", "13000", "2026-06-01", "paid"),
                payment("pay_2", "app_2", "13000", "2026-07-01", "pending")
        ));
    }

    // ----- builders -------------------------------------------------------

    private User user(String id, String name, String email, String mobile, String pan, String role,
                      LocalDateTime createdAt, boolean active, Integer creditScore, boolean profileComplete) {
        User u = new User();
        u.setId(id);
        u.setName(name);
        u.setEmail(email);
        u.setMobile(mobile);
        u.setPan(pan);
        u.setRole(role);
        u.setCreatedAt(createdAt);
        u.setActive(active);
        u.setCreditScore(creditScore);
        u.setHasCompletedProfile(profileComplete);
        u.setPasswordHash(passwordEncoder.encode(DEMO_PASSWORD));
        return u;
    }

    private LoanProduct product(String id, String name, String type, String interestRate, long minAmount,
                                long maxAmount, int tenure, String processingFee, String description,
                                List<String> eligibility, List<String> documents) {
        LoanProduct p = new LoanProduct();
        p.setId(id);
        p.setName(name);
        p.setType(type);
        p.setInterestRate(new BigDecimal(interestRate));
        p.setMinAmount(BigDecimal.valueOf(minAmount));
        p.setMaxAmount(BigDecimal.valueOf(maxAmount));
        p.setTenure(tenure);
        p.setProcessingFee(new BigDecimal(processingFee));
        p.setDescription(description);
        p.setEligibilityCriteria(new java.util.ArrayList<>(eligibility));
        p.setDocumentsRequired(new java.util.ArrayList<>(documents));
        return p;
    }

    private LoanApplication application(String id, String userId, String productId, long amount, int tenure,
                                        String status, LocalDateTime createdAt, LocalDateTime updatedAt,
                                        String fullName, String dob, String pan, String aadhaar, String companyName,
                                        String employmentType, long salary, double experience, long existingEmi,
                                        long monthlyExpenses, String bankAccount, int creditScore) {
        LoanApplication a = new LoanApplication();
        a.setId(id);
        a.setUserId(userId);
        a.setProductId(productId);
        a.setAmount(BigDecimal.valueOf(amount));
        a.setTenure(tenure);
        a.setStatus(status);
        a.setCreatedAt(createdAt);
        a.setUpdatedAt(updatedAt);
        a.setFullName(fullName);
        a.setDob(dob);
        a.setPan(pan);
        a.setAadhaar(aadhaar);
        a.setCompanyName(companyName);
        a.setEmploymentType(employmentType);
        a.setSalary(BigDecimal.valueOf(salary));
        a.setExperience(experience);
        a.setExistingEmi(BigDecimal.valueOf(existingEmi));
        a.setMonthlyExpenses(BigDecimal.valueOf(monthlyExpenses));
        a.setBankAccount(bankAccount);
        a.setCreditScore(creditScore);
        return a;
    }

    private LoanDocument document(String id, String applicationId, String fileName, String type, String url,
                                  LocalDateTime uploadedAt) {
        LoanDocument d = new LoanDocument();
        d.setId(id);
        d.setApplicationId(applicationId);
        d.setFileName(fileName);
        d.setDocumentType(type);
        d.setFileUrl(url);
        d.setUploadedAt(uploadedAt);
        d.setStatus("pending");
        return d;
    }

    private Payment payment(String id, String applicationId, String emiAmount, String date, String status) {
        Payment p = new Payment();
        p.setId(id);
        p.setApplicationId(applicationId);
        p.setEmiAmount(new BigDecimal(emiAmount));
        p.setPaymentDate(date);
        p.setStatus(status);
        return p;
    }
}
