package com.sparta.abnb.service;

import com.sparta.abnb.dto.requestdto.ReservationRequestDto;
import com.sparta.abnb.dto.responsedto.ReservationResponseDto;
import com.sparta.abnb.dto.responsedto.RoomResponseDto;
import com.sparta.abnb.entity.Reservation;
import com.sparta.abnb.entity.Room;
import com.sparta.abnb.entity.User;
import com.sparta.abnb.repository.ReservationRepository;
import com.sparta.abnb.repository.RoomRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j(topic = "Reservation Service")
@Service
public class ReservationService {
    private ReservationRepository reservationRepository;
    private RoomRepository roomRepository;

    // 예약하기 비즈니스 로직
    public ReservationResponseDto createReservation(Long roomId, ReservationRequestDto reservationRequestDto, User user){
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new NullPointerException("해당 숙소를 찾을 수 없습니다."));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate checkInDate = LocalDate.parse(reservationRequestDto.getCheckInDate(), formatter);
        LocalDate checkOutDate = LocalDate.parse(reservationRequestDto.getCheckOutDate(), formatter);

        Reservation reservation = Reservation.builder()
                .reservationNumber(reservationRequestDto.getReservationNumber())
                .checkin(checkInDate)
                .checkout(checkOutDate)
                .user(user)
                .room(room)
                .build();

        reservationRepository.save(reservation);

        return ReservationResponseDto.builder()
                .reservationId(reservation.getReservationId())
                .checkInDate(reservation.getCheckin())
                .checkOutDate(reservation.getCheckout())
                .reservationNumber(reservation.getReservationNumber())
                .build();
    }
}
