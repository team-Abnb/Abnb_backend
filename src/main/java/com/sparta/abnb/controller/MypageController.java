package com.sparta.abnb.controller;

import com.sparta.abnb.dto.requestdto.UserRequestDto;
import com.sparta.abnb.dto.responsedto.MypageResponseDto;
import com.sparta.abnb.dto.responsedto.UserResponseDto;
import com.sparta.abnb.security.UserDetailsImpl;
import com.sparta.abnb.service.MypageService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/users/profile")
public class MypageController {

    private final MypageService mypageService;

    //프로필 조회는 누구나 가능하게
    @GetMapping("/{userId}")
    public MypageResponseDto getUserProfile(@PathVariable Long userId){
        return mypageService.getUserProfile(userId);
    }
    // 프로필 수정
    @PutMapping("/{userId}")
    public MypageResponseDto updateUserProfile(@PathVariable Long userId,
                                             @AuthenticationPrincipal UserDetailsImpl userDetails,
                                             @RequestPart("file") MultipartFile file,
                                             @RequestPart("metadata") UserRequestDto userRequestDto){
        return mypageService.updateUserProfile(userId, userDetails.getUser(), file, userRequestDto);
    }
}
