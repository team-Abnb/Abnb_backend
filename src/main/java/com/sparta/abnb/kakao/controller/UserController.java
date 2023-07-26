package com.sparta.abnb.kakao.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sparta.abnb.kakao.service.KakaoService;
import com.sparta.abnb.util.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {

    private final JwtUtil jwtUtil;
    private final KakaoService kakaoService;

    @GetMapping("/users/kakao")
    public String kakaoLogin(@RequestParam String code, HttpServletResponse response) throws JsonProcessingException {
        String token = kakaoService.kakaoLogin(code);

        Cookie cookie = new Cookie(jwtUtil.HEADER_ACCESS_TOKEN, token);
        cookie.setPath("/");
        response.addCookie(cookie);

        return "redirect:/";
    }
}
