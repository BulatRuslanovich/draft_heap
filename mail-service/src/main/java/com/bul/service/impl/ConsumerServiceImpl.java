package com.bul.service.impl;

import com.bul.dto.MailParams;
import com.bul.service.ConsumerService;
import com.bul.service.MailSenderService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ConsumerServiceImpl implements ConsumerService {
    private final MailSenderService mailSenderService;

    @Override
    @RabbitListener(queues = "${spring.rabbitmq.queues.registration-mail}")
    public void consumeRegistrationMail(MailParams mailParams) {
        mailSenderService.send(mailParams);
    }
}
