package com.codemastersTournament.PersonnelManagerBot.controller.commands;

import com.codemastersTournament.PersonnelManagerBot.service.impl.AnswerConsumerImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
@Component
public class StopCommand implements Command  {
    private final AnswerConsumerImpl answerConsumer;
    @Autowired
    public StopCommand(AnswerConsumerImpl answerConsumer) {
        this.answerConsumer = answerConsumer;
    }
    @Override
    public SendMessage apply(Update update) {
        long chatId = update.getMessage().getChatId();
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("Сброщены все настройки");
        return message;
    }
}
