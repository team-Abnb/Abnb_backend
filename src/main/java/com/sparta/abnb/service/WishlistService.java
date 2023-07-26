package com.sparta.abnb.service;

import com.sparta.abnb.dto.responsedto.WishlistResponseDto;
import com.sparta.abnb.entity.Room;
import com.sparta.abnb.entity.User;
import com.sparta.abnb.entity.Wishlist;
import com.sparta.abnb.repository.WishlistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final RoomService roomService;

    @Transactional
    //좋아요 등록 / 삭제
    public ResponseEntity<String> createWishlist(Long targetRoomId, User user){

        // userId에 해당하는 wishlist DB를 조회 후 userId roomId 기준으로 해당 좋아요를 조회
        Room targetRoom = roomService.findRoom(targetRoomId);
        Optional <Wishlist> check_wishlist = wishlistRepository.findByUserAndRoom(user,targetRoom);


        // 조회했을 때 targetRoomId가 있으면 좋아요 취소;
        if(check_wishlist.isPresent()){
            wishlistRepository.deleteById(check_wishlist.get().getWishlistId());
            return ResponseEntity.status(HttpStatus.OK).body("해당 ROOM의 좋아요를 취소하였습니다.");
        }
        // 조회했을 때 targetRoomId가 없으면 좋아요 등록
        Wishlist wishlist = Wishlist.builder()
                .room(targetRoom)
                .user(user)
                .build();

        wishlistRepository.save(wishlist);

        return ResponseEntity.status(HttpStatus.OK).body("해당 ROOM의 좋아요를 등록하였습니다.");

    }

    // 좋아요 리스트 조회
    public List<WishlistResponseDto> getWishlists(User user){

        List <Wishlist> wishlist = wishlistRepository.findAllByUserOrderByCreatedAtDesc(user);
        List<WishlistResponseDto> wishlistResponseDtos = wishlist.stream()
                .map(wish ->
                        WishlistResponseDto.builder()
                        .title(wish.getRoom().getTitle())
                        .address(wish.getRoom().getAddress())
                        .roomId(wish.getRoom().getRoomId())
                        .price(wish.getRoom().getPrice())
                        .thumbnailImg(roomService.findRoomPicture(wish.getRoom()).get(0).getUrlLink())
                        .isWishlist(true)
                        .build())
                .collect(Collectors.toList());

        return wishlistResponseDtos;
    }

}
