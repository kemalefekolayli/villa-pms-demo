// src/main/java/io/villapms/villapms/auth/config/SecurityConfig.java
package io.villapms.villapms.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authz -> authz
                        // Public endpoints
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/health", "/api/version").permitAll()
                        .requestMatchers("/api/users/register").permitAll()

                        // MVP: Allow property and booking endpoints for now
                        .requestMatchers("/properties/**").permitAll()
                        .requestMatchers("/api/homes/**").permitAll()
                        .requestMatchers("/api/reservations/**").permitAll()

                        // Require auth for everything else
                        .anyRequest().authenticated()
                );

        return http.build();
    }
}