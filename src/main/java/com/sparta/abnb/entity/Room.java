package com.sparta.abnb.entity;

import com.sparta.abnb.dto.requestdto.RoomRequestDto;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
//@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "room")
public class Room extends Timestamped{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 10000)
    private String content;

    @Column(nullable = false)
    private Integer maxPeople;

    // room을 먼저 만들고 이후에 roompictures를 추가하면서 자연스럽게 roomPictures 부분은 채워짐
    @OneToMany(mappedBy = "room")
    private List<RoomPicture> roomPictures = new ArrayList<>();

    @Column(nullable = false)
    private String theme;

    @Column(nullable = false)
    private Long price;

    @Column(nullable = false)
    private String address;

    // user와 연관관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // comment와 연관관계
    @OneToMany(mappedBy = "room", cascade = CascadeType.REMOVE)
    private List<Comment> comments = new ArrayList<>();

    // wishlist와 연관관계
    @OneToMany(mappedBy = "room", cascade = CascadeType.REMOVE)
    private List<Wishlist> wishlists = new ArrayList<>();

    public Room(RoomRequestDto requestDto, User user) {
        this.title = requestDto.getTitle();
        this.content = requestDto.getContent();
        this.maxPeople = requestDto.getMaxPeople();
        this.theme = requestDto.getTheme();
        this.price = requestDto.getPrice();
        this.address = requestDto.getAddress();

        this.user = user;
    }

    public void update(RoomRequestDto requestDto) {
        this.title = requestDto.getTitle();
        this.content = requestDto.getContent();
        this.maxPeople = requestDto.getMaxPeople();
        this.theme = requestDto.getTheme();
        this.price = requestDto.getPrice();
        this.address = requestDto.getAddress();
    }
}
