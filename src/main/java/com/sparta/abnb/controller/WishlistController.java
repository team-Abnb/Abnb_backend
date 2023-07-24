package com.sparta.abnb.controller;

import com.sparta.abnb.dto.requestdto.WishlistRequestDto;
import com.sparta.abnb.dto.responsedto.WishlistResponseDto;
import com.sparta.abnb.service.WishlistService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/wishlist")
public class WishlistController {

    private final WishlistService wishlistService;

    //좋아요 등록 / 삭제
    @PostMapping
    public ResponseEntity<String> createWishlist(
            @RequestBody WishlistRequestDto wishlistRequestDto, @AuthenticationPrincipal UserDetailsImpl userDetails ) throws AccessDeniedException {
        Long targetRoomId = wishlistRequestDto.getRoomId();
        return wishlistService.createWishlist(targetRoomId,userDetails.getUser());
    }

    //좋아요 리스트 조회
    @GetMapping
    public List<WishlistResponseDto> getWishlists(HttpServletRequest req) throws AccessDeniedException {
        return wishlistService.getWishlists(req);
    }

}
