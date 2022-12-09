package com.mentoree.config.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.AlgorithmMismatchException;
import com.auth0.jwt.exceptions.InvalidClaimException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.mentoree.api.advice.response.ErrorCode;
import com.mentoree.exception.InvalidTokenException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtUtilsImpl implements InitializingBean, JwtUtils {

    @Value("${spring.jwt.secret-key}")
    private String secretKey;

    @Value("${spring.jwt.accessToken.validation}")
    private int ACCESS_VALID_TIME;

    private Algorithm algorithm;
    private JWTVerifier jwtVerifier;

    @Override
    public void afterPropertiesSet() throws Exception {
        this.algorithm = Algorithm.HMAC256(secretKey);
        this.jwtVerifier = JWT.require(algorithm).acceptLeeway(5).build();
    }

    @Override
    public String generateToken(Long id, String subject, String authorities) {
        return JWT.create()
                .withSubject(subject)
                .withClaim("role", authorities)
                .withClaim("id", id)
                .withExpiresAt(new Date(System.currentTimeMillis() + ACCESS_VALID_TIME))
                .withIssuedAt(new Date(System.currentTimeMillis()))
                .sign(algorithm);
    }

    @Override
    public Map<String, Object> decode(String token) {
        verify(token);
        return parsingInfo(token);
    }

    @Override
    public void verify(String token) {
        try {
            jwtVerifier.verify(token);
        } catch (TokenExpiredException e) {
            throw new TokenExpiredException("만료된 토큰 입니다.");
        } catch (SignatureVerificationException | AlgorithmMismatchException | InvalidClaimException e) {
            throw new InvalidTokenException("잘못된 토큰 입니다.", e, ErrorCode.INVALID_TOKEN);
        }
    }

    private Map<String, Object> parsingInfo(String token) {
        Map<String, Object> extract = new HashMap<>();
        DecodedJWT decodedJWT = JWT.decode(token);
        extract.put("email", decodedJWT.getSubject());
        extract.put("id", decodedJWT.getClaim("id").asLong());
        extract.put("role", decodedJWT.getClaim("role").asString());
        return extract;
    }

}
