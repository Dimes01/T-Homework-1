package org.example.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.dto.PasswordResetConfirmRequest;
import org.example.dto.PasswordResetRequest;
import org.example.entities.User;
import org.example.dto.LoginRequest;
import org.example.dto.SignUpRequest;
import org.example.exceptions.EntityNotFoundException;
import org.example.repositories.UserRepository;
import org.example.services.CustomUserDetailsService;
import org.example.services.PasswordResetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final CustomUserDetailsService customUserDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final PasswordResetService passwordResetService;
    private final Logger logger = LoggerFactory.getLogger(AuthController.class);


    @PostMapping("/register")
    public ResponseEntity<User> register(@Valid @RequestBody SignUpRequest signUpRequest) {
        logger.info("Endpoint '/register': call");
        if (userRepository.findByUsername(signUpRequest.getUsername()) != null) {
            throw new IllegalArgumentException("User already exists");
        }
        User user = customUserDetailsService.registerUser(
            signUpRequest.getUsername(),
            signUpRequest.getPassword(),
            signUpRequest.getEmail(),
            signUpRequest.getAuthority(),
            signUpRequest.getName());
        logger.info("Endpoint '/register': finish");
        return ResponseEntity.ok(userRepository.save(user));
    }


    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
//        User user = userRepository.findByUsername(loginRequest.getUsername());
//
//        if (user == null) {
//            return ResponseEntity.status(401).body("Unauthorized");
//        }

        return ResponseEntity.ok("User authenticated successfully");
    }


    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        logger.info("Endpoint '/logout': call");
        HttpSession session = request.getSession(false);
        if (session != null) {
            logger.info("Endpoint '/logout': Invalidating session");
            session.invalidate();
        }
        SecurityContextHolder.clearContext();
        logger.info("Endpoint '/logout': finish");
        return ResponseEntity.ok().build();
    }


    @PostMapping("/password-reset")
    public ResponseEntity<?> requestPasswordReset(@Valid @RequestBody PasswordResetRequest request) {
        User user = userRepository.findByUsername(request.getUsername());
        if (user == null) {
            throw new EntityNotFoundException("User not found");
        }
        passwordResetService.sendConfirmationCode(user);
        return ResponseEntity.ok("Confirmation code sent");
    }


    @PostMapping("/password-reset/confirm")
    public ResponseEntity<?> confirmResetPassword(@Valid @RequestBody PasswordResetConfirmRequest request) {
        User user = userRepository.findByUsername(request.getUsername());
        if (user == null) {
            throw new EntityNotFoundException("User not found");
        }

        if (!passwordResetService.validateConfirmationCode(user.getUsername(), request.getConfirmationCode())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid confirmation code");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        passwordResetService.removeConfirmationCode(user.getUsername());

        return ResponseEntity.ok("Password reset successful");
    }
}
