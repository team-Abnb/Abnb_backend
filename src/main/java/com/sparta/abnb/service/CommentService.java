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
import org.springframework.transaction.annotation.Transactional;

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

    //후기 삭제
    @Transactional
    public ResponseEntity<String> deleteComment(Long roomId, Long commentId, User user) {

        // 후기를 남긴 Room이 있는지 확인
        Room room = roomRepository.findById(roomId).orElseThrow(()->
                new IllegalArgumentException("해당 ROOM이 존재하지 않습니다."));
        // 해당 후기를 DB에 있는지 확인
        Comment comment = commentRepository.findById(commentId).orElseThrow(()->
                new IllegalArgumentException("작성하신 후기가 존재하지 않습니다."));

        commentRepository.delete(comment);

        return ResponseEntity.status(HttpStatus.OK).body("후기 삭제가 완료되었습니다.");
    }
}
