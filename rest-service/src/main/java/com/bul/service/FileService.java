package com.bul.service;

import com.bul.entity.AppDocument;
import com.bul.entity.AppPhoto;
import com.bul.entity.AppSticker;
import com.bul.entity.BinaryContent;
import org.springframework.core.io.FileSystemResource;

public interface FileService {
    AppDocument getDocument(String id);
    AppPhoto getPhoto(String id);
    AppSticker getSticker(String id);
    FileSystemResource getFileSystemResource(BinaryContent binaryContent);
}
