package com.bul.controller;

import com.bul.service.UpdateProducer;
import com.bul.utils.MessageUtils;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendSticker;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.bul.RabbitQueue.DOC_MESSAGE_UPDATE;
import static com.bul.RabbitQueue.PHOTO_MESSAGE_UPDATE;
import static com.bul.RabbitQueue.STICKER_MESSAGE_UPDATE;
import static com.bul.RabbitQueue.TEXT_MESSAGE_UPDATE;

@Component
@Log4j
public class UpdateController {
    private TelegramBot telegramBot;
    private final MessageUtils messageUtils;
    private final UpdateProducer updateProducer;
    private final int SIZE_LIMIT = 10_485_760;

    public UpdateController(MessageUtils messageUtils, UpdateProducer updateProducer) {
        this.messageUtils = messageUtils;
        this.updateProducer = updateProducer;
    }

    public void registerBot(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    public void processUpdate(Update update) {
        if (update == null) {
            log.error("Update is null");
            return;
        }

        if (update.hasMessage()) {
            distributeMessageByType(update);
        } else {
            log.error("Message is null");
        }
    }

    public void _processEditMessage(Update update) {
        if (update == null) {
            log.error("Update is null");
            return;
        }

        if (update.hasEditedMessage()) {
            log.debug("Edited: " + update.getEditedMessage().getText());
            setEditAnswer(update);
        } else {
            log.error("Edited message is null");
        }

    }

    private void setEditAnswer(Update update) {
        var sendMessage = messageUtils.generateSendMessageWithText(update, "Че меняем письма?");
        setView(sendMessage);
    }

    private void distributeMessageByType(Update update) {
        var message = update.getMessage();

        if (message.hasText()) {
            processTextMessage(update);
        } else if (message.hasDocument()) {
            processDocMessage(update);
        } else if (message.hasPhoto()) {
            processPhotoMessage(update);
        } else if (message.hasSticker()) {
            processStickerMessage(update);
        } else {
            setUnsupportedMessageTypeView(update);
        }
    }

    private void setUnsupportedMessageTypeView(Update update) {
        var sendMessage = messageUtils.generateSendMessageWithText(update,
                "Неподдерживаемый тип сообщений. Не балуйтесь!");

        setView(sendMessage);
    }

    private void setSizeLimitMessageView(Update update) {
        var sendMessage = messageUtils.generateSendMessageWithText(update,
                "Размер файла превышает " + SIZE_LIMIT / 1024 / 1024 + " Мб!");

        setView(sendMessage);
    }


    public void setView(SendMessage sendMessage) {
        telegramBot.sendAnswerMessage(sendMessage);
    }

    public void setViewSticker(SendSticker sendSticker) {
        telegramBot.sendAnswerSticker(sendSticker);
    }

    private void processStickerMessage(Update update) {
        if (update.getMessage().getSticker().getFileSize() <= SIZE_LIMIT) {
            updateProducer.produce(STICKER_MESSAGE_UPDATE, update);
        } else {
            setSizeLimitMessageView(update);
        }

    }

    private void processPhotoMessage(Update update) {
        int sizeSum = 0;
        for (var photo : update.getMessage().getPhoto()) {
            sizeSum += photo.getFileSize();
        }

        if (sizeSum <= SIZE_LIMIT) {
            updateProducer.produce(PHOTO_MESSAGE_UPDATE, update);
        } else {
            setSizeLimitMessageView(update);
        }
    }

    private void processDocMessage(Update update) {
        if (update.getMessage().getDocument().getFileSize() <= SIZE_LIMIT) {
            updateProducer.produce(DOC_MESSAGE_UPDATE, update);
        } else {
            setSizeLimitMessageView(update);
        }
    }

    private void processTextMessage(Update update) {
        updateProducer.produce(TEXT_MESSAGE_UPDATE, update);
    }


}