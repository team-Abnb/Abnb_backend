package com.sparta.abnb.dto.responsedto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

@Getter
@Builder
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PROTECTED)
public class WishlistResponseDto {
    private Long roomId;
    private String title;
    private Long price;
    private String address;
    private String thumbnailImg;
    private Boolean isWishlist;
}
