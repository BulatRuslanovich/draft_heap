package com.bul.dao;

import com.bul.entity.AppDocument;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppDocumentDAO extends JpaRepository<AppDocument, Long> {}
