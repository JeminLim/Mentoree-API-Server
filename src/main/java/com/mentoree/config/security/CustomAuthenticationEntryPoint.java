package com.mentoree.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mentoree.api.advice.response.ErrorCode;
import com.mentoree.api.advice.response.ErrorResponse;
import com.mentoree.exception.InvalidTokenException;
import lombok.RequiredArgsConstructor;
import org.apache.http.entity.ContentType;
import org.json.simple.JSONObject;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        if(request.getAttribute("Exception") != null) {
            InvalidTokenException exception = (InvalidTokenException) request.getAttribute("Exception");

            if(exception.getErrorCode() == ErrorCode.INVALID_TOKEN) {
                deleteCookies(response, "refresh");
            }

            ErrorResponse errorResponse = ErrorResponse.of(exception.getErrorCode(), exception.getMessage());
            setResponse(response, errorResponse);
        }
        else {
            ErrorResponse denied = ErrorResponse.of(ErrorCode.ACCESS_DENIED, "Access denied");
            setResponse(response, denied);
        }
    }

    private void setResponse(HttpServletResponse response, ErrorResponse errorResponse) throws IOException {
        response.setContentType(ContentType.APPLICATION_JSON.toString());
        response.setStatus(errorResponse.getStatus());

        JSONObject responseJson = new JSONObject();
        responseJson.put("error", errorResponse);
        objectMapper.writeValue(response.getWriter(), errorResponse);
        response.getWriter().println(responseJson);
    }

    private void deleteCookies(HttpServletResponse response, String cookieName) {
        Cookie cookie = new Cookie(cookieName, "");
        cookie.setPath("/");
        cookie.setMaxAge(0);
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
    }
}
