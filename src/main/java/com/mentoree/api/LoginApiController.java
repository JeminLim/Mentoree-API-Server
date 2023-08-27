package com.mentoree.api;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.mentoree.api.advice.response.ErrorCode;
import com.mentoree.config.redis.RedisUtils;
import com.mentoree.config.redis.Entity.RefreshToken;
import com.mentoree.config.redis.service.TokenService;
import com.mentoree.config.security.AuthenticateUser;
import com.mentoree.config.security.oauth.ConfigUtils;
import com.mentoree.config.security.oauth.dto.GoogleLoginDto;
import com.mentoree.config.utils.EncryptUtils;
import com.mentoree.config.utils.JwtUtils;
import com.mentoree.exception.InvalidTokenException;
import com.mentoree.exception.NoDataFoundException;
import com.mentoree.service.MemberService;
import com.mentoree.service.dto.LogoutMember;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.ResponseCookie.ResponseCookieBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URI;
import java.net.URISyntaxException;
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
    private final String REFRESH_TOKEN = "refresh";
    @Value("${spring.jwt.refreshToken.validation}")
    private int REFRESH_VALIDATION;
    private final JwtUtils jwtUtils;
    private final EncryptUtils encryptUtils;
    private final MemberService memberService;
    private final TokenService tokenService;
    private final ConfigUtils configUtils;

    @PostMapping("/login/success")
    public ResponseEntity loginSuccess() {
        Map<String, Object> userDetails = extractUserDetails();
        Long memberId = (Long) userDetails.get("id");
        String email = (String) userDetails.get("email");
        String authorities = (String) userDetails.get("authorities");

        Map<String, Object> memberInfo = memberService.login(memberId);

        return createResponse(memberId, email, authorities, memberInfo);
    }

    @PostMapping("/logout")
    public ResponseEntity logout(HttpServletRequest request, HttpServletResponse response) {
        log.info("logout . . . ");
        String refresh = encryptUtils.decrypt(getCookie(request, REFRESH_TOKEN).getValue());
        String[] refreshInfo = refresh.split("\\+");
        String email = refreshInfo[0];


        log.info("get refresh token from redis . . . ");
        tokenService.logout(email);

        // 리프레시 토큰 쿠키 삭제
        log.info("delete refresh cookie . . . ");
        Cookie cookie = new Cookie(REFRESH_TOKEN, null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);
        // 액세스 토큰 블랙리스트화
        log.info("blacklist token . . . ");
        String accessToken = request.getHeader("Authorization").substring(7);
        tokenService.blockToken(accessToken, email);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/login/oauth/google")
    public ResponseEntity<GoogleLoginDto> redirectGoogleLogin(@RequestParam("code") String code){
        try {
            // 프론트로 부터 받은 code를 이용해서 access token refresh token 받아오기
            JSONObject accessToken = getGoogleAccessToken(code);
            // 받은 토큰을 이용해서 사용자 정보 파싱하기

            Map<String, String> userInfo = parsingToken(accessToken);
            String email = userInfo.get("email");
            String name = userInfo.get("name");
            AuthenticateUser loginMember = memberService.duplicateEmailCheck(email)
                    ? memberService.getProfile(email)
                    : memberService.singUp(email, name);

            // id, email, authority 를 가지고 JWT 토큰을 생성
            String authorities = loginMember.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority).collect(Collectors.joining(","));

            // 생성된 jwt 토큰을 프론트에 전달
            Map<String, Object> memberInfo = memberService.login(loginMember.getEmail());
            return createResponse(loginMember.getId(), loginMember.getEmail(), authorities, memberInfo);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return ResponseEntity.badRequest().body(null);
    }

    @PostMapping("/reissue")
    public ResponseEntity reissueAccessToken(HttpServletRequest request) {
        String refresh = encryptUtils.decrypt(getCookie(request, REFRESH_TOKEN).getValue());
        String[] refreshInfo = refresh.split("\\+");

        String email = refreshInfo[0];
        String identifier = refreshInfo[1];
        RefreshToken fromRedis = tokenService.findRefreshToken(email).orElseThrow(NoDataFoundException::new);
        validCheck(fromRedis, identifier, request.getHeader("Authorization"));
        return returnRefreshTokenResponse(fromRedis.getMemberId(), email, fromRedis.getAuthorities());
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
        if(!refreshToken.getIdentifier().equals(identifier)
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
    private ResponseEntity returnRefreshTokenResponse(Long memberId, String email, String authorities) {
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
        RefreshToken refreshToken = new RefreshToken(email, memberId, accessToken, identifier, authorities);
        tokenService.save(refreshToken);
        String encrypt = encryptUtils.encrypt(email.concat("+").concat(identifier));
        ResponseCookie refreshCookie = createCookie(REFRESH_TOKEN, encrypt).build();
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

    private JSONObject getGoogleAccessToken(String code) throws URISyntaxException, ParseException {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", MediaType.APPLICATION_FORM_URLENCODED_VALUE + ";charset=utf-8");

        MultiValueMap<String, String> accessTokenParams = new LinkedMultiValueMap<>();
        accessTokenParams.add("grant_type", "authorization_code");
        accessTokenParams.add("code", code);
        accessTokenParams.add("client_id", configUtils.getGoogleClientId());
        accessTokenParams.add("client_secret", configUtils.getGoogleClientSecret());
        accessTokenParams.add("redirect_uri", configUtils.getGoogleRedirectUri());
        String tokenUri = configUtils.getGoogleTokenUrl();
        HttpEntity<MultiValueMap<String, String>> accessTokenRequest = new HttpEntity<>(accessTokenParams, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> accessTokenResponse = restTemplate.exchange(
                new URI(tokenUri),
                HttpMethod.POST,
                accessTokenRequest,
                String.class
        );

        JSONParser parser = new JSONParser();
        return (JSONObject) parser.parse(accessTokenResponse.getBody());
    }


    private Map<String, String> parsingToken(JSONObject token) {
        String idToken = (String) token.get("id_token");

        DecodedJWT googleDecoded = JWT.decode(idToken);

        String oAuthId = googleDecoded.getSubject();
        String email = googleDecoded.getClaim("email").asString();
        String familyName = googleDecoded.getClaim("family_name").asString();
        String givenName = googleDecoded.getClaim("given_name").asString();

        Map<String, String> result = new HashMap<>();
        result.put("oAuthId", oAuthId);
        result.put("email", email);
        result.put("name", familyName.concat(givenName));
        return result;
    }


}
