package com.sparta.abnb.controller;

import com.sparta.abnb.dto.responsedto.UserResponseDto;
import com.sparta.abnb.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
