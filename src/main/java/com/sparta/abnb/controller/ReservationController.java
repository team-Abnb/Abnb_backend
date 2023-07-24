package com.sparta.abnb.controller;

import com.sparta.abnb.dto.requestdto.ReservationRequestDto;
import com.sparta.abnb.dto.responsedto.ReservationResponseDto;
import com.sparta.abnb.entity.User;
import com.sparta.abnb.service.ReservationService;
import lombok.AllArgsConstructor;
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
                                                    @AuthenticationPrincipal UserDetailsImpl userDetails){
        User user = userDetails.getUser;
        return reservationService.createReservation(roomId, requestDto, user);
    }
}
