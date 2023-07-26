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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Slf4j(topic = "Reservation Service")
@Service
@RequiredArgsConstructor
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final RoomRepository roomRepository;

    // 예약하기 비즈니스 로직
    public ResponseEntity<ReservationResponseDto> createReservation(Long roomId, ReservationRequestDto reservationRequestDto, User user) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new NullPointerException("해당 숙소를 찾을 수 없습니다."));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate checkInDate = LocalDate.parse(reservationRequestDto.getCheckInDate(), formatter);
        LocalDate checkOutDate = LocalDate.parse(reservationRequestDto.getCheckOutDate(), formatter);

        if (checkOutDate.isBefore(checkInDate)){
            throw new IllegalArgumentException("체크아웃 날짜는 체크인 날짜보다 빠르게 선택할 수 없습니다.");
        }

        Reservation reservation = Reservation.builder()
                .reservationNumber(reservationRequestDto.getReservationNumber())
                .checkin(checkInDate)
                .checkout(checkOutDate)
                .user(user)
                .room(room)
                .build();

        reservationRepository.save(reservation);

        ReservationResponseDto reservationResponseDto = ReservationResponseDto.builder()
                .reservationId(reservation.getReservationId())
                .checkInDate(reservation.getCheckin())
                .checkOutDate(reservation.getCheckout())
                .reservationNumber(reservation.getReservationNumber())
                .username(reservation.getUser().getUsername())
                .roomTitle(reservation.getRoom().getTitle())
                .createdAt(reservation.getCreatedAt())
                .modifiedAt(reservation.getModifiedAt())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(reservationResponseDto);
    }

    // 예약한 내역 확인 비즈니스 로직
    public ResponseEntity<ReservationResponseDto> reservationDetail(Long roomId, Long reservationId, User user) throws AccessDeniedException {
        // 예약 내역 조회
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new NullPointerException("해당 예약내역을 찾을 수 없습니다."));

        // 사용자가 요청 시 보낸 RoomId로 조회한 Room
        Room requestRoom = roomRepository.findById(roomId)
                .orElseThrow(() -> new NullPointerException("해당 숙소는 존재하지 않습니다."));
        // 예약 내역에서 조회한 Room
        Room reservationRoom = reservation.getRoom();

        if (!requestRoom.equals(reservationRoom)) {
            throw new IllegalArgumentException("요청하신 숙소와 예약하신 숙소가 다릅니다.");
        }

        if (reservation.getUser().getUserId() != user.getUserId()) {
            throw new AccessDeniedException("예약 내역은 예약자 본인만 확인할 수 있습니다.");
        }

        ReservationResponseDto reservationResponseDto = ReservationResponseDto.builder()
                .reservationId(reservation.getReservationId())
                .checkInDate(reservation.getCheckin())
                .checkOutDate(reservation.getCheckout())
                .reservationNumber(reservation.getReservationNumber())
                .username(reservation.getUser().getUsername())
                .roomTitle(reservation.getRoom().getTitle())
                .createdAt(reservation.getCreatedAt())
                .modifiedAt(reservation.getModifiedAt())
                .build();

        return ResponseEntity.ok(reservationResponseDto);
    }

    // 예약 정보 수정 비즈니스 로직
    @Transactional
    public ResponseEntity<ReservationResponseDto> reservationUpdate(Long roomId, Long reservationId, ReservationRequestDto reservationRequestDto, User user) throws AccessDeniedException {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new NullPointerException("해당 예약 내역을 찾을 수 없습니다."));

        // 사용자가 요청 시 보낸 RoomId로 조회한 Room
        Room requestRoom = roomRepository.findById(roomId)
                .orElseThrow(() -> new NullPointerException("해당 숙소는 존재하지 않습니다."));
        // 예약 내역에서 조회한 Room
        Room reservationRoom = reservation.getRoom();

        if (!requestRoom.equals(reservationRoom)) {
            throw new IllegalArgumentException("요청하신 숙소와 예약하신 숙소가 다릅니다.");
        }

        if (reservation.getUser().getUserId() != user.getUserId()) {
            throw new AccessDeniedException("예약 정보 수정은 예약자 본인만 변경이 가능합니다.");
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate checkInDate = LocalDate.parse(reservationRequestDto.getCheckInDate(), formatter);
        LocalDate checkOutDate = LocalDate.parse(reservationRequestDto.getCheckOutDate(), formatter);

        reservation.update(reservationRequestDto, checkInDate, checkOutDate, reservationRoom, user);

        ReservationResponseDto reservationResponseDto = ReservationResponseDto.builder()
                .reservationId(reservation.getReservationId())
                .checkInDate(reservation.getCheckin())
                .checkOutDate(reservation.getCheckout())
                .reservationNumber(reservation.getReservationNumber())
                .username(reservation.getUser().getUsername())
                .roomTitle(reservation.getRoom().getTitle())
                .createdAt(reservation.getCreatedAt())
                .modifiedAt(reservation.getModifiedAt())
                .build();

        return ResponseEntity.ok(reservationResponseDto);
    }

    // 예약 취소 비즈니스 로직
    public ResponseEntity<String> deleteReservation(Long roomId, Long reservationId, User user) throws AccessDeniedException {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new NullPointerException("해당 예약 내역을 찾을 수 없습니다."));

        // 사용자가 요청 시 보낸 RoomId로 조회한 Room
        Room requestRoom = roomRepository.findById(roomId)
                .orElseThrow(() -> new NullPointerException("해당 숙소는 존재하지 않습니다."));
        // 예약 내역에서 조회한 Room
        Room reservationRoom = reservation.getRoom();

        if (!requestRoom.equals(reservationRoom)) {
            throw new IllegalArgumentException("요청하신 숙소와 예약하신 숙소가 다릅니다.");
        }

        if (reservation.getUser().getUserId() != user.getUserId()) {
            throw new AccessDeniedException("예약 취소는 예약자 본인만 가능합니다.");
        }

        reservationRepository.delete(reservation);
        return ResponseEntity.ok("예약 삭제");
    }
}
