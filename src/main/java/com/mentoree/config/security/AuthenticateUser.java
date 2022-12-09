package com.mentoree.config.security;

import com.mentoree.domain.entity.Member;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

@Getter
@Builder
public class AuthenticateUser implements UserDetails {

    private Long id;
    private String email;
    private String password;
    private Boolean withdrawal;
    private Collection<? extends GrantedAuthority> authorities = new ArrayList<>();

    public static AuthenticateUser of(Member member) {
        List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(member.getRole().getKey()));
        return AuthenticateUser.builder()
                .id(member.getId())
                .email(member.getEmail())
                .password(member.getUserPassword())
                .withdrawal(member.getWithdrawal())
                .authorities(authorities)
                .build();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return !this.withdrawal;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !this.withdrawal;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
