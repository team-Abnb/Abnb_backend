package com.sparta.abnb.dto.responsedto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class ReservationResponseDto {
    private Long reservationId;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate checkInDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate checkOutDate;
    private Long reservationNumber;
    private String username;
    private String roomTitle;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
}
