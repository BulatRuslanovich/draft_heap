package com.bul.service.impl;

import com.bul.service.ProducerService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendSticker;

import static com.bul.RabbitQueue.ANSWER_MESSAGE;
import static com.bul.RabbitQueue.STICKER_ANSWER_MESSAGE;

@Service
public class ProducerServiceImpl implements ProducerService {
    private final RabbitTemplate rabbitTemplate;

    public ProducerServiceImpl(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void producerAnswer(SendMessage sendMessage) {
        rabbitTemplate.convertAndSend(ANSWER_MESSAGE, sendMessage);
    }

    @Override
    public void producerSticker(SendSticker sendSticker) {
        rabbitTemplate.convertAndSend(STICKER_ANSWER_MESSAGE, sendSticker);
    }
}
