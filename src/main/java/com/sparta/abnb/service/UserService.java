package com.sparta.abnb.service;

import com.sparta.abnb.dto.requestdto.UserRequestDto;
import com.sparta.abnb.entity.User;
import com.sparta.abnb.repository.UserRepository;
import com.sparta.abnb.role.UserRole;
import com.sparta.abnb.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RedisService redisService;
    private final String ADMIN_TOKEN = "AAABnvxRVklrnYxKZ0aHgTBcXukeZygoC";

    @Transactional
    public ResponseEntity<String> signUp(UserRequestDto userRequestDto) {
        String email = userRequestDto.getEmail();
        String username = userRequestDto.getUsername();
        String password = passwordEncoder.encode(userRequestDto.getPassword());

        if (checkEmail(email) || checkUsername(username)) {
            throw new IllegalArgumentException("데이터가 이미 존재합니다.");
        }

        // 사용자 ROLE 확인
        UserRole role = UserRole.USER;
        if (userRequestDto.isAdmin()) {
            if (!ADMIN_TOKEN.equals(userRequestDto.getAdminToken())) {
                throw new IllegalArgumentException("관리자 번호가 유효하지 않습니다.");
            }
            role = UserRole.ADMIN;
        }

        // 사용자 등록
        User user = User.builder()
                .email(email)
                .password(password)
                .username(username)
                .role(role) // role 필드 설정 추가
                .build();
        userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body("회원가입 성공");
    }

    public Boolean checkEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public Boolean checkUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public ResponseEntity<String> logOut(HttpServletRequest req) {
        String refreshToken = req.getHeader(jwtUtil.HEADER_REFRESH_TOKEN);
        redisService.deleteToken(refreshToken);
        return ResponseEntity.status(HttpStatus.OK).body("로그아웃 성공");
    }
}
