package com.sparta.abnb.dto.requestdto;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MypageRequestDto {
    private String username;
    private String password;
    private String newPassword;
    private String phoneNumber;
    private Boolean useDefaultPicture;
    private String bio;
}
