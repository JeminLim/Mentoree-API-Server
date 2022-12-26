package com.mentoree.api;

import com.mentoree.api.advice.response.ErrorCode;
import com.mentoree.config.redis.RedisUtils;
import com.mentoree.config.redis.RefreshToken;
import com.mentoree.config.security.AuthenticateUser;
import com.mentoree.config.utils.EncryptUtils;
import com.mentoree.config.utils.JwtUtils;
import com.mentoree.exception.InvalidTokenException;
import com.mentoree.exception.NoDataFoundException;
import com.mentoree.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseCookie.ResponseCookieBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class LoginApiController {

    private final int RANDOM_LENGTH = 10;
    @Value("${spring.jwt.refreshToken.validation}")
    private int REFRESH_VALIDATION;
    private final JwtUtils jwtUtils;
    private final EncryptUtils encryptUtils;
    private final RedisUtils redisUtils;
    private final MemberService memberService;

    @PostMapping("/login/success")
    public ResponseEntity loginSuccess() {

        Map<String, Object> userDetails = extractUserDetails();
        Long memberId = (Long) userDetails.get("id");
        String email = (String) userDetails.get("email");
        String authorities = (String) userDetails.get("authorities");

        Map<String, Object> memberInfo = memberService.login(memberId);

        return createResponse(memberId, email, authorities, memberInfo);
    }

    @PostMapping("/reissue")
    public ResponseEntity reissueAccessToken(HttpServletRequest request) {
        String refresh = encryptUtils.decrypt(getCookie(request, "refresh").getValue());
        String[] refreshInfo = refresh.split("\\+");

        String email = refreshInfo[0];
        String identifier = refreshInfo[1];
        RefreshToken fromRedis = redisUtils.get(email, RefreshToken.class).orElseThrow(NoDataFoundException::new);
        validCheck(fromRedis, identifier, request.getHeader("Authorization"));
        return createResponse(fromRedis.getMemberId(), email, fromRedis.getAuthorities());
    }

    private Map<String, Object> extractUserDetails() {

        Map<String, Object> result = new HashMap<>();

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(principal instanceof DefaultOAuth2User) {
            Map<String, Object> attributes = ((DefaultOAuth2User) principal).getAttributes();
            String email = (String) attributes.get("email");
            Long memberId = (Long) attributes.get("id");
            String authorities = ((DefaultOAuth2User) principal).getAuthorities().stream()
                            .map(GrantedAuthority::getAuthority).collect(Collectors.joining(","));

            result.put("id", memberId);
            result.put("email", email);
            result.put("authorities", authorities);
            return result;
        }

        Long memberId = ((AuthenticateUser) principal).getId();
        String email = ((AuthenticateUser) principal).getEmail();
        String authorities = ((AuthenticateUser) principal).getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority).collect(Collectors.joining(","));

        result.put("id", memberId);
        result.put("email", email);
        result.put("authorities", authorities);
        return result;
    }
    private Cookie getCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        return Arrays.stream(cookies).filter(c -> c.getName().equals(name))
                .findFirst()
                .orElseThrow(() -> new InvalidTokenException("리프레시 토큰이 없습니다.", ErrorCode.INVALID_TOKEN));
    }
    private void validCheck(RefreshToken refreshToken, String identifier, String accessToken) {
        if(!refreshToken.getTokenId().equals(identifier)
                && !refreshToken.getAccessToken().equals(accessToken))
            throw new InvalidTokenException("부적절한 토큰입니다", ErrorCode.INVALID_TOKEN);
    }
    private ResponseEntity createResponse(Long memberId, String email, String authorities, Map<String, Object> loginInfo) {
        String accessToken = jwtUtils.generateToken(memberId, email, authorities);
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("accessToken", accessToken);
        responseBody.putAll(loginInfo);

        ResponseCookie refreshCookie = generateRefreshToken(memberId, email, authorities, accessToken);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(responseBody);
    }
    private ResponseEntity createResponse(Long memberId, String email, String authorities) {
        String accessToken = jwtUtils.generateToken(memberId, email, authorities);
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("accessToken", accessToken);
        ResponseCookie refreshCookie = generateRefreshToken(memberId, email, authorities, accessToken);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(responseBody);

    }
    private ResponseCookie generateRefreshToken(Long memberId, String email, String authorities, String accessToken) {
        String identifier = RandomStringUtils.randomAlphanumeric(RANDOM_LENGTH);
        RefreshToken refreshToken = new RefreshToken(identifier, memberId, authorities, accessToken);
        redisUtils.save(email, refreshToken);
        String encrypt = encryptUtils.encrypt(email.concat("+").concat(identifier));
        ResponseCookie refreshCookie = createCookie("refresh", encrypt).build();
        return refreshCookie;
    }
    private ResponseCookieBuilder createCookie(String name, String value) {
        ResponseCookieBuilder builder = ResponseCookie.from(name, value);
        builder.maxAge(REFRESH_VALIDATION);
        builder.path("/");
        builder.sameSite("Lax");
        builder.secure(true);
        builder.httpOnly(true);
        return builder;
    }
}
