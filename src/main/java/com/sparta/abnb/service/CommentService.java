package com.sparta.abnb.service;

import com.sparta.abnb.dto.requestdto.CommentRequestDto;
import com.sparta.abnb.dto.responsedto.CommentResponseDto;
import com.sparta.abnb.entity.Comment;
import com.sparta.abnb.entity.Room;
import com.sparta.abnb.entity.User;
import com.sparta.abnb.repository.CommentRepository;
import com.sparta.abnb.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final RoomRepository roomRepository;


    //후기 작성
    public ResponseEntity<CommentResponseDto> createComment(Long roomId, CommentRequestDto commentRequestDto, User user) {
        // 해당 room이 존재하는지 확인
        Room room = roomRepository.findById(roomId).orElseThrow(()
                -> new IllegalArgumentException("찾으시는 ROOM은 존재하지 않습니다."));

        // 댓글 작성 및 저장
       Comment comment = Comment.builder()
               .user(user)
               .room(room)
               .comment(commentRequestDto.getComment())
               .build();

       commentRepository.save(comment);

       CommentResponseDto commentResponseDto = CommentResponseDto.builder()
               .commentId(comment.getCommentId())
               .comment(comment.getComment())
               .build();

       return ResponseEntity.status(HttpStatus.OK).body(commentResponseDto);

    }

}
