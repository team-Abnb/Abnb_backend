package com.sparta.abnb.kakao.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class KakaoUserInfoDto {

    private Long kakaoId;
    private String email;
    private String username;
    private String profilePicture;

    public KakaoUserInfoDto(Long kakaoId, String email, String username, String profilePicture) {
        this.kakaoId = kakaoId;
        this.email = email;
        this.username = username;
        this.profilePicture = profilePicture;
    }

}
