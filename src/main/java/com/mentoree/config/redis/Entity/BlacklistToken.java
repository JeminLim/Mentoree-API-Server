package com.mentoree.config.redis.Entity;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;


@Getter
@RedisHash(value = "blacklist", timeToLive = 2592000L)
public class BlacklistToken {

    @Id
    private String blockedToken;
    private final String email;

    @Builder
    public BlacklistToken(String blockedToken, String email) {
        this.blockedToken = blockedToken;
        this.email = email;
    }

}
