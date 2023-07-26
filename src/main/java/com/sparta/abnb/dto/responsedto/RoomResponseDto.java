package com.sparta.abnb.dto.responsedto;

import com.sparta.abnb.entity.Room;
import com.sparta.abnb.entity.RoomPicture;
import com.sparta.abnb.entity.User;
import lombok.Getter;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Getter
public class RoomResponseDto {
    private Long roomId;
    private String title;
    private String content;
    private Integer maxPeople;
    private List<String> roomPictures; // responsedto 가 필요없이 바로 service에서 string으로 줘도 되려나?
    private String theme;
    private Long price;
    private String address;
    private Long userId;
    private String userName;
    private String userProfilePicture;
    private Boolean isWishList; // isLike로 바꿔야 하나?
    private String createdAt;
    private String modifiedAt;

    public RoomResponseDto(Room room, User user, List<RoomPicture> roomPictures) {
        this.roomId = room.getRoomId();
        this.title = room.getTitle();
        this.content = room.getContent();
        this.maxPeople = room.getMaxPeople();
        this.roomPictures = roomPictures
                .stream()
                .map(RoomPicture::getUrlLink)
                .toList();
        this.theme = room.getTheme();
        this.price = room.getPrice();
        this.address = room.getAddress();
        this.userId = room.getUser().getUserId();
        this.userName = room.getUser().getUsername();
        this.userProfilePicture = getUserProfilePicture();
        this.isWishList = room.getWishlists()
                .stream()
                .anyMatch(wishlist ->
                        wishlist.hasUserId(user.getUserId())
                );
        this.createdAt = room.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        this.modifiedAt = room.getModifiedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }
}
