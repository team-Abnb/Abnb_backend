package com.sparta.abnb.controller;

import com.sparta.abnb.dto.requestdto.UserRequestDto;
import com.sparta.abnb.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {
    private final UserService userService;

    @PostMapping("/users/signup")
    public ResponseEntity<String> signUp(@Valid @RequestBody UserRequestDto userRequestDto) {
        return userService.signUp(userRequestDto);
    }

    //중복되면 false
    //중복안되면 true
    @PostMapping("/users/email")
    public Boolean checkeMail(@RequestParam String email) {
        return !userService.checkEmail(email);
    }

    @PostMapping("/users/username")
    public Boolean checkUsername(@RequestParam String username) {
        return !userService.checkUsername(username);
    }
}
