package com.bul.service.impl;

import com.bul.dao.AppUserDAO;
import com.bul.dto.MailParams;
import com.bul.entity.AppUser;
import com.bul.enums.UserState;
import com.bul.service.AppUserService;
import com.bul.utils.CryptoTool;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

@Log4j
@RequiredArgsConstructor
@Service
public class AppUserServiceImpl implements AppUserService {
    private final AppUserDAO appUserDAO;
    private final CryptoTool cryptoTool;

    @Value("${spring.rabbitmq.queues.registration-mail}")
    private String registrationMailQueue;

    private final RabbitTemplate rabbitTemplate;

    @Override
    public String registerUser(AppUser appUser) {
        if (appUser.isActive()) {
            return "Вы уже зарегистрированы!";
        } else if (appUser.getEmail() != null) {
            return "Вам на почту уже было отправлено письмо. " +
                    "Если не нашли, оно может находиться в спаме";
        }

        appUser.setUserState(UserState.WAIT_FOR_EMAIL_STATE);
        appUserDAO.save(appUser);
        return "Введите, пожалуйста ваш email:";
    }

    @Override
    public String setEmail(AppUser appUser, String email) {
        try {
            var emailAddress = new InternetAddress(email);
            emailAddress.validate();
        } catch (AddressException e) {
            return "Введите, пожалуйста, корректный email. Для отмены команды введите /cancel";
        }

        var appUserOpt = appUserDAO.findByEmail(email);

        if (appUserOpt.isEmpty()) {
            appUser.setEmail(email);
            appUser.setUserState(UserState.BASIC_STATE);
            appUser = appUserDAO.save(appUser);

            var cryptoUserId = cryptoTool.hashOf(appUser.getId());
            sendRegistrationMail(cryptoUserId, email);

            return "Вам на почту было отправлено письмо."
                    + "Перейдите по ссылке в письме для подтверждения регистрации.";
        } else {
            return "Этот email уже занят. Введите другой email." +
                    " Для отмены команды введите /cancel";
        }
    }

    private void sendRegistrationMail(String cryptoUserId, String email) {
        var mailParams = MailParams.builder()
                .id(cryptoUserId)
                .emailTo(email)
                .build();

        rabbitTemplate.convertAndSend(registrationMailQueue, mailParams);
    }
}
