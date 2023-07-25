package com.sparta.abnb.dto.requestdto;

import lombok.Getter;

@Getter
public class ReservationRequestDto {
    private String checkInDate;
    private String checkOutDate;
    private Long reservationNumber;
}
