package com.onebank.onebank.config;

import com.onebank.onebank.auth.security.ApiKeyAuthFilter;
import com.onebank.onebank.auth.security.JwtRequestFilter;
import com.onebank.onebank.auth.service.ApiKeyService;
import com.onebank.onebank.auth.service.UserService;
import com.onebank.onebank.auth.utilities.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserService userService;
    private final ApiKeyService apiKeyService;
    private final JwtUtil jwtUtil;

    @Autowired
    public SecurityConfig(UserService userService, JwtUtil jwtUtil, ApiKeyService apiKeyService) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.apiKeyService = apiKeyService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests()
                .antMatchers("/api/auth/**").permitAll()
                .antMatchers("/api/user/**", "/api/transactions/**",  "/api/payment-account/**").authenticated()
                .antMatchers("/swagger-ui.html").permitAll()
                .antMatchers("/swagger-ui/**").permitAll()
                .antMatchers("/swagger-resources/**").permitAll()
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.addFilterBefore(jwtRequestFilter(), UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(apiKeyAuthFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public JwtRequestFilter jwtRequestFilter() {
        return new JwtRequestFilter(jwtUtil, userService);
    }

    @Bean
    public ApiKeyAuthFilter apiKeyAuthFilter() {
        return new ApiKeyAuthFilter(apiKeyService, userService);
    }
}
