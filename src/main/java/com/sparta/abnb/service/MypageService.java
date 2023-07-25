package com.sparta.abnb.service;

import com.sparta.abnb.dto.requestdto.MypageRequestDto;
import com.sparta.abnb.dto.responsedto.MypageResponseDto;
import com.sparta.abnb.entity.User;
import com.sparta.abnb.repository.UserRepository;
import com.sparta.abnb.util.S3Util;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class MypageService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final S3Util s3Util;

    //마이페이지 조회
    public MypageResponseDto getUserProfile(Long userId) {
        User checkuser =  userRepository.findById(userId).orElseThrow(()
                -> new IllegalArgumentException("해당 USER가 존재하지 않습니다."));


        return MypageResponseDto.builder()
                .profileImageUrl(checkuser.getProfilePicture())
                .username(checkuser.getUsername())
                .bio(checkuser.getBio())
                .build();
    }
    // 프로필 수정
    @Transactional
    public MypageResponseDto updateUserProfile(Long userId, User user, MultipartFile file, MypageRequestDto mypageRequestDto) {

        if (!user.getUserId().equals(userId)) {
            throw new IllegalArgumentException("해당 USER가 아닙니다.");
        }

        // 비밀번호 수정 로직
        // 웹에서 현재 비번을 확인 - 새 비밀번호를 적는게 한 공간에 있기 때문에 새로운 비번이 적혀있다면?
        // 비밀번호를 수정한다는 뜻으로 받아드리고 이 로직을 작성함.
        String newPassword = null;
        if (mypageRequestDto.getPassword() != null) {
            if (!user.getPassword().equals(mypageRequestDto.getPassword())) {
                throw new IllegalArgumentException("비밀번호가 일치하지 않습니다. 다시 입력해주세요.");
            }
            newPassword = passwordEncoder.encode(mypageRequestDto.getNewPassword());
        }

        //이미지 수정 로직
        String url = null;
        if (file != null) {
            // 기본이미지를 보내주거나? 새로운 이미지르 보내주거나
            url = s3Util.updateImage(user.getProfilePicture(), file, "profilePicture");
        }
        // entity 저장
        User updateUser = User.builder()
                .username(mypageRequestDto.getUsername())
                .phoneNumber(mypageRequestDto.getPhoneNumber())
                .bio(mypageRequestDto.getBio())
                .profilePicture(file == null ? user.getProfilePicture() : url)
                .password(mypageRequestDto.getPassword() != null ? newPassword : user.getPassword())
                .build();

        userRepository.save(updateUser);
        //entity -> responseDto
        return MypageResponseDto.builder()
                .username(updateUser.getUsername())
                .phoneNumber(updateUser.getPhoneNumber())
                .bio(updateUser.getBio())
                .profileImageUrl(updateUser.getProfilePicture())
                .build();

    }

}
