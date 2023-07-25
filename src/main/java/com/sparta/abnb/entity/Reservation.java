package com.sparta.abnb.entity;

import com.sparta.abnb.dto.requestdto.ReservationRequestDto;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Reservation extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reservationId;

    @Column
    private Long reservationNumber; // 예약 인원

    @Column(columnDefinition = "DATE")
    private LocalDate checkin; // 체크인

    @Column(columnDefinition = "DATE")
    private LocalDate checkout; // 체크아웃

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private Room room;

    public void update(ReservationRequestDto requestDto, LocalDate checkInDate, LocalDate checkOutDate, Room reservationRoom, User user) {
        this.reservationNumber = requestDto.getReservationNumber();
        this.checkin = checkInDate;
        this.checkout = checkOutDate;
        this.user = user;
        this.room = reservationRoom;
    }
}