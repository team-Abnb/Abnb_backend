package com.sparta.abnb.service;

import com.sparta.abnb.dto.requestdto.MypageRequestDto;
import com.sparta.abnb.dto.responsedto.MypageResponseDto;
import com.sparta.abnb.entity.User;
import com.sparta.abnb.repository.UserRepository;
import com.sparta.abnb.util.S3Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class MypageService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final S3Util s3Util;

    @Value("${app.default.profile.image}")
    private String defaultProfileImage;

    //마이페이지 조회
    public ResponseEntity<MypageResponseDto> getUserProfile(Long userId) {
        User checkuser =  userRepository.findById(userId).orElseThrow(()
                -> new IllegalArgumentException("해당 USER가 존재하지 않습니다."));

        MypageResponseDto mypageResponseDto = MypageResponseDto.builder()
                .email(checkuser.getEmail())
                .profilePicture(checkuser.getProfilePicture())
                .username(checkuser.getUsername())
                .bio(checkuser.getBio())
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(mypageResponseDto);
    }
    // 프로필 수정
    @Transactional
    public ResponseEntity<MypageResponseDto> updateUserProfile(Long userId, User user, MultipartFile file, MypageRequestDto mypageRequestDto) {

        if (!user.getUserId().equals(userId)) {
            throw new IllegalArgumentException("해당 USER가 아닙니다.");
        }

        // 비밀번호 수정 로직
        String newPassword = user.getPassword();
        if (mypageRequestDto.getPassword() != null) { // 비밀번호를 변경하기 위해 기존의 비밀번호의 값을 입력했을 경우
            if (!passwordEncoder.matches(mypageRequestDto.getPassword(),user.getPassword())) {
                throw new IllegalArgumentException("비밀번호가 일치하지 않습니다. 다시 입력해주세요.");
            }
            newPassword = passwordEncoder.encode(mypageRequestDto.getNewPassword());
        }


        //기존 가지고 있는 이미지
        String currentPicture = user.getProfilePicture();
        log.info("요청받은 프로필 이미지 url : " + file);
        //이미지 수정 로직
        String newProfilePicture = Optional.ofNullable(mypageRequestDto.getUseDefaultPicture())
                .filter(useDefault  -> useDefault)
                .map(userDefaultImg -> defaultProfileImage)
                .orElseGet(() -> Optional.ofNullable(file)
                        .map(picture -> s3Util.uploadImage(file, "profilePicture"))
                        .orElse(user.getProfilePicture()));


        log.info("새로들어가는 이미지 : "  + newProfilePicture);
        // 현재 이미지가 default가 아니고 / 현재이미지와 들어온 이미지가 다른경우 현재이미지 삭제하기
        if(!currentPicture.equals(defaultProfileImage) && !currentPicture.equals(newProfilePicture)){
            log.info("삭제할 url : " + currentPicture);
            s3Util.deleteImage(currentPicture);
        }

        user.updateUser(user,mypageRequestDto,newPassword,newProfilePicture);

        userRepository.save(user);

        //entity -> responseDto

        MypageResponseDto mypageResponseDto = MypageResponseDto.builder()
                .email(user.getEmail())
                .username(user.getUsername())
                .bio(user.getBio())
                .profilePicture(user.getProfilePicture())
                .build();

        log.info("user에 업데이트 된 이미지  : " + user.getProfilePicture());
        log.info(user.getPassword());
        return ResponseEntity.status(HttpStatus.OK).body(mypageResponseDto);


    }

}
