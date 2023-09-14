package com.codemastersTournament.PersonnelManagerBot.utils;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class MessageUtils {
    //TODO ЭТОТ КЛАСС НУЖЕН ДЛЯ ДАЛЬНЕЙШЕГО РАСШИРЕНИЯ БОТА ( ПРИ ПРОВЕРКЕ НЕ УЧИТЫВАТЬ )
    public SendMessage generateSendMessageWithText(Update update, String text) {
        var message = update.getMessage();
        var sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.setText(text);
        return sendMessage;
    }
}