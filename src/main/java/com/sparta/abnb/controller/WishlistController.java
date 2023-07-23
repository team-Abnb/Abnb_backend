package com.sparta.abnb.controller;

import com.sparta.abnb.dto.requestdto.WishlistRequestDto;
import com.sparta.abnb.service.WishlistService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class WishlistController {

    private final WishlistService wishlistService;

    //좋아요 등록 / 삭제
    @PostMapping("/wishlist")
    public void createWishlist(
            @RequestBody WishlistRequestDto wishlistRequestDto,
            HttpServletRequest req )
    {
        Long targetRoomId = wishlistRequestDto.getRoomId();
        wishlistService.createWishlist(targetRoomId,req);
    }

    //좋아요 리스트 조회
}
