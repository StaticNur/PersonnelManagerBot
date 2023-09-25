package com.codemastersTournament.PersonnelManagerBot.controller.sender;

import com.codemastersTournament.PersonnelManagerBot.controller.TelegramBot;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@Log4j
public class MessageSender implements SubmittingAdditionalMessage {
    private TelegramBot telegramBot;
    public void registerBot(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }
    @Override
    public void sendMessage(Object messageObject) {
        if (messageObject instanceof SendMessage) {
            SendMessage sendMessage = (SendMessage) messageObject;
            try {
                telegramBot.execute(sendMessage);
            } catch (TelegramApiException e) {
                log.error(e);
            }
        } else if (messageObject instanceof SendPhoto) {
            SendPhoto sendPhoto = (SendPhoto) messageObject;
            try {
                telegramBot.execute(sendPhoto);
            } catch (TelegramApiException e) {
                log.error(e);
            }
        } else if (messageObject instanceof EditMessageText) {
            EditMessageText editMessageText = (EditMessageText) messageObject;
            try {
                telegramBot.execute(editMessageText);
            } catch (TelegramApiException e) {
                log.error(e);
            }
        } else {
            log.error("Получен неподдерживаемый тип сообщения: " + messageObject);
        }
    }
}