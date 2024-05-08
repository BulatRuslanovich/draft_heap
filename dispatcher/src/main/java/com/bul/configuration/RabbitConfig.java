package com.bul.configuration;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.bul.RabbitQueue.ANSWER_MESSAGE;
import static com.bul.RabbitQueue.DOC_MESSAGE_UPDATE;
import static com.bul.RabbitQueue.PHOTO_MESSAGE_UPDATE;
import static com.bul.RabbitQueue.STICKER_ANSWER_MESSAGE;
import static com.bul.RabbitQueue.STICKER_MESSAGE_UPDATE;
import static com.bul.RabbitQueue.TEXT_MESSAGE_UPDATE;


@Configuration
public class RabbitConfig {
    @Bean
    public MessageConverter jsonConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public Queue textMessageQueue() {
        return new Queue(TEXT_MESSAGE_UPDATE);
    }

    @Bean
    public Queue docMessageQueue() {
        return new Queue(DOC_MESSAGE_UPDATE);
    }

    @Bean
    public Queue photoMessageQueue() {
        return new Queue(PHOTO_MESSAGE_UPDATE);
    }

    @Bean
    public Queue stikerMessageQueue() {
        return new Queue(STICKER_MESSAGE_UPDATE);
    }

    @Bean
    public Queue answerQueue() {
        return new Queue(ANSWER_MESSAGE);
    }
}
