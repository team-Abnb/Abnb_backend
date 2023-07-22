package com.sparta.abnb.util;

import com.sparta.abnb.entity.User;
import com.sparta.abnb.repository.UserRepository;
import com.sparta.abnb.role.UserRole;
import com.sparta.abnb.service.RedisService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtil {
    private UserRepository userRepository;
    private RedisService redisService;
    private RedisTemplate<String, String> redisTemplate;

    public  final String HEADER_ACCESS_TOKEN = "AccessToken";
    private final String BEARER = "Bearer ";
    private final Long ACCESS_TOKEN_EXPIRATION_TIME = 60 * 60 * 1000L; // 1시간
    private final Long REFRESSH_TOKEN_EXPIRATION_TIME = 14 * 24 * 60 * 60 * 1000L; // 2주
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
    private static Date date;

    @Value("${jwt.secret.key}") // Base64 Encode 한 SecretKey
    private String secretKey;

    // JWT AccessToken 생성 메서드
    public String createAccessToken(Long id, String username, UserRole userRole) {
        return BEARER +
                Jwts.builder()
                        .setSubject(String.valueOf(id)) // 토큰(사용자) 식별자 값
                        .claim("username", username)
                        .claim("userRole", userRole)
                        .setExpiration(new Date(date.getTime() + ACCESS_TOKEN_EXPIRATION_TIME)) // 만료일
                        .setIssuedAt(date) // 발급일
                        .signWith(signatureAlgorithm, secretKey) // 암호화 알고리즘, 시크릿 키
                        .compact();
    }

    // JWT RefreshToken 생성 메서드
    public String createRefreshToken(Long id) {
        return BEARER +
                Jwts.builder()
                        .setSubject(String.valueOf(id)) // 사용자 식별자값(ID)
                        .setExpiration(new Date(date.getTime() + REFRESSH_TOKEN_EXPIRATION_TIME)) // 만료 시간
                        .setIssuedAt(date) // 발급일
                        .signWith(signatureAlgorithm, secretKey) // 암호화 알고리즘
                        .compact();
    }

    // JWT Bearer Substirng 메서드
    public String substringToken(String token) {
        if (StringUtils.hasText(token) && token.startsWith("Bearer")) {
            return token.substring(7);
        }
        throw new NullPointerException("토큰의 값이 존재하지 않습니다.");
    }

    // JWT 토큰의 사용자 정보 가져오는 메서드
    public Claims getUserInfo(String tokenValue) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(tokenValue)
                .getBody();
    }

    // JWT 검증 메서드
    public boolean validateAccessToken(String accessToken) {
        try {
            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(accessToken); // key로 accessToken 검증
            return true;
        } catch (SecurityException | MalformedJwtException | SignatureException e) {
            log.error("Invalid JWT signature, 유효하지 않는 JWT 서명 입니다.");
        } catch (ExpiredJwtException e) {
            log.error("Expired JWT accessToken, 만료된 JWT accessToken 입니다.");
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT accessToken, 지원되지 않는 JWT 토큰 입니다.");
        } catch (IllegalArgumentException e) {
            log.error("JWT claims is empty, 잘못된 JWT 토큰 입니다.");
        }
        return false;
    }

    // AccessToken, RefreshToken 검증 메서드
    public boolean validateRegenerate(String accessToken, String refreshToken) {
        // refreshToken이 없을 경우
        if (accessToken.isEmpty() && accessToken != null || refreshToken.isEmpty() && refreshToken != null) {
            log.error("Access Token 또는 Refresh Token이 존재하지 않습니다.");
            throw new NullPointerException("Access Token 또는 Refresh Token이 존재하지 않습니다.");
        }

        // redis에서 기존에 저장된 AccessToken 조회
        String accessTokenFromRedis = getAccessTokenFromRedis(refreshToken);

        // 사용자가 보낸 Access Token과 최초 발급된 Access Token이 일치하지 않을 경우
        if (!accessToken.equals(accessTokenFromRedis)) {
            log.error("Access Token이 위조되었습니다.");
            throw new IllegalArgumentException("Access Token이 위조되었습니다.");
        }
        return true;
    }

    // AccessToken 재발급 메서드
    public String regenerateAccessToken(String refreshToken, HttpServletResponse res) {
        Long userId = Long.parseLong(getUserInfo(substringToken(refreshToken)).getSubject());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NullPointerException("해당 유저는 존재하지 않습니다."));

        String username = user.getUsername();
        UserRole userRole = user.getUserRole();

        String newAccessToken = createAccessToken(userId, username, userRole);

        res.addHeader(HEADER_ACCESS_TOKEN, newAccessToken);
        log.info("토큰재발급 성공: {}", newAccessToken);
        return newAccessToken;
    }

    // Redis에 저장된 최초 AccessToken 반환 메서드 (key : refreshToken / value : accessToken)
    public String getAccessTokenFromRedis(String refreshToken) {
        ValueOperations<String, String> values = redisTemplate.opsForValue();
        return redisService.getAccessToken(refreshToken);
    }

    // Redis에 최초 발급된 토큰 값 저장 (key : refreshToken / value : accessToken)
    public void saveAccessTokenFromRedis(String refreshToken, String accessToken){
        Date refreshExpire = getUserInfo(refreshToken).getExpiration(); // refresh 토큰의 만료일
        redisService.saveAccessToken(refreshToken, accessToken, refreshExpire);
    }

//    위의 예제에서 "setKeyWithExpiration" 메서드는 Redis에 데이터를 저장하고, 해당 데이터를 주어진 만료 시간 후에 삭제하도록 설정합니다.
//    이렇게 함으로써 특정 데이터가 일정 시간이 지나면 Redis에서 자동으로 삭제되도록 할 수 있습니다.

//    redisTemplate.opsForValue().set(key, value);
//    redisTemplate.expireAt(key, new Date(System.currentTimeMillis() + expirationInMillis));


    // accessToken 재발급 요청 ->  AccessToken, RefreshToken 검증 -> redis에서 refreshToken으로 기존 토큰 대조
    // -> accessToken 재발급

    // 전제조건 : JWT의 refreshToken의 만료 시간 == Redis의 자동 삭제가 되는 시간(refresh 토큰이 살아있을 때만 토큰 재발급)
    // 1. redis에서 refreshToken으로 조회했을 때 없는 경우 => refresh 토큰의 만료시간이 지난 경우 => 이후 손쓸 방법 없음.
    // 2. 사용자가 제출한 accessToken과 Redis에 저장된 accessToken과 대조에 성공한 경우 => redis에 기존에 저장된 토큰을 지우고 새 토큰을 저장.
    // 3. 클라이언트에게 새 토큰 반환
}