package com.sparta.abnb.service;

import com.sparta.abnb.dto.requestdto.ReservationRequestDto;
import com.sparta.abnb.dto.responsedto.ReservationResponseDto;
import com.sparta.abnb.entity.Reservation;
import com.sparta.abnb.entity.Room;
import com.sparta.abnb.entity.User;
import com.sparta.abnb.repository.ReservationRepository;
import com.sparta.abnb.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Slf4j(topic = "Reservation Service")
@Service
@RequiredArgsConstructor
public class ReservationService {
    private ReservationRepository reservationRepository;
    private RoomRepository roomRepository;

    // 예약하기 비즈니스 로직
    public ReservationResponseDto createReservation(Long roomId, ReservationRequestDto reservationRequestDto, User user) {
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

    // 예약한 내역 확인 비즈니스 로직
    public ReservationResponseDto reservationDetail(Long roomId, Long reservationId, User user) throws AccessDeniedException {
        // 예약 내역 조회
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new NullPointerException("해당 예약내역을 찾을 수 없습니다."));

        // 사용자가 요청 시 보낸 RoomId로 조회한 Room
        Room requestRoom = roomRepository.findById(roomId).orElse(null);
        // 예약 내역에서 조회한 Room
        Room reservationRoom = reservation.getRoom();

        if (!requestRoom.equals(reservationRoom)) {
            throw new IllegalArgumentException("요청하신 숙소와 예약하신 숙소가 다릅니다.");
        }

        if (reservation.getUser().getUserId() != user.getUserId()) {
            throw new AccessDeniedException("예약 내역은 예약자 본인만 확인할 수 있습니다.");
        }

        return ReservationResponseDto.builder()
                .reservationId(reservation.getReservationId())
                .checkInDate(reservation.getCheckin())
                .checkOutDate(reservation.getCheckout())
                .reservationNumber(reservation.getReservationNumber())
                .build();
    }
}
