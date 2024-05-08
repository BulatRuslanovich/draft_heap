package com.bul.dao;

import com.bul.entity.AppSticker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface AppStickerDAO extends JpaRepository<AppSticker, Long> {

}
