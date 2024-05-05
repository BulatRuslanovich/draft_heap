package com.bul.exaptions;

import lombok.Getter;

@Getter
public class ExistStickerException extends RuntimeException {
    private String username;
    public ExistStickerException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExistStickerException(String message) {
        super(message);
    }
    public ExistStickerException(String message, String username) {
        super(message);
        this.username = username;
    }

    public ExistStickerException(Throwable cause) {
        super(cause);
    }
}
