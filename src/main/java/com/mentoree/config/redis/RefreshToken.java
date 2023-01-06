package com.mentoree.config.redis;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RefreshToken {

    private String tokenId;
    private Long memberId;
    private String authorities;
    private String accessToken;

    public RefreshToken update(String tokenId, String accessToken) {
        this.tokenId = tokenId;
        this.accessToken = accessToken;
        return this;
    }


}
