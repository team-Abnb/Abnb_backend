package com.sparta.abnb.controller;

import com.sparta.abnb.dto.requestdto.ReservationRequestDto;
import com.sparta.abnb.dto.responsedto.ReservationResponseDto;
import com.sparta.abnb.entity.User;
import com.sparta.abnb.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rooms/{roomId}")
@RequiredArgsConstructor
public class ReservationController {
    private ReservationService reservationService;

    // 예약하기 API
    @PostMapping("/reseravtion")
    public ReservationResponseDto createReservation(@PathVariable Long roomId,
                                                    @RequestBody ReservationRequestDto requestDto,
                                                    @AuthenticationPrincipal UserDetailsImpl userDetails) {
        User user = userDetails.getUser;
        return reservationService.createReservation(roomId, requestDto, user);
    }

    // 예약한 내역 확인 API
    @GetMapping("/reservation/{reservationId}")
    public ReservationResponseDto reservationDetail(@PathVariable Long roomId,
                                                    @PathVariable Long reservationId,
                                                    @AuthenticationPrincipal UserDetailsImpl userDetails) {

        User user = userDetails.getUser;
        return reservationService.reservationDetail(roomId, reservationId, user);
    }

    // 예약 정보 수정 API
    @PutMapping("/reservation/{reservationId}")
    public ReservationResponseDto reservationUpdate(@PathVariable Long roomId,
                                                    @PathVariable Long reservationId,
                                                    @RequestBody ReservationRequestDto requestDto
                                                    @AuthenticationPrincipal UserDetailsImpl userDetails) {
        User user = userDetails.getUser;
        return reservationService.reservationUpdate(roomId, reservationId, requestDto, user);
    }
}
