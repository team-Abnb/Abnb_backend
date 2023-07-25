package com.sparta.abnb.dto.responsedto;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class MypageResponseDto {
    private String username;
    private String phoneNumber;
    private String profileImageUrl;
    private String bio;

}
