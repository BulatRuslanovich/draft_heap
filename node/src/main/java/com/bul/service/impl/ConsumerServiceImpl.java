package com.bul.service.impl;

import com.bul.service.ConsumerService;
import com.bul.service.MainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

@Log4j
@RequiredArgsConstructor
@Service
public class ConsumerServiceImpl implements ConsumerService {
    private final MainService mainService;

    @Override
    @RabbitListener(queues = "${spring.rabbitmq.queues.text-message-update}")
    public void consumerTextMessageUpdates(Update update) {
        log.debug("NODE: Text message is received");
        mainService.processTextMessage(update);
    }

    @Override
    @RabbitListener(queues = "${spring.rabbitmq.queues.doc-message-update}")
    public void consumerDocMessageUpdates(Update update) {
        log.debug("NODE: Doc message is received");
        mainService.processDocMessage(update);
    }

    @Override
    @RabbitListener(queues = "${spring.rabbitmq.queues.photo-message-update}")
    public void consumerPhotoMessageUpdates(Update update) {
        log.debug("NODE: Photo message is received");
        mainService.processPhotoMessage(update);
    }

    @Override
    @RabbitListener(queues = "${spring.rabbitmq.queues.sticker-message-update}")
    public void consumerStickerMessageUpdates(Update update) {
        log.debug("NODE: Sticker message is received");
        mainService.processStickerMessage(update);
    }
}
