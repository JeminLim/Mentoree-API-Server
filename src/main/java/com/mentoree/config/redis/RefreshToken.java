package com.mentoree.config.redis;

import lombok.Getter;

@Getter
public class RefreshToken {

    private String tokenId;
    private Long memberId;
    private String authorities;
    private String accessToken;

    public RefreshToken(String tokenId, Long memberId, String authorities, String accessToken) {
        this.tokenId = tokenId;
        this.memberId = memberId;
        this.authorities = authorities;
        this.accessToken =  accessToken;
    }

    public RefreshToken update(String tokenId, String accessToken) {
        this.tokenId = tokenId;
        this.accessToken = accessToken;
        return this;
    }


}
