package com.sparta.abnb.kakao.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sparta.abnb.kakao.service.KakaoService;
import com.sparta.abnb.util.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class KakaoController {

    private final JwtUtil jwtUtil;
    private final KakaoService kakaoService;

    @GetMapping("/users/kakao")
    public ResponseEntity<String> kakaoLogin(@RequestParam String code, HttpServletResponse response) throws JsonProcessingException {
        String accessToken = kakaoService.kakaoAccessToken(code);
        String refreshToken = kakaoService.kakaoRefreshToken(code);
        jwtUtil.saveTokenToRedis(refreshToken, accessToken);
        jwtUtil.addTokenToHeader(accessToken, refreshToken, response);
        return ResponseEntity.status(HttpStatus.OK).body("로그인 성공");
    }
}
