/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

package com.loanhub;

import com.loanhub.repository.LoanProductRepository;
import com.loanhub.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Smoke test: the application context (including the security filter chain) wires up and the
 * DataInitializer seeds demo fixtures. Runs against in-memory H2 via the {@code test} profile.
 */
@SpringBootTest
@ActiveProfiles("test")
class LoanHubApplicationTests {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LoanProductRepository productRepository;

    @Test
    void contextLoads_andSeedDataIsPresent() {
        assertThat(userRepository.count()).isEqualTo(3);
        assertThat(productRepository.count()).isEqualTo(8);
        assertThat(userRepository.findByEmailIgnoreCase("admin@hdfcland.com")).isPresent();
    }
}
