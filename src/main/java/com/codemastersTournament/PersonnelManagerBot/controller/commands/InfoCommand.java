package com.codemastersTournament.PersonnelManagerBot.controller.commands;

import com.codemastersTournament.PersonnelManagerBot.service.impl.AnswerConsumerImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class InfoCommand implements Command{
    private final AnswerConsumerImpl answerConsumer;
    @Autowired
    public InfoCommand(AnswerConsumerImpl answerConsumer) {
        this.answerConsumer = answerConsumer;
    }
    @Override
    public SendMessage apply(Update update) {
        long chatId = update.getMessage().getChatId();
        return answerConsumer.generateNewInfoMessage(chatId);
    }
}
