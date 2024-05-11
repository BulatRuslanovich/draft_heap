package com.bul.service;

import com.bul.dto.MailParams;

public interface ConsumerService {
    void consumeRegistrationMail(MailParams mailParams);
}