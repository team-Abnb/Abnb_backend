package com.sparta.abnb.service;

import com.sparta.abnb.entity.Room;
import com.sparta.abnb.entity.User;
import com.sparta.abnb.entity.Wishlist;
import com.sparta.abnb.repository.RoomRepository;
import com.sparta.abnb.repository.UserRepository;
import com.sparta.abnb.repository.WishlistRepository;
import com.sparta.abnb.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WishlistService {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final WishlistRepository wishlistRepository;
    private final RoomRepository roomRepository;

    @Transactional
    public void createWishlist(Long targetRoomId, HttpServletRequest req){

        //token 받아서 - substring - 검증
        String token = req.getHeader(jwtUtil.HEADER_ACCESS_TOKEN);
        String tokenValue = jwtUtil.substringToken(token);
        if(!jwtUtil.validateAccessToken(tokenValue)){
            throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
        }

        //사용자 정보 가져오기
        Claims info = jwtUtil.getUserInfo(tokenValue);
        // 가져온 사용자 정보가 먼저 UserDB에 존재하는지 확인
        User user = userRepository.findByEmail(info.getSubject()).orElseThrow(()
                -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        // userId 뽑아오기
        Long userId = user.getUserId();
        // roomId 뽑아오기

        // userId에 해당하는 wishlist DB를 조회 후 userId 기준으로 roomId를 조회
        List<Wishlist> wishlists = wishlistRepository.findByUserIn(userId);
        Optional<Wishlist> roomIdInWishlists = wishlists.stream()
                .filter(wishlist -> wishlist.getRoom().getRoomId().equals(targetRoomId))
                .findFirst();

        // 조회했을 때 targetRoomId가 있으면 좋아요 취소;
        if(roomIdInWishlists.isPresent()){
            wishlistRepository.deleteById(roomIdInWishlists.get().getWishlistId());
            ResponseEntity.status(HttpStatus.OK).body("해당 ROOM의 좋아요를 취소하였습니다.");
            return;
        }
        // 조회했을 때 targetRoomId가 없으면 좋아요 등록
        Room targetRoom = roomRepository.findById(targetRoomId).orElseThrow(()->
                new IllegalArgumentException("해당 Room 이 존재하지 않습니다."));

        Wishlist wishlist = Wishlist.builder()
                .room(targetRoom)
                .user(user)
                .build();

        wishlistRepository.save(wishlist);

        ResponseEntity.status(HttpStatus.OK).body("해당 ROOM의 좋아요를 등록하였습니다.");

    }
}
