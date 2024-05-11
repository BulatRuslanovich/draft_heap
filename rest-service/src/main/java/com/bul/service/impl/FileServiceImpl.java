package com.bul.service.impl;

import com.bul.dao.AppDocumentDAO;
import com.bul.dao.AppPhotoDAO;
import com.bul.dao.AppStickerDAO;
import com.bul.entity.AppDocument;
import com.bul.entity.AppPhoto;
import com.bul.entity.AppSticker;
import com.bul.service.FileService;
import com.bul.utils.CryptoTool;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Service;

@Log4j
@Service
public class FileServiceImpl implements FileService {
    private final AppDocumentDAO appDocumentDAO;
    private final AppPhotoDAO appPhotoDAO;
    private final AppStickerDAO appStickerDAO;
    private final CryptoTool cryptoTool;

    public FileServiceImpl(AppDocumentDAO appDocumentDAO, AppPhotoDAO appPhotoDAO, AppStickerDAO appStickerDAO, CryptoTool cryptoTool) {
        this.appDocumentDAO = appDocumentDAO;
        this.appPhotoDAO = appPhotoDAO;
        this.appStickerDAO = appStickerDAO;
        this.cryptoTool = cryptoTool;
    }

    @Override
    public AppDocument getDocument(String hash) {
        var id = cryptoTool.idOf(hash);

        if (id == null) {
            return null;
        }

        return appDocumentDAO.findById(id).orElse(null);
    }

    @Override
    public AppPhoto getPhoto(String hash) {
        var id = cryptoTool.idOf(hash);

        if (id == null) {
            return null;
        }

        return appPhotoDAO.findById(id).orElse(null);
    }

    @Override
    public AppSticker getSticker(String hash) {
        var id = cryptoTool.idOf(hash);

        if (id == null) {
            return null;
        }

        return appStickerDAO.findById(id).orElse(null);
    }

}
