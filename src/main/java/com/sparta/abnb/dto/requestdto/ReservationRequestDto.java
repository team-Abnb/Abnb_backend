package com.sparta.abnb.dto.requestdto;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public class ReservationRequestDto {
    private String checkInDate;
    private String checkOutDate;
    private Long reservationNumber;
}
