package com.mentoree.config.redis.service;

import com.mentoree.config.redis.Entity.BlacklistToken;
import com.mentoree.config.redis.Entity.RefreshToken;
import com.mentoree.config.redis.repository.BlacklistTokenRepository;
import com.mentoree.config.redis.repository.RefreshTokenRepository;
import com.mentoree.exception.NoDataFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenService {

    private final BlacklistTokenRepository blacklistTokenRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshToken save(RefreshToken refreshToken) {
        return refreshTokenRepository.save(refreshToken);
    }

    public Optional<RefreshToken> findRefreshToken(String email) {
        return refreshTokenRepository.findByEmail(email);
    }

    public void logout(String email) {
//        RefreshToken logoutRefreshToken = refreshTokenRepository.findByEmail(refreshToken.getEmail())
//                        .orElseThrow(NoDataFoundException::new);
        log.info("email = {}", email);
        Optional<RefreshToken> findFromRedis = refreshTokenRepository.findByEmail(email);
        log.info("find refresh Token is empty ? {}", findFromRedis.isEmpty());
//        refreshTokenRepository.save(logoutRefreshToken.update("", ""));
    }


    public Boolean isBlacklist(String accessToken) {
        return !blacklistTokenRepository.findByBlockedToken(accessToken).isEmpty();
    }

    public void blockToken(String accessToken, String email) {
        BlacklistToken blockedToken = BlacklistToken.builder()
                .blockedToken(accessToken)
                .email(email)
                .build();
        blacklistTokenRepository.save(blockedToken);
    }

}
