package com.pelinhangisi.springboottodoapp.controller;

import com.pelinhangisi.springboottodoapp.exception.EmailAlreadyExistsException;
import com.pelinhangisi.springboottodoapp.request.UserRegistrationRequest;
import com.pelinhangisi.springboottodoapp.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
class UserRegistrationControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserRegistrationController userRegistrationController;

    @Test
    void registerUser_withNewEmail_redirectsToSuccess() {
        UserRegistrationRequest request = new UserRegistrationRequest(
                "John", "Doe", "john.doe@example.com", "password123");

        String result = userRegistrationController.registerUser(request);

        assertThat(result).isEqualTo("redirect:/registration?success");
    }

    @Test
    void registerUser_withDuplicateEmail_redirectsToError() {
        UserRegistrationRequest request = new UserRegistrationRequest(
                "John", "Doe", "john.doe@example.com", "password123");
        doThrow(new EmailAlreadyExistsException("Email already registered"))
                .when(userService).save(request);

        String result = userRegistrationController.registerUser(request);

        assertThat(result).isEqualTo("redirect:/registration?error");
    }
}
