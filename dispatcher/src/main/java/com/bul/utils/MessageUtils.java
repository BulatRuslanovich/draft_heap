package com.bul.utils;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class MessageUtils {
    public SendMessage generateSendMessageWithText(Update update, String text) {
        var message = update.getMessage();
        var sendMessage = new SendMessage();

        if (message != null) {
            sendMessage.setChatId(message.getChatId());
            sendMessage.setText(text);
        } else {
            sendMessage.setChatId(update.getEditedMessage().getChatId());
            sendMessage.setText(text);
        }

        return sendMessage;
    }
}