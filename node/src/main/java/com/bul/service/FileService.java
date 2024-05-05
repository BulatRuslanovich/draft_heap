package com.bul.service;

import com.bul.entity.AppDocument;
import com.bul.entity.AppPhoto;
import com.bul.entity.AppSticker;
import org.telegram.telegrambots.meta.api.objects.Message;

public interface FileService {
    AppDocument processDoc(Message telegramMessage);
    AppPhoto processPhoto(Message telegramMessage);
    AppSticker processSticker(Message telegramMessage);
}
