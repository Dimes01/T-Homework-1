package org.example.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.entities.User;
import org.springframework.security.crypto.password.PasswordEncoder;

@Data
@AllArgsConstructor
public class RegistrationForm {
    private String username;
    private String password;

    public User createUser(PasswordEncoder passwordEncoder, String role) {
        return User.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .authority(role)
                .build();
    }
}
