/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

package com.loanhub.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * CORS policy as a {@link CorsConfigurationSource} bean so it is applied by the Spring
 * Security filter chain (see {@code SecurityConfig}).
 *
 * <p>Allowed origins are explicit and configurable via {@code app.cors.allowed-origins}
 * (env {@code APP_CORS_ALLOWED_ORIGINS}); the API is never opened to {@code *}. When the
 * frontend is served behind the bundled nginx reverse proxy, requests are same-origin and
 * CORS is not exercised at all.</p>
 */
@Configuration
public class CorsConfig {

    @Value("${app.cors.allowed-origins}")
    private String[] allowedOrigins;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Arrays.asList(allowedOrigins));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
