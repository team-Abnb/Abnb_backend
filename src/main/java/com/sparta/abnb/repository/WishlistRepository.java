package com.sparta.abnb.repository;

import com.sparta.abnb.entity.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WishlistRepository extends JpaRepository<Wishlist, Long> {
    Optional<Wishlist> findByUserIdAndRoomId(Long userId, Long targetRoomId);

    List<Wishlist> findByUserIdOrderByCreateAtDesc(Long userId);

}
