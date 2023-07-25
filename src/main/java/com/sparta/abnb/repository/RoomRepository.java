package com.sparta.abnb.repository;

import com.sparta.abnb.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {
    List<Room> findAllByThemeOrderByCreatedAtDesc(String theme);
}
