package io.villapms.villapms.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 1) Disable CSRF (so H2 console POSTs aren’t blocked)
                .csrf(csrf -> csrf.disable())

                // 2) Allow frames so that H2 console can render in an iframe
                .headers(headers -> headers.frameOptions(frame -> frame.disable()))

                // 3) Permit H2 console AND all villa‐endpoints without login
                .authorizeHttpRequests(auth -> auth
                        // allow GET/POST to /h2-console and its sub‐URLs
                        .requestMatchers("/h2-console", "/h2-console/**").permitAll()
                        // allow all GET/POST/etc to /villas/** without any authentication
                        .requestMatchers("/villas/**").permitAll()
                        // any other request still requires authentication
                        .anyRequest().authenticated()
                )

                // 4) Keep the default login form active (for any other protected endpoints)
                .formLogin();

        return http.build();
    }
}
