package org.example.controllers;

import lombok.RequiredArgsConstructor;
import org.example.entities.User;
import org.example.models.RegistrationForm;
import org.example.repositories.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody RegistrationForm registrationForm) {
        User user = registrationForm.createUser(passwordEncoder, "ROLE_USER");
        userRepository.save(user);
        return ResponseEntity.ok(user);
    }
}
