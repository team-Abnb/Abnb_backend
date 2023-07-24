package com.sparta.abnb.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.Date;

@RequiredArgsConstructor
@Service
public class RedisService {
    private final RedisTemplate<String, String> redisTemplate;

    // 최초 발급된 Access을 반환하는 비즈니스 로직(조회?)
    public String getAccessToken(String refreshToken) {
        return redisTemplate.opsForValue().get(refreshToken);
    }

    // 최초 발급된 Access Token과 Refresh Token을 저장 (key : Access, value : refresh)
    public void saveAccessToken(String accessToken, String refreshToken, Date refreshExpire) {
        String encodedAccessToken = Base64.getEncoder().encodeToString(accessToken.getBytes());
        String encodedRefreshToken = Base64.getEncoder().encodeToString(refreshToken.getBytes());
        redisTemplate.opsForValue().set(encodedRefreshToken, encodedAccessToken); // key : value
        redisTemplate.expireAt(encodedRefreshToken, refreshExpire); // 키 값에 해당 객체 만료일(자동삭제) 설정
    }

    // 해당 데이터를 삭제하는 메서드?
    // accessToken이 유효한데? refreshToken에 요청을 할 경우?
    public void deleteToken(String refreshToken) {
        redisTemplate.unlink(refreshToken);
    }
}
