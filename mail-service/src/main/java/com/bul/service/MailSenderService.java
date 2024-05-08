package com.bul.service;

import com.bul.dto.MailParams;

public interface MailSenderService {
    void send(MailParams mailParams);
}
