package com.sparta.abnb.repository;

import com.sparta.abnb.entity.Comment;
import com.sparta.abnb.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByRoomId(Long roomId);
}