package org.example.services;

import lombok.RequiredArgsConstructor;
import org.example.entities.User;
import org.example.repositories.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return org.springframework.security.core.userdetails.User.builder()
            .username(user.getUsername())
            .password(user.getPassword())
            .roles("USER")
            .build();
    }

    @Transactional
    public User registerUser(String username, String email, String password, String authority, String name) {
        if (userRepository.findByUsername(username) != null) {
            throw new IllegalArgumentException("User already exists");
        }
        String encodedPassword = passwordEncoder.encode(password);
        User user = new User(username, email, encodedPassword, authority, name);
        return userRepository.save(user);
    }
}
