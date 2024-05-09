package com.bul.service.impl;

import com.bul.dao.AppStickerDAO;
import com.bul.dao.AppUserDAO;
import com.bul.entity.AppDocument;
import com.bul.entity.AppPhoto;
import com.bul.entity.AppSticker;
import com.bul.entity.AppUser;
import com.bul.enums.LinkType;
import com.bul.enums.ServiceCommands;
import com.bul.enums.UserState;
import com.bul.exaptions.UnknownUserException;
import com.bul.exaptions.UploadFileException;
import com.bul.service.AppUserService;
import com.bul.service.FileService;
import com.bul.service.MainService;
import com.bul.service.ProducerService;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.Optional;

import static com.bul.enums.ServiceCommands.CANCEL;
import static com.bul.enums.ServiceCommands.HELP;
import static com.bul.enums.ServiceCommands.REGISTRATION;
import static com.bul.enums.ServiceCommands.START;
import static com.bul.enums.UserState.BASIC_STATE;
import static com.bul.enums.UserState.WAIT_FOR_EMAIL_STATE;

@Log4j
@Service
public class MainServiceImpl implements MainService {
    private final ProducerService producerService;
    private final AppUserDAO appUserDAO;
    private final FileService fileService;
    private final AppUserService appUserService;

    public MainServiceImpl(ProducerService producerService, AppUserDAO appUserDAO, AppStickerDAO appStickerDAO, FileService fileService, AppUserService appUserService) {
        this.producerService = producerService;
        this.appUserDAO = appUserDAO;
        this.fileService = fileService;
        this.appUserService = appUserService;
    }

    @Override
    public void processTextMessage(Update update) {
        var appUser = findOrSaveAppUser(update);
        UserState state = appUser.getUserState();
        var text = update.getMessage().getText();

        var output = "";
        var serviceCommand = ServiceCommands.fromValue(text);

        if (CANCEL.equals(serviceCommand)) {
            output = cancelProcess(appUser);
        } else if (BASIC_STATE.equals(state)) {
            output = processServiceCommand(appUser, text);
        } else if (WAIT_FOR_EMAIL_STATE.equals(state)) {
            output = appUserService.setEmail(appUser, text);
        } else  {
            log.error("Unknown user state: " + state);
            output = "Unknown user state";
        }

        var chatId = update.getMessage().getChatId();
        sendAnswer(output, chatId);
    }

    @Override
    public void processDocMessage(Update update) {
        var appUser = findOrSaveAppUser(update);
        var chatId = update.getMessage().getChatId();

        if (isNotAllowToSendContent(chatId, appUser)) {
            return;
        }

        try {
            AppDocument doc = fileService.processDoc(update.getMessage());
            var link = fileService.generateLink(doc.getId(), LinkType.GET_DOC);
            var answer = "Документ успешно загружен! " +
                    "Ссылка для скачивания: " + link;
            sendAnswer(answer, chatId);
        } catch (UploadFileException e) {
            log.error(e);
            String error = "К сожалению, загрузка файла не удалась. Повторите попытку позже.";
            sendAnswer(error, chatId);
        }
    }

    @Override
    public void processPhotoMessage(Update update) {
        var appUser = findOrSaveAppUser(update);
        var chatId = update.getMessage().getChatId();

        if (isNotAllowToSendContent(chatId, appUser)) {
            return;
        }

        try {
            AppPhoto photo = fileService.processPhoto(update.getMessage());
            var link = fileService.generateLink(photo.getId(), LinkType.GET_PHOTO);
            var answer = "Фото успешно загружен! " +
                    "Ссылка для скачивания: " + link;
            sendAnswer(answer, chatId);
        } catch (UploadFileException e) {
            log.error(e);
            String error = "К сожалению, загрузка файла не удалась. Повторите попытку позже.";
            sendAnswer(error, chatId);
        }
    }

    @Override
    public void processStickerMessage(Update update) {
        var appUser = findOrSaveAppUser(update);
        var chatId = update.getMessage().getChatId();

        if (isNotAllowToSendContent(chatId, appUser)) {
            return;
        }


        try {
            AppSticker sticker = fileService.processSticker(update.getMessage());
            var link = fileService.generateLink(sticker.getId(), LinkType.GET_STICKER);
            var answer = "Стикер успешно загружен! " +
                    "Ссылка для скачивания: " + link;
            sendAnswer(answer, chatId);
        } catch (UploadFileException e) {
            log.error(e);
            String error = "К сожалению, загрузка стикера не удалась. Повторите попытку позже.";
            sendAnswer(error, chatId);
        } catch (UnknownUserException e) {
            log.error(e);
            String error = "К сожалению, вас нет в наших списках, ваше документ не может быть загружен :(";
            sendAnswer(error, chatId);
        }
    }

    private boolean isNotAllowToSendContent(Long chatId, AppUser appUser) {
        var userState = appUser.getUserState();

        if (!appUser.isActive()) {
            var error = "Зарегистрируйтесь или активируйте свою учетную запись для загрузки контента.";
            sendAnswer(error, chatId);
            return true;
        } else if (!BASIC_STATE.equals(userState)) {
            var error = "Отмените текущую команду с помощью /cancel для отправки файлов.";
            sendAnswer(error, chatId);
            return true;
        }

        return false;
    }

    private String processServiceCommand(AppUser appUser, String text) {
        var serviceCommand = ServiceCommands.fromValue(text);

        if (REGISTRATION.equals(serviceCommand)) {
            return appUserService.registerUser(appUser);
        } else if (HELP.equals(serviceCommand)) {
            return help();
        } else if (START.equals(serviceCommand)) {
            return "Добрый день! Для просмотра списка команд введите /help";
        } else {
            return "Такой команды нет, введите /help, чтобы узнать о доступных командах";
        }
    }

    private String help() {
        return "Вот список доступных команд:\n" +
                "/start - Начать взаимодействие\n" +
                "/help - Показать список доступных команд\n" +
                "/registration - Зарегистрироваться\n" +
                "/cancel - Отменить текущее действие";
    }

    private String cancelProcess(AppUser appUser) {
        appUser.setUserState(BASIC_STATE);
        appUserDAO.save(appUser);
        return "Команда отменена!";
    }

    private void sendAnswer(String output, Long chatId) {
        var sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(output);
        producerService.producerAnswer(sendMessage);
    }

    private AppUser findOrSaveAppUser(Update update) {
        User telegramUser = update.getMessage().getFrom();
        Optional<AppUser> optional = appUserDAO.findByTelegramUserId(telegramUser.getId());

        if (optional.isEmpty()) {
            AppUser transientAppUser = AppUser.builder()
                    .telegramUserId(telegramUser.getId())
                    .username(telegramUser.getUserName())
                    .firstname(telegramUser.getFirstName())
                    .lastname(telegramUser.getLastName())
                    .isActive(false)
                    .userState(BASIC_STATE)
                    .build();

            return appUserDAO.save(transientAppUser);
        }

        return optional.get();
    }
}
