package com.sparta.abnb.dto.responsedto;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserRequestDto {
    private String username;
    private String password;
    private String newPassword;
    private String phoneNumber;
    private String bio;
}
