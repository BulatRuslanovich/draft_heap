package com.bul.controller;

import com.bul.configuration.RabbitConfig;
import com.bul.service.UpdateProducer;
import com.bul.utils.MessageUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Log4j
@RequiredArgsConstructor
@Component
public class UpdateProcessor {
    private TelegramBot telegramBot;
    private final MessageUtils messageUtils;
    private final UpdateProducer updateProducer;
    private final RabbitConfig rabbitConfig;

    private final int SIZE_LIMIT = 10_485_760;

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

    private void processStickerMessage(Update update) {
        if (update.getMessage().getSticker().getFileSize() <= SIZE_LIMIT) {
            updateProducer.produce(rabbitConfig.getStickerMessageUpdateQueue(), update);
        } else {
            setSizeLimitMessageView(update);
        }

    }

    private void processPhotoMessage(Update update) {
        var photoSize = update.getMessage().getPhoto().size();
        var photoIndex = photoSize > 1 ? photoSize - 1 : 0;

        if (update.getMessage().getPhoto().get(photoIndex).getFileSize() <= SIZE_LIMIT) {
            updateProducer.produce(rabbitConfig.getPhotoMessageUpdateQueue(), update);
        } else {
            setSizeLimitMessageView(update);
        }
    }

    private void processDocMessage(Update update) {
        if (update.getMessage().getDocument().getFileSize() <= SIZE_LIMIT) {
            updateProducer.produce(rabbitConfig.getDocMessageUpdateQueue(), update);
        } else {
            setSizeLimitMessageView(update);
        }
    }

    private void processTextMessage(Update update) {
        updateProducer.produce(rabbitConfig.getTextMessageUpdateQueue(), update);
    }
}
