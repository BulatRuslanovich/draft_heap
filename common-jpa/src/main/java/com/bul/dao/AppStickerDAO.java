package com.bul.dao;

import com.bul.entity.AppSticker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface AppStickerDAO extends JpaRepository<AppSticker, Long> {
    @Query(value = "SELECT * FROM app_sticker ORDER BY RANDOM() LIMIT 1", nativeQuery = true)
    Optional<AppSticker> findRandomAppSticker();

    Optional<AppSticker> findByTelegramFileUniqueId(String telegramFileId);
}
