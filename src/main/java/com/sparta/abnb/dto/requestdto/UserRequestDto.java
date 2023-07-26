package com.sparta.abnb.dto.requestdto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;

@Getter
public class UserRequestDto {

    @Email(message = "이메일 양식이 아닙니다")
    @NotBlank(message = "Email 공백 불가")
    private String email;

    @NotBlank(message = "password 공백 불가")
    private String password;

    @NotBlank(message = "이름 공백 불가")
    private String username;

    private String profilePicture;

    private boolean admin = false;
    private String adminToken;
}
