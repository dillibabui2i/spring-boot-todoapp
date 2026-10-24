package com.pelinhangisi.springboottodoapp.service;

import com.pelinhangisi.springboottodoapp.dao.UserRepository;
import com.pelinhangisi.springboottodoapp.exception.EmailAlreadyExistsException;
import com.pelinhangisi.springboottodoapp.models.User;
import com.pelinhangisi.springboottodoapp.request.UserRegistrationRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void save_withNewEmail_persistsUser() {
        UserRegistrationRequest request = new UserRegistrationRequest(
                "John", "Doe", "john.doe@example.com", "password123");
        when(passwordEncoder.encode("password123")).thenReturn("encoded-password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User savedUser = userService.save(request);

        assertThat(savedUser.getEmail()).isEqualTo("john.doe@example.com");
        assertThat(savedUser.getPassword()).isEqualTo("encoded-password");
    }

    @Test
    void save_withDuplicateEmail_throwsEmailAlreadyExistsException() {
        UserRegistrationRequest request = new UserRegistrationRequest(
                "John", "Doe", "john.doe@example.com", "password123");
        when(passwordEncoder.encode("password123")).thenReturn("encoded-password");
        when(userRepository.save(any(User.class)))
                .thenThrow(new DataIntegrityViolationException("unique constraint violation"));

        assertThatThrownBy(() -> userService.save(request))
                .isInstanceOf(EmailAlreadyExistsException.class);
    }
}
