package com.mentoree.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final PasswordEncoder passwordEncoder;
    private final CustomUserDetailService customUserDetailService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        String email = (String) authentication.getPrincipal();
        String password = (String) authentication.getCredentials();

        AuthenticateUser authenticateUser = verifyUser(email, password);

        return new UsernamePasswordAuthenticationToken(authenticateUser, null, authentication.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }

    private AuthenticateUser verifyUser(String email, String password) {
        UserDetails fromDbUser = customUserDetailService.loadUserByUsername(email);
        if(!passwordEncoder.matches(password, fromDbUser.getPassword())) {
            throw new BadCredentialsException("Wrong password");
        }
        return (AuthenticateUser) fromDbUser;
    }

}
