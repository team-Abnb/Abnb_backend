package com.sparta.abnb.controller;

import com.sparta.abnb.dto.responsedto.UserRequestDto;
import com.sparta.abnb.dto.responsedto.UserResponseDto;
import com.sparta.abnb.entity.User;
import com.sparta.abnb.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/users/profile")
public class UserController {
    private final UserService userService;

    //프로필 조회는 누구나 가능하게
    @GetMapping("/{userId}")
    public UserResponseDto getUserProfile(@PathVariable Long userId){
        return userService.getUserProfile(userId);
    }
    // 프로필 수정
    @PutMapping("/{userId}")
    public UserResponseDto updateUserProfile(@PathVariable Long userId, User user,
                                             @RequestPart ("file") MultipartFile file,
                                             @RequestPart("metadata") UserRequestDto userRequestDto){
        return userService.updateUserProfile(userId, user, file, userRequestDto);
    }

}
