package com.sparta.abnb.service;

import com.sparta.abnb.dto.responsedto.UserRequestDto;
import com.sparta.abnb.dto.responsedto.UserResponseDto;
import com.sparta.abnb.entity.User;
import com.sparta.abnb.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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
    // 프로필 수정
    @Transactional
    public UserResponseDto updateUserProfile(Long userId, User user, MultipartFile file, UserRequestDto userRequestDto) {
        if(!user.getUserId().equals(userId)){
            throw new IllegalArgumentException("해당 USER가 아닙니다.");
        }
        if(!user.getPassword().equals(userRequestDto.getPassword())){
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다. 다시 입력해주세요.");
        }

        // entity 저장
        User updateUser = User.builder()
                .username(userRequestDto.getUsername())
                .phoneNumber(userRequestDto.getPhoneNumber())
                .bio(userRequestDto.getBio())
                .profilePicture(user.getProfilePicture())
                .password(userRequestDto.getNewPassword())
                .build();

        //entity -> responseDto

        return UserResponseDto.builder()
                .username(updateUser.getUsername())
                .phoneNumber(updateUser.getPhoneNumber())
                .bio(updateUser.getBio())
                .profileImageUrl(updateUser.getProfilePicture())
                .password(updateUser.getPassword())
                .build();

    }

}
