package com.sparta.abnb.dto.requestdto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class RoomRequestDto {
    @NotEmpty(message = "title의 길이는 1에서 255 사이여야 합니다")
    @Size(min = 1, max = 255, message = "title의 길이는 1에서 255 사이여야 합니다")
    private String title;

    @NotEmpty(message = "content의 길이는 1에서 10000 사이여야 합니다")
    @Size(min = 1, max = 10000, message = "content의 길이는 1에서 10000 사이여야 합니다")
    private String content;

    private Integer maxPeople;

    private String theme;

    private Long price;

    private String address;
}
