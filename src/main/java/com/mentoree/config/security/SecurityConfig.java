package com.mentoree.config.security;

import com.mentoree.config.utils.AESUtils;
import com.mentoree.config.utils.EncryptUtils;
import com.mentoree.config.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailService customUserDetailService;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final JwtUtils jwtUtils;

    private final String[] NO_AUTH_PATH = {
            "/favicon.ico",
            "/api/login",
            "/api/oauth/login",
            "/api/logout",
            "/api/login/**",
            "/api/reissue",
            "/api/programs/categories",
            "/api/programs/list",
            "/api/members/join",
            "/api/members/join/**",
            "/images/**",
            "/api/login/oauth/google",
            "/api/login/oauth/**"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity security) throws Exception {

        security.httpBasic().disable()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and().cors();

        security.formLogin()
                .loginProcessingUrl("/api/login")
                .usernameParameter("email")
                .passwordParameter("password")
                .successForwardUrl("/api/login/success")
                .permitAll();

        security.oauth2Login()
                .userInfoEndpoint()
                .userService(customUserDetailService);

        security.logout()
                .logoutUrl("/logout")
                .deleteCookies("REFRESHTOKEN");

        security.authorizeRequests()
                .antMatchers(           "/images/**",
                                        "/api/members/join/**",
                                        "/api/programs/list",
                                        "/api/programs/{\\d+}",
                                        "/api/reissue",
                                        "/api/programs/categories",
                                        "/api/login/**",
                                        "/api/login/oauth/**",
                                        "/api/login/oauth/google"
                ).permitAll()
                .anyRequest().authenticated()
                .and().exceptionHandling()
                .authenticationEntryPoint(customAuthenticationEntryPoint);

        security.addFilterAfter(jwtFilter(), LogoutFilter.class);

        return security.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity security) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder
                = security.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.authenticationProvider(new CustomAuthenticationProvider(passwordEncoder(), customUserDetailService));
        return authenticationManagerBuilder.build();
    }

    @Bean
    public JwtFilter jwtFilter() {
        JwtFilter jwtFilter = new JwtFilter(jwtUtils, NO_AUTH_PATH);
        return jwtFilter;
    }

    @Bean
    public EncryptUtils encryptUtils() {
        return new AESUtils();
    }
}
