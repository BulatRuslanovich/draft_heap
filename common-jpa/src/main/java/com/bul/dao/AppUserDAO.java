package com.bul.dao;

import com.bul.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.Nullable;

import java.util.Optional;

public interface AppUserDAO extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByTelegramUserId(Long id);
    @Override
    Optional<AppUser> findById(Long id);
    Optional<AppUser> findByEmail(String email);

}
