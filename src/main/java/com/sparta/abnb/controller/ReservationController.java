package com.sparta.abnb.controller;

import com.sparta.abnb.dto.requestdto.ReservationRequestDto;
import com.sparta.abnb.dto.responsedto.ReservationResponseDto;
import com.sparta.abnb.entity.User;
import com.sparta.abnb.security.UserDetailsImpl;
import com.sparta.abnb.service.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;

@Slf4j(topic = "Reservation Controller")
@RestController
@RequestMapping("/api/rooms/{roomId}")
@RequiredArgsConstructor
public class ReservationController {
    private final ReservationService reservationService;

    // 예약하기 API
    @PostMapping("/reservation")
    public ResponseEntity<ReservationResponseDto> createReservation(@PathVariable Long roomId,
                                                                    @RequestBody ReservationRequestDto requestDto,
                                                                    @AuthenticationPrincipal UserDetailsImpl userDetails) {
        User user = userDetails.getUser();
        return reservationService.createReservation(roomId, requestDto, user);
    }

    // 예약한 내역 확인 API
    @GetMapping("/reservation/{reservationId}")
    public ResponseEntity<ReservationResponseDto> reservationDetail(@PathVariable Long roomId,
                                                                    @PathVariable Long reservationId,
                                                                    @AuthenticationPrincipal UserDetailsImpl userDetails) throws AccessDeniedException {

        User user = userDetails.getUser();
        return reservationService.reservationDetail(roomId, reservationId, user);
    }

    // 예약 정보 수정 API
    @PutMapping("/reservation/{reservationId}")
    public ResponseEntity<ReservationResponseDto> reservationUpdate(@PathVariable Long roomId,
                                                                    @PathVariable Long reservationId,
                                                                    @RequestBody ReservationRequestDto requestDto,
                                                                    @AuthenticationPrincipal UserDetailsImpl userDetails) throws AccessDeniedException {

        User user = userDetails.getUser();
        return reservationService.reservationUpdate(roomId, reservationId, requestDto, user);
    }

    // 예약 취소 API
    @DeleteMapping("/reservation/{reservationId}")
    public ResponseEntity<String> reservationDelete(@PathVariable Long roomId,
                                                    @PathVariable Long reservationId,
                                                    @AuthenticationPrincipal UserDetailsImpl userDetails) throws AccessDeniedException {

        User user = userDetails.getUser();
        return reservationService.deleteReservation(roomId, reservationId, user);
    }
}
