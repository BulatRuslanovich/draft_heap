package com.bul.service.impl;

import com.bul.controller.UpdateController;
import com.bul.service.AnswerConsumer;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendSticker;

import static com.bul.RabbitQueue.ANSWER_MESSAGE;
import static com.bul.RabbitQueue.STICKER_ANSWER_MESSAGE;

@Service
public class AnswerConsumerImpl implements AnswerConsumer {
    private final UpdateController updateController;

    public AnswerConsumerImpl(UpdateController updateController) {
        this.updateController = updateController;
    }

    @Override
    @RabbitListener(queues = ANSWER_MESSAGE)
    public void consume(SendMessage sendMessage) {
        updateController.setView(sendMessage);
    }

    @Override
    @RabbitListener(queues = STICKER_ANSWER_MESSAGE)
    public void consumeSticker(SendSticker sendSticker) {
        updateController.setViewSticker(sendSticker);
    }


}
