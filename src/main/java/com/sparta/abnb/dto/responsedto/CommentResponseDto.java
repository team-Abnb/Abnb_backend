package com.sparta.abnb.dto.responsedto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentResponseDto {
    private Long commentId;
    private String comment;
    private String profilePicture;
    private String username;
    private LocalDateTime createdAt;
}
