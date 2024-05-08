package com.bul.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendSticker;

public interface AnswerConsumer {
    void consume(SendMessage sendMessage);
}
