package com.mentoree.api.mock;

import com.mentoree.config.security.AuthenticateUser;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.Collections;


public class WithMockCustomUserSecurityContextFactory implements WithSecurityContextFactory<WithMockCustomUser> {
    @Override
    public SecurityContext createSecurityContext(WithMockCustomUser customUser) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        AuthenticateUser principal = AuthenticateUser.builder()
                .id(customUser.id()).email(customUser.email())
                .withdrawal(false).password("1234QWer!@")
                .authorities(Collections.singletonList(new SimpleGrantedAuthority(customUser.role()))).build();
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        context.setAuthentication(authentication);
        return context;
    }
}
