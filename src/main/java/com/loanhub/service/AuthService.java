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
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

/**
 * Username/password authentication backed by PostgreSQL.
 *
 * <p>Registration always creates a <strong>customer</strong> (the role is never taken from
 * the request), passwords are stored as BCrypt hashes, and login returns a short-lived JWT
 * signed by this service. The role is embedded as a claim so the resource server can
 * authorize requests without a database round-trip.</p>
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private static final String ROLE_CUSTOMER = "customer";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtEncoder jwtEncoder;

    @Value("${app.jwt.expiration-minutes:720}")
    private long expirationMinutes;

    @Transactional
    public AuthResponse register(RegisterRequest req) {
        userRepository.findByEmailIgnoreCase(req.email()).ifPresent(u -> {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "An account with this email already exists.");
        });

        User user = new User();
        user.setId("user_" + UUID.randomUUID());
        user.setName(req.name());
        user.setEmail(req.email());
        user.setMobile(req.mobile());
        user.setPan(req.pan());
        user.setPasswordHash(passwordEncoder.encode(req.password()));
        user.setRole(ROLE_CUSTOMER); // never trust a client-supplied role
        user.setActive(true);
        // Profile is not yet complete: the customer is sent to the profile-setup form
        // (employment + income → credit score) immediately after registering.
        user.setHasCompletedProfile(false);

        User saved = userRepository.save(user);
        return new AuthResponse(issueToken(saved), saved);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest req) {
        User user = userRepository.findByEmailIgnoreCase(req.email())
                .filter(u -> u.getPasswordHash() != null
                        && passwordEncoder.matches(req.password(), u.getPasswordHash()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                        "Invalid email or password."));

        if (!user.isActive()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "This account is currently blocked by an administrator.");
        }
        return new AuthResponse(issueToken(user), user);
    }

    /** Mints an HS256 JWT carrying the user's role and id for stateless authorization. */
    private String issueToken(User user) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("loanhub")
                .issuedAt(now)
                .expiresAt(now.plus(expirationMinutes, ChronoUnit.MINUTES))
                .subject(user.getEmail())
                .claim("uid", user.getId())
                .claim("role", user.getRole())
                .claim("name", user.getName())
                .build();
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        return jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }
}
