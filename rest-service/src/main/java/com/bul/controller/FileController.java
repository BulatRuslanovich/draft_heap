package com.bul.controller;

import com.bul.service.FileService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/file")
@RestController
public class FileController {
    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/get-doc")
    public ResponseEntity<?> getDoc(@RequestParam("id") String id) {
        var doc = fileService.getDocument(id);

        if (doc == null) {
            return ResponseEntity.badRequest().build(); // возможно ошибка пользователя
        }

        var binaryContent = doc.getBinaryContent();
        var fileSystemResource = fileService.getFileSystemResource(binaryContent);

        if (fileSystemResource == null) {
            return ResponseEntity.internalServerError().build(); // ошибка на нашей стороне
        }

        return ResponseEntity
                .ok()
                .contentType(MediaType.valueOf(doc.getMimeType()))
                .header("Content-disposition", "attachment; filename=" + doc.getDocName())
                .body(fileSystemResource);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/get-photo")
    public ResponseEntity<?> getPhoto(@RequestParam("id") String id) {
        var photo = fileService.getPhoto(id);

        if (photo == null) {
            return ResponseEntity.badRequest().build(); // возможно ошибка пользователя
        }

        var binaryContent = photo.getBinaryContent();
        var fileSystemResource = fileService.getFileSystemResource(binaryContent);

        if (fileSystemResource == null) {
            return ResponseEntity.internalServerError().build(); // ошибка на нашей стороне
        }

        return ResponseEntity
                .ok()
                .contentType(MediaType.IMAGE_JPEG)
                .header("Content-disposition", "attachment;")
                .body(fileSystemResource);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/get-sticker")
    public ResponseEntity<?> getSticker(@RequestParam("id") String id) {
        var sticker = fileService.getSticker(id);

        if (sticker == null) {
            return ResponseEntity.badRequest().build(); // возможно ошибка пользователя
        }

        var binaryContent = sticker.getBinaryContent();
        var fileSystemResource = fileService.getFileSystemResource(binaryContent);

        if (fileSystemResource == null) {
            return ResponseEntity.internalServerError().build(); // ошибка на нашей стороне
        }

        return ResponseEntity
                .ok()
                .contentType(MediaType.IMAGE_JPEG)
                .header("Content-disposition", "attachment;")
                .body(fileSystemResource);
    }


}
