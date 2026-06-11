/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

package com.loanhub.service;

import com.loanhub.dto.AuthResponse;
import com.loanhub.dto.LoginRequest;
import com.loanhub.dto.RegisterRequest;
import com.loanhub.entity.User;
import com.loanhub.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtEncoder jwtEncoder;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        // AuthService takes PasswordEncoder via constructor; inject the real BCrypt encoder.
        ReflectionTestUtils.setField(authService, "passwordEncoder", passwordEncoder);
        ReflectionTestUtils.setField(authService, "expirationMinutes", 60L);
        // Stub the encoder to return a dummy signed token.
        Jwt jwt = Jwt.withTokenValue("signed.jwt.token")
                .header("alg", "HS256")
                .claim("role", "customer")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .subject("x@y.com")
                .build();
        lenient().when(jwtEncoder.encode(any())).thenReturn(jwt);
    }

    @Test
    void register_alwaysCreatesCustomer_andHashesPassword() {
        when(userRepository.findByEmailIgnoreCase("new@x.com")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        AuthResponse res = authService.register(
                new RegisterRequest("New User", "new@x.com", "9999999999", "ABCDE1234F", "secret1"));

        assertThat(res.token()).isEqualTo("signed.jwt.token");
        assertThat(res.user().getRole()).isEqualTo("customer");
        assertThat(res.user().isActive()).isTrue();
        // Password is stored as a BCrypt hash, never in clear text.
        assertThat(res.user().getPasswordHash()).isNotEqualTo("secret1");
        assertThat(passwordEncoder.matches("secret1", res.user().getPasswordHash())).isTrue();
    }

    @Test
    void register_rejectsDuplicateEmail() {
        when(userRepository.findByEmailIgnoreCase("dupe@x.com")).thenReturn(Optional.of(new User()));

        assertThatThrownBy(() -> authService.register(
                new RegisterRequest("Dupe", "dupe@x.com", "1", "PAN", "secret1")))
                .isInstanceOf(ResponseStatusException.class);
    }

    @Test
    void login_succeedsWithCorrectPassword() {
        User user = new User();
        user.setEmail("raj@x.com");
        user.setRole("customer");
        user.setActive(true);
        user.setPasswordHash(passwordEncoder.encode("password123"));
        when(userRepository.findByEmailIgnoreCase("raj@x.com")).thenReturn(Optional.of(user));

        AuthResponse res = authService.login(new LoginRequest("raj@x.com", "password123"));
        assertThat(res.token()).isEqualTo("signed.jwt.token");
    }

    @Test
    void login_rejectsWrongPassword() {
        User user = new User();
        user.setEmail("raj@x.com");
        user.setActive(true);
        user.setPasswordHash(passwordEncoder.encode("password123"));
        when(userRepository.findByEmailIgnoreCase("raj@x.com")).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> authService.login(new LoginRequest("raj@x.com", "wrong")))
                .isInstanceOf(ResponseStatusException.class);
    }

    @Test
    void login_rejectsBlockedAccount() {
        User user = new User();
        user.setEmail("blocked@x.com");
        user.setActive(false);
        user.setPasswordHash(passwordEncoder.encode("password123"));
        when(userRepository.findByEmailIgnoreCase("blocked@x.com")).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> authService.login(new LoginRequest("blocked@x.com", "password123")))
                .isInstanceOf(ResponseStatusException.class);
    }
}
