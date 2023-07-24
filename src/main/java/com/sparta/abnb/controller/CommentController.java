package com.sparta.abnb.controller;

import com.sparta.abnb.dto.requestdto.CommentRequestDto;
import com.sparta.abnb.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/rooms")
public class CommentController {

    private final CommentService commentService;
    //후기 등록
    @PostMapping("/{roomId}/comment")
    public ResponseEntity<String> createComment(
            @PathVariable Long roomId,
            @RequestBody CommentRequestDto commentRequestDto
            @AuthenticationPrincipal UserDetailsImpl userDetails){
       return commentService.createComment(roomId, commentRequestDto, userDetails.getUser());
    }
    //후기 조회

    //후기 삭제

}
