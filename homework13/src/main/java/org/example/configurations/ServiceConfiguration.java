package org.example.configurations;

import lombok.RequiredArgsConstructor;
import org.example.services.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;

@Configuration
@RequiredArgsConstructor
public class ServiceConfiguration {

    private final CustomUserDetailsService customUserDetailsService;

    @Bean
    public CustomUserDetailsService userDetailsService() {
        return customUserDetailsService;
    }
}
