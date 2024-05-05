package com.bul.service.impl;

import com.bul.dao.*;
import com.bul.entity.*;
import com.bul.exaptions.ExistStickerException;
import com.bul.exaptions.UploadFileException;
import com.bul.service.FileService;
import lombok.extern.log4j.Log4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.stickers.Sticker;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;
import java.util.Optional;

@Log4j
@Service
public class FileServiceImpl implements FileService {
    @Value("${token}")
    private String token;
    // https://api.telegram.org/bot{bot_token}/sendSticker?chat_id={chat_id}&sticker={sticker}
    @Value("${service.file_info.uri}")
    private String fileInfoUri;

    @Value("${service.file_storage.uri}")
    private String fileStorageUri;

    private final AppDocumentDAO appDocumentDAO;
    private final AppPhotoDAO appPhotoDAO;
    private final AppStickerDAO appStickerDAO;
    private final BinaryContentDAO binaryContentDAO;
    private final AppUserDAO appUserDAO;

    public FileServiceImpl(AppDocumentDAO appDocumentDAO, AppPhotoDAO appPhotoDAO, AppStickerDAO appStickerDAO, BinaryContentDAO binaryContentDAO, AppUserDAO appUserDAO) {
        this.appDocumentDAO = appDocumentDAO;
        this.appPhotoDAO = appPhotoDAO;
        this.appStickerDAO = appStickerDAO;
        this.binaryContentDAO = binaryContentDAO;
        this.appUserDAO = appUserDAO;
    }

    @Override
    public AppDocument processDoc(Message telegramMessage) {
        var telegramDoc = telegramMessage.getDocument();
        var fileId = telegramDoc.getFileId();
        AppUser user = getAppUser(telegramMessage);

        var response = getFilePath(fileId);
        if (response.getStatusCode() == HttpStatus.OK) {
            var persistentBinaryContent = getPersistentBinaryContent(response);
            var transientAppDoc = buildTransientAppDoc(telegramDoc, persistentBinaryContent, user);
            return appDocumentDAO.save(transientAppDoc);
        } else {
            throw new UploadFileException("Bad response from telegram service: " + response);
        }
    }

    @Override
    public AppPhoto processPhoto(Message telegramMessage) {
        var photoSize = telegramMessage.getPhoto().size();
        var photoIndex = photoSize > 1 ? photoSize - 1 : 0;

        var telegramPhoto = telegramMessage.getPhoto().get(photoIndex);
        var fileId = telegramPhoto.getFileId();
        AppUser user = getAppUser(telegramMessage);

        var response = getFilePath(fileId);
        if (response.getStatusCode() == HttpStatus.OK) {
            var persistentBinaryContent = getPersistentBinaryContent(response);
            var transientAppPhoto = buildTransientAppPhoto(telegramPhoto, persistentBinaryContent, user);
            return appPhotoDAO.save(transientAppPhoto);
        } else {
            throw new UploadFileException("Bad response from telegram service: " + response);
        }
    }

    @Override
    public AppSticker processSticker(Message telegramMessage) {
        var telegramSticker = telegramMessage.getSticker();
        var fileId = telegramSticker.getFileId();
        var fileUniqueId = telegramSticker.getFileUniqueId();

        Optional<AppSticker> appSticker = appStickerDAO.findByTelegramFileUniqueId(fileUniqueId);

        if (appSticker.isPresent()) {
            throw new ExistStickerException("This sticker is exists: " + appSticker.get().getTelegramFileId(), appSticker.get().getAppUser().getUsername());
        }

        AppUser user = getAppUser(telegramMessage);

        var response = getFilePath(fileId);
        if (response.getStatusCode() == HttpStatus.OK) {
            var persistentBinaryContent = getPersistentBinaryContent(response);
            var transientAppSticker = buildTransientAppSticker(telegramSticker, persistentBinaryContent, user);
            return appStickerDAO.save(transientAppSticker);
        } else {
            throw new UploadFileException("Bad response from telegram service: " + response);
        }
    }



    private AppUser getAppUser(Message telegramMessage) {
        var userId = telegramMessage.getFrom().getId();
        return appUserDAO.findAppUserByTelegramUserId(userId);
    }

    private BinaryContent getPersistentBinaryContent(ResponseEntity<String> response) {
        var filePath = getFilePath(response);
        var fileInByte = downloadFile(filePath);
        var transientBinaryContent = BinaryContent.builder()
                .fileAsArrayOfBytes(fileInByte)
                .build();
        return binaryContentDAO.save(transientBinaryContent);
    }

    private ResponseEntity<String> getFilePath(String fileId) {
        var restTemplate = new RestTemplate();
        var headers = new HttpHeaders();
        var request = new HttpEntity<>(headers);

        return restTemplate.exchange(
                fileInfoUri,
                HttpMethod.GET,
                request,
                String.class,
                token, fileId
        );
    }

    private String getFilePath(ResponseEntity<String> response) {
        var jsonObject = new JSONObject(Objects.requireNonNull(response.getBody()));
        return String.valueOf(jsonObject
                .getJSONObject("result")
                .getString("file_path"));
    }



    private byte[] downloadFile(String filePath) {
        var fullUri = fileStorageUri.replace("{token}", token)
                .replace("{filePath}", filePath);
        URL urlObj;

        try {
            urlObj = new URL(fullUri);
        } catch (MalformedURLException e) {
            throw new UploadFileException(e);
        }

        try (InputStream is = urlObj.openStream()) {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int bytesRead;

            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }

            return os.toByteArray();
        } catch (IOException e) {
            throw new UploadFileException(urlObj.toExternalForm(), e);
        }
    }

    private AppDocument buildTransientAppDoc(Document telegramDoc, BinaryContent persistentBinaryContent, AppUser user) {
        return AppDocument.builder()
                .telegramFileId(telegramDoc.getFileId())
                .docName(telegramDoc.getFileName())
                .binaryContent(persistentBinaryContent)
                .mimeType(telegramDoc.getMimeType())
                .fileSize(telegramDoc.getFileSize())
                .appUser(user)
                .build();
    }

    private AppSticker buildTransientAppSticker(Sticker telegramSticker, BinaryContent persistentBinaryContent, AppUser user) {
        return AppSticker.builder()
                .telegramFileId(telegramSticker.getFileId())
                .telegramFileUniqueId(telegramSticker.getFileUniqueId())
                .emoji(telegramSticker.getEmoji())
                .isVideo(telegramSticker.getIsVideo())
                .isAnimated(telegramSticker.getIsAnimated())
                .appUser(user)
                .binaryContent(persistentBinaryContent)
                .fileSize(telegramSticker.getFileSize())
                .build();
    }

    private AppPhoto buildTransientAppPhoto(PhotoSize telegramPhoto, BinaryContent persistentBinaryContent, AppUser user) {
        return AppPhoto.builder()
                .telegramFileId(telegramPhoto.getFileId())
                .binaryContent(persistentBinaryContent)
                .fileSize(telegramPhoto.getFileSize())
                .appUser(user)
                .build();
    }



}
