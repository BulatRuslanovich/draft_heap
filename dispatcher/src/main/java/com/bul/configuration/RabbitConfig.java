package com.bul.configuration;

import lombok.Getter;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;

@Getter
@Configuration
public class RabbitConfig {
    @Value("${spring.rabbitmq.queues.text-message-update}")
    private String textMessageUpdateQueue;

    @Value("${spring.rabbitmq.queues.doc-message-update}")
    private String docMessageUpdateQueue;

    @Value("${spring.rabbitmq.queues.photo-message-update}")
    private String photoMessageUpdateQueue;

    @Value("${spring.rabbitmq.queues.sticker-message-update}")
    private String stickerMessageUpdateQueue;

    @Value("${spring.rabbitmq.queues.answer-message}")
    private String answerMessageQueue;

    @Bean
    public MessageConverter jsonConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public Queue textMessageQueue() {
        return new Queue(textMessageUpdateQueue);
    }

    @Bean
    public Queue docMessageQueue() {
        return new Queue(docMessageUpdateQueue);
    }

    @Bean
    public Queue photoMessageQueue() {
        return new Queue(photoMessageUpdateQueue);
    }

    @Bean
    public Queue stikerMessageQueue() {
        return new Queue(stickerMessageUpdateQueue);
    }

    @Bean
    public Queue answerQueue() {
        return new Queue(answerMessageQueue);
    }
}
