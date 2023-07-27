package com.sparta.abnb.dto.responsedto;

import lombok.*;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentResponseDto {
    private Integer totalComments;
    private List<CommentDto> commentResponseDtos;
    private String message;
}
