package com.sparta.abnb.dto.responsedto;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public class ReservationResponseDto {
    private Long reservationId;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Long reservationNumber;
}
