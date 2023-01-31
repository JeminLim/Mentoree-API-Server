package com.mentoree.config.security;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.mentoree.api.advice.response.ErrorCode;
import com.mentoree.config.utils.JwtUtils;
import com.mentoree.exception.InvalidTokenException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    private JwtUtils jwtUtils;
    private String[] permitAllList;

    public JwtFilter(JwtUtils jwtUtils, String[] uriList) {
        this.jwtUtils = jwtUtils;
        this.permitAllList = uriList;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            if (!permitAllURICheck(request)) {
                String accessToken = request.getHeader("Authorization").substring(7);
                UsernamePasswordAuthenticationToken authentication = getAuthenticationFromToken(accessToken);

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (TokenExpiredException e) {
            InvalidTokenException exception = new InvalidTokenException("만료된 토큰 입니다", ErrorCode.EXPIRED_TOKEN);
            request.setAttribute("Exception", exception);
            throw exception;
        } catch (InvalidTokenException e) {
            request.setAttribute("Exception", e);
            throw e;
        }

        filterChain.doFilter(request, response);
    }

    private UsernamePasswordAuthenticationToken getAuthenticationFromToken(String accessToken) {
        Map<String, Object> userDetails = jwtUtils.decode(accessToken);
        Long memberId = (Long) userDetails.get("id");
        String email = (String) userDetails.get("email");
        String authString = (String) userDetails.get("role");
        List<SimpleGrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(authString));

        AuthenticateUser authenticateUser = new AuthenticateUser(memberId, email, null, false, authorities);
        UsernamePasswordAuthenticationToken authentication
                = new UsernamePasswordAuthenticationToken(authenticateUser, null, authorities);

        return authentication;
    }

    private boolean permitAllURICheck(HttpServletRequest request){
        AntPathMatcher pathMatcher = new AntPathMatcher();
        String uri = request.getRequestURI();
        for (String path : permitAllList) {
            if(pathMatcher.match(path, uri))
                return true;
        }

        return false;
    }
}
