/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

package com.loanhub.service;

import com.loanhub.entity.User;
import com.loanhub.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository repository;

    @InjectMocks
    private UserService service;

    @Test
    void toggleActive_flipsFlag() {
        User user = new User();
        user.setId("user_1");
        user.setActive(true);
        when(repository.findById("user_1")).thenReturn(Optional.of(user));
        when(repository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        Optional<User> result = service.toggleActive("user_1");

        assertThat(result).isPresent();
        assertThat(result.get().isActive()).isFalse();
    }

    @Test
    void updateProfile_doesNotChangeRole() {
        User existing = new User();
        existing.setId("user_1");
        existing.setRole("customer");
        existing.setName("Old Name");
        when(repository.findById("user_1")).thenReturn(Optional.of(existing));
        when(repository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User updates = new User();
        updates.setName("New Name");
        updates.setRole("admin"); // attempt to elevate via profile update — must be ignored

        Optional<User> result = service.updateProfile("user_1", updates);

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("New Name");
        assertThat(result.get().getRole()).isEqualTo("customer");
    }
}
