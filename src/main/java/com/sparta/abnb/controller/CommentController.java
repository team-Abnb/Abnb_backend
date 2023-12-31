package com.sparta.abnb.controller;

import com.sparta.abnb.dto.requestdto.CommentRequestDto;
import com.sparta.abnb.dto.responsedto.CommentDto;
import com.sparta.abnb.dto.responsedto.CommentResponseDto;
import com.sparta.abnb.security.UserDetailsImpl;
import com.sparta.abnb.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/rooms")
public class CommentController {

    private final CommentService commentService;
    //후기 등록
    @PostMapping("/{roomId}/comments")
    public ResponseEntity<CommentDto> createComment(
            @PathVariable Long roomId,
            @RequestBody CommentRequestDto commentRequestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails){
        return commentService.createComment(roomId, commentRequestDto, userDetails.getUser());
    }
    //후기 조회
    @GetMapping("/{roomId}/comments")
    public ResponseEntity<CommentResponseDto> getComments(@PathVariable Long roomId){
        return commentService.getComments(roomId);
    }
    //후기 삭제
    @DeleteMapping("/{roomId}/comments/{commentId}")
    public ResponseEntity<String> deleteComment(
            @PathVariable Long roomId,
            @PathVariable Long commentId,
            @AuthenticationPrincipal UserDetailsImpl userDetails){
        return commentService.deleteComment(roomId,commentId, userDetails.getUser());
    }

}
