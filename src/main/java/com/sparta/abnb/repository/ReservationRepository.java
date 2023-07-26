package com.sparta.abnb.repository;

import com.sparta.abnb.entity.Reservation;
import com.sparta.abnb.entity.Room;
import com.sparta.abnb.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findAllByRoom(Room room);
    List<Reservation> findAllByUserAndRoom(User user, Room room);
}
