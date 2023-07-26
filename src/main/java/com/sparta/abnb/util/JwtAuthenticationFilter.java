package com.sparta.abnb.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.abnb.dto.requestdto.LoginRequestDto;
import com.sparta.abnb.entity.User;
import com.sparta.abnb.role.UserRole;
import com.sparta.abnb.security.UserDetailsImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j(topic = "로그인 및 JWT 생성")
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
        setFilterProcessesUrl("/api/users/login");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        log.info("로그인 시도");
        try {
            LoginRequestDto requestDto = new ObjectMapper().readValue(request.getInputStream(), LoginRequestDto.class);

            return getAuthenticationManager().authenticate(
                    new UsernamePasswordAuthenticationToken(
                            requestDto.getEmail(),
                            requestDto.getPassword(),
                            null
                    )
            );
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        log.info("로그인 성공 및 JWT 생성");
        User user = ((UserDetailsImpl) authResult.getPrincipal()).getUser();
        Long id = user.getUserId();
        UserRole role = user.getRole();
        String username = user.getUsername();
        String email = user.getEmail();
        String accessToken = jwtUtil.createAccessToken(id, username, email, role);
        String refreshToken = jwtUtil.createRefreshToken(id);
        jwtUtil.saveTokenToRedis(refreshToken, accessToken);
        jwtUtil.addTokenToHeader(accessToken, refreshToken, response);
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("success", true);
        data.put("statusCode", HttpServletResponse.SC_OK);
        data.put("msg", "로그인 성공");

        ObjectMapper mapper = new ObjectMapper();
        String jsonString = mapper.writeValueAsString(data);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(jsonString);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        log.info("로그인 실패");

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("success", false);
        data.put("statusCode", HttpServletResponse.SC_BAD_REQUEST);
        data.put("msg", "비밀번호 혹은 이메일이 틀렸습니다.");

        // 에러 메시지를 JSON 형식으로 생성
        ObjectMapper objectMapper = new ObjectMapper();
        String errorJson = objectMapper.writeValueAsString(data);

        // 응답에 에러 메시지 전송
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(errorJson);
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }
}