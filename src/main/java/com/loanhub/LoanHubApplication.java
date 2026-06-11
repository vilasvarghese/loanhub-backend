/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

package com.loanhub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Enterprise Application Main Entrypoint for LoanHub.
 */
@SpringBootApplication
public class LoanHubApplication {
    public static void main(String[] args) {
        SpringApplication.run(LoanHubApplication.class, args);
    }
}
