package com.mentoree.config.security.oauth.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OAuthInfo {

    private String code;
    private String provider;

}
