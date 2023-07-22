package com.sparta.abnb.repository;

import com.sparta.abnb.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
