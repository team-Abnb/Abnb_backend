package com.sparta.abnb.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "room_picture")
public class RoomPicture {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomPictuerId;

    @Column(nullable = false)
    private String roomPictureKey;

    @Column(nullable = false)
    private String urlLink;

    // user와 연관관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private Room room;

    public RoomPicture(String urlLink, Room room) {
        this.urlLink = urlLink;
        this.room = room;
    }
}
