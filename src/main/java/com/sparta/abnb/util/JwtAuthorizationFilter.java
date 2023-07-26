package com.sparta.abnb.util;

import com.sparta.abnb.security.UserDetailsServiceImpl;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j(topic = "JWT 검증 및 인가")
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;

    public JwtAuthorizationFilter(JwtUtil jwtUtil, UserDetailsServiceImpl userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain filterChain) throws ServletException, IOException {
        String accessTokenValue = jwtUtil.getAccessTokenFromHeader(req);

        if (StringUtils.hasText(accessTokenValue)) {
            log.info(accessTokenValue);

            if (!jwtUtil.validateAccessToken(accessTokenValue)) {
                log.error("AccessTokenValue Error");

                String refreshTokenValue = jwtUtil.getRefreshTokenFromHeader(req);

                if (StringUtils.hasText(refreshTokenValue)) {

                    // 유효한 Refresh Token인지 검증
                    if (jwtUtil.validateRegenerate(accessTokenValue, refreshTokenValue)) {
                        String newAccessToken = jwtUtil.regenerateAccessToken(refreshTokenValue, res);
                        log.info("새로운 AccessToken 발급 완료: {}", newAccessToken);
                    } else {
                        log.error("Refresh Token 검증 오류");
                    }
                } else {
                    log.error("Refresh Token이 없습니다.");
                }
                return;
            }
            Claims info = jwtUtil.getUserInfo(accessTokenValue);

            try {
                setAuthentication(info.get("email", String.class));
            } catch (Exception e) {
                log.error(e.getMessage());
                return;
            }
        } else {
            log.info("로그인 하지 않은 사용자");
        }
        filterChain.doFilter(req, res);
    }

    public void setAuthentication(String email) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication authentication = createAuthentication(email);
        context.setAuthentication(authentication);

        SecurityContextHolder.setContext(context);
    }

    // 인증 객체 생성
    private Authentication createAuthentication(String email) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
}