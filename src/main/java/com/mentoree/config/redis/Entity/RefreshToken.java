package com.mentoree.config.redis.Entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@RedisHash("refreshToken")
public class RefreshToken {

    @Id
    private String email;
    private Long memberId;
    private String accessToken;
    private String identifier;
    private String authorities;

    public RefreshToken update(String identifier, String accessToken) {
        this.identifier = identifier;
        this.accessToken = accessToken;
        return this;
    }


}
