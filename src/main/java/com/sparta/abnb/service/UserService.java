package com.sparta.abnb.service;

import com.sparta.abnb.dto.responsedto.UserResponseDto;
import com.sparta.abnb.entity.User;
import com.sparta.abnb.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserResponseDto getUserProfile(Long userId) {
        User checkUser =  userRepository.findById(userId).orElseThrow(()
                -> new IllegalArgumentException("해당 USER가 존재하지 않습니다."));

        return UserResponseDto.builder()
                .profileImageUrl(checkUser.getProfilePicture())
                .username(checkUser.getUsername())
                .bio(checkUser.getBio())
                .build();
    }

}
