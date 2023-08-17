package com.mentoree.config.security.oauth;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@Component
public class ConfigUtils {

    @Value("${oauth2.google.token.url}")
    private String googleTokenUrl;

    @Value("${oauth2.google.login.url}")
    private String googleLoginUrl;

    @Value("${oauth2.google.redirect.uri}")
    private String googleRedirectUri;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String googleClientSecret;


    public String googleInitUrl() {
        Map<String, Object> params = new HashMap<>();
        params.put("client_id", googleClientId);
        params.put("redirect_uri", googleRedirectUri);
        params.put("response_type", "code");
        params.put("scope", "email%20profile");

        String paramStr = params.entrySet().stream().map(param -> param.getKey() + "=" + param.getValue())
                .collect(Collectors.joining("&"));

        return googleLoginUrl + "/o/oauth2/v2/auth?" + paramStr;
    }
}
