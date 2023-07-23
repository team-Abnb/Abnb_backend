package com.sparta.abnb.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@Getter
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

    @Column(nullable = false)
    private String homePicture;

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
    @OneToMany(mappedBy = "room")
    private List<Comment> comments = new ArrayList<>();

    // wishlist와 연관관계
    @OneToMany(mappedBy = "room")
    private List<Wishlist> wishlists = new ArrayList<>();
}
