package com.bul.dao;

import com.bul.entity.AppPhoto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppPhotoDAO extends JpaRepository<AppPhoto, Long> {
}
