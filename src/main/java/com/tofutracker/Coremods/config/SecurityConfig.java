package com.tofutracker.Coremods.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tofutracker.Coremods.dto.LoginRequest;
import com.tofutracker.Coremods.entity.User;
import com.tofutracker.Coremods.services.CustomUserDetailsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.io.IOException;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public SecurityConfig(@Lazy CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityContextRepository securityContextRepository() {
        return new HttpSessionSecurityContextRepository();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationManager authenticationManager) throws Exception {
        http
            .authorizeHttpRequests(authz -> authz
                .requestMatchers(
                    "/auth/register",
                    "/auth/login", 
                    "/auth/verify-email",
                    "/auth/resend-verification",
                    "/auth/forgot-password",
                    "/auth/reset-password",
                    "/auth/me",
                    "/error"
                ).permitAll()
                .anyRequest().authenticated()
            )
            .addFilterBefore(jsonAuthenticationFilter(authenticationManager),
                    org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class)
            .logout(logout -> logout
                .logoutUrl("/auth/logout")
                .logoutSuccessHandler((req, res, auth) -> {
                    res.setStatus(HttpServletResponse.SC_OK);
                    res.setContentType("application/json");
                    res.getWriter().write("{\"success\":true,\"message\":\"Logout successful\"}");
                })
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
            )
            .sessionManagement(session -> session
                .sessionFixation().changeSessionId()
                .maximumSessions(1)
                .maxSessionsPreventsLogin(false)
            )
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/auth/**")
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
            )
            .securityContext(context -> context
                .securityContextRepository(securityContextRepository())
            )
            .authenticationProvider(authenticationProvider());

        return http.build();
    }

    @Bean
    public JsonAuthenticationFilter jsonAuthenticationFilter(AuthenticationManager authenticationManager) {
        JsonAuthenticationFilter filter = new JsonAuthenticationFilter();
        filter.setAuthenticationManager(authenticationManager);
        filter.setAuthenticationSuccessHandler(authenticationSuccessHandler());
        filter.setAuthenticationFailureHandler(authenticationFailureHandler());
        filter.setSecurityContextRepository(securityContextRepository());
        return filter;
    }

    public class JsonAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

        public JsonAuthenticationFilter() {
            super(new AntPathRequestMatcher("/auth/login", "POST"));
        }

        @Override
        public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) 
                throws AuthenticationException {
            try {
                LoginRequest loginRequest = objectMapper.readValue(request.getInputStream(), LoginRequest.class);
                UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsernameOrEmail(), loginRequest.getPassword());
                return getAuthenticationManager().authenticate(authRequest);
            } catch (IOException e) {
                throw new RuntimeException("Failed to parse authentication request body", e);
            }
        }

        @Override
        protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                jakarta.servlet.FilterChain chain, Authentication authResult) throws IOException, jakarta.servlet.ServletException {
            
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authResult);
            SecurityContextHolder.setContext(context);
            securityContextRepository().saveContext(context, request, response);
            getSuccessHandler().onAuthenticationSuccess(request, response, authResult);
        }
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return (request, response, authentication) -> {
            request.getSession(true);
            
            User user = (User) authentication.getPrincipal();
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("application/json");
            
            if (!user.isEmailVerified()) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.getWriter().write(String.format(
                    "{\"message\":\"Email not verified. Please check your email for verification link.\"}"));
            } else {
                response.getWriter().write("{\"message\":\"Login successful\"}");
            }
        };
    }

    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler() {
        return (request, response, exception) -> {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"message\":\"" + exception.getMessage() + "\"}");
        };
    }
}