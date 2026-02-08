package com.mergington.signup.controller;

import com.mergington.signup.service.SignupService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class SignupController {
    private final SignupService signupService;

    public SignupController(SignupService signupService) {
        this.signupService = signupService;
    }

    @PostMapping("/activities/{activityName}/signup")
    public Map<String, String> signup(@PathVariable String activityName, @RequestParam String email) {
        return signupService.signup(activityName, email);
    }
}
