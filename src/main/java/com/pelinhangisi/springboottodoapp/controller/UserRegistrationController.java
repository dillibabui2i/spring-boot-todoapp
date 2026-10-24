package com.pelinhangisi.springboottodoapp.controller;


import com.pelinhangisi.springboottodoapp.exception.EmailAlreadyExistsException;
import com.pelinhangisi.springboottodoapp.request.UserRegistrationRequest;
import com.pelinhangisi.springboottodoapp.service.UserService;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequestMapping("/registration")
@RequiredArgsConstructor
public class UserRegistrationController {

    private final UserService userService;

    @GetMapping
    public String showRegistration(Model model){
        model.addAttribute("user", new UserRegistrationRequest());
        return "registration";
    }

    @PostMapping
    public String registerUser(@Valid @ModelAttribute("user") UserRegistrationRequest userRegistrationRequest,
                                BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            return "registration";
        }
        try {
            userService.save(userRegistrationRequest);
        } catch (EmailAlreadyExistsException e) {
            return "redirect:/registration?error";
        }
        return "redirect:/registration?success";
    }
}
