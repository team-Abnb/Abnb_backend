package com.sparta.abnb.repository;

import com.sparta.abnb.entity.Room;
import com.sparta.abnb.entity.RoomPicture;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomPictureRepository extends JpaRepository<RoomPicture, Long> {
    List<RoomPicture> findAllByRoom(Room room);
}
