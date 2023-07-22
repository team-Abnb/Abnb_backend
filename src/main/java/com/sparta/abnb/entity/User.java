package com.sparta.abnb.entity;

import com.sparta.abnb.role.UserRole;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String username;

    @Column
    private String profilePicture;

    @Column(nullable = false)
    private String phoneNumber;

    @Column
    private String bio;

    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    // room과 연관관계
    @OneToMany(mappedBy = "user")
    private List<Room> rooms = new ArrayList<>();

    // 예약과 연관관계
    @OneToMany(mappedBy = "user")
    private List<Reservation> reservations = new ArrayList<>();

    // comment와 연관관계
    @OneToMany(mappedBy = "user")
    private List<Comment> comments = new ArrayList<>();

    // wishlist와 연관관계
    @OneToMany(mappedBy = "user")
    private List<Wishlist> wishlists = new ArrayList<>();

}