package com.sparta.abnb.service;

import com.sparta.abnb.dto.responsedto.WishlistResponseDto;
import com.sparta.abnb.entity.Room;
import com.sparta.abnb.entity.User;
import com.sparta.abnb.entity.Wishlist;
import com.sparta.abnb.repository.RoomRepository;
import com.sparta.abnb.repository.UserRepository;
import com.sparta.abnb.repository.WishlistRepository;
import com.sparta.abnb.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WishlistService {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final WishlistRepository wishlistRepository;
    private final RoomService roomService;

    @Transactional
    //좋아요 등록 / 삭제
    public ResponseEntity<String> createWishlist(Long targetRoomId, User user) throws AccessDeniedException {

//        //token 받아서 - substring - 검증
//         String tokenValue = checkToken(req);
//        //사용자 정보 가져오기
//        Claims info = jwtUtil.getUserInfo(tokenValue);
//        // 가져온 사용자 정보가 UserDB에 존재하는지 확인
//        User user = findUserMethod(info);

        Long userId = user.getUserId();
        // userId에 해당하는 wishlist DB를 조회 후 userId roomId 기준으로 해당 좋아요를 조회
        Optional <Wishlist> check_wishlist = wishlistRepository.findByUserIdAndRoomId(userId,targetRoomId);

//        Optional<Wishlist> roomIdInWishlists = wishlists.stream()
//                .filter(wishlist -> wishlist.getRoom().getRoomId().equals(targetRoomId))
//                .findFirst();


        // 조회했을 때 targetRoomId가 있으면 좋아요 취소;
        if(check_wishlist.isPresent()){
            wishlistRepository.deleteById(check_wishlist.get().getWishlistId());
            return ResponseEntity.status(HttpStatus.OK).body("해당 ROOM의 좋아요를 취소하였습니다.");
        }
        // 조회했을 때 targetRoomId가 없으면 좋아요 등록
        Room targetRoom = roomRepository.findById(targetRoomId).orElseThrow(()->
                new IllegalArgumentException("해당 Room 이 존재하지 않습니다."));

        Wishlist wishlist = Wishlist.builder()
                .room(targetRoom)
                .user(user)
                .build();

        wishlistRepository.save(wishlist);

        return ResponseEntity.status(HttpStatus.OK).body("해당 ROOM의 좋아요를 등록하였습니다.");

    }

    // 좋아요 리스트 조회
    public List<WishlistResponseDto> getWishlists(User user) throws AccessDeniedException {

//        //token 받아서 - substring - 검증
//        String tokenValue = checkToken(req);
//        Claims info = jwtUtil.getUserInfo(tokenValue);
//
//        // 가져온 사용자 정보가 UserDB에 존재하는지 확인
//        // 여기서 주의할점! 썸네일 이미지를 하나 어떻게 할 것인지를 정해야함.
//        User checkUser = findUserMethod(user);

        List <Wishlist> wishlist = wishlistRepository.findByUserIdOrderByCreateAtDesc(user.getUserId());
        List<WishlistResponseDto> wishlistResponseDtos = wishlist.stream()
                .map(wish ->
                        WishlistResponseDto.builder()
                        .title(wish.getRoom().getTitle())
                        .address(wish.getRoom().getAddress())
                        .roomId(wish.getRoom().getRoomId())
                        .price(wish.getRoom().getPrice())
                        .thumbnailImg(wish.getRoom().getRoomPictures().get(0).getUrlLink())
                        .isWishlist(true)
                        .build())
                .collect(Collectors.toList());

        return wishlistResponseDtos;
    }

    // 토큰을 검증하는 메소드 <token -> substring -> 검증>
//    private String checkToken(HttpServletRequest req) throws AccessDeniedException {
//        String token = req.getHeader(jwtUtil.HEADER_ACCESS_TOKEN);
//        String tokenValue = jwtUtil.substringToken(token);
//        if (!jwtUtil.validateAccessToken(tokenValue)) {
//            throw new AccessDeniedException("유효하지 않은 토큰입니다.");
//        }
//         return tokenValue;
//    }

    // user가 존재하는지 확인하는 메서드
//    private User findUserMethod(User user) {
//          return userRepository.findByEmail(user.getEmail()).orElseThrow(()
//                  ->  new IllegalArgumentException("해당 USER가 존재하지 않습니다."));
//    }
}
