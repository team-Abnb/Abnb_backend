package com.sparta.abnb.entity;

import com.sparta.abnb.dto.requestdto.MypageRequestDto;
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

    @Column
    private String phoneNumber;

    @Column
    private String bio;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserRole role;

    private Long kakaoId;

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

    @OneToOne(mappedBy = "user")
    private Mypage mypage;

    public void updateUser(User user, MypageRequestDto mypageRequestDto, String newPassword, String newProfilePicture) {
        this.email = user.getEmail();
        this.bio = mypageRequestDto.getBio();
        this.password = newPassword;
        this.profilePicture = newProfilePicture;
        this.role = user.getRole();
        this.username = mypageRequestDto.getUsername();
    }

    public User kakaoIdUpdate(Long kakaoId) {
        this.kakaoId = kakaoId;
        return this;
    }
}