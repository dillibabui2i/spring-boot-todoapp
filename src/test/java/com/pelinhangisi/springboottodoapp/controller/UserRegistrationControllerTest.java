package com.pelinhangisi.springboottodoapp.controller;

import com.pelinhangisi.springboottodoapp.exception.EmailAlreadyExistsException;
import com.pelinhangisi.springboottodoapp.request.UserRegistrationRequest;
import com.pelinhangisi.springboottodoapp.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

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
        BindingResult bindingResult = new BeanPropertyBindingResult(request, "user");

        String result = userRegistrationController.registerUser(request, bindingResult);

        assertThat(result).isEqualTo("redirect:/registration?success");
    }

    @Test
    void registerUser_withDuplicateEmail_redirectsToError() {
        UserRegistrationRequest request = new UserRegistrationRequest(
                "John", "Doe", "john.doe@example.com", "password123");
        BindingResult bindingResult = new BeanPropertyBindingResult(request, "user");
        doThrow(new EmailAlreadyExistsException("Email already registered"))
                .when(userService).save(request);

        String result = userRegistrationController.registerUser(request, bindingResult);

        assertThat(result).isEqualTo("redirect:/registration?error");
    }

    @Test
    void registerUser_withBlankRequiredField_rendersRegistrationViewWithoutSaving() {
        UserRegistrationRequest request = new UserRegistrationRequest(
                "", "Doe", "john.doe@example.com", "password123");
        BindingResult bindingResult = new BeanPropertyBindingResult(request, "user");
        bindingResult.rejectValue("firstName", "NotBlank", "must not be blank");

        String result = userRegistrationController.registerUser(request, bindingResult);

        assertThat(result).isEqualTo("registration");
        verify(userService, never()).save(request);
    }

    @Test
    void registerUser_withMalformedEmail_rendersRegistrationViewWithoutSaving() {
        UserRegistrationRequest request = new UserRegistrationRequest(
                "John", "Doe", "not-an-email", "password123");
        BindingResult bindingResult = new BeanPropertyBindingResult(request, "user");
        bindingResult.rejectValue("email", "Email", "must be a well-formed email address");

        String result = userRegistrationController.registerUser(request, bindingResult);

        assertThat(result).isEqualTo("registration");
        verify(userService, never()).save(request);
    }
}
