package com.codemastersTournament.PersonnelManagerBot.controller.commands;

import com.codemastersTournament.PersonnelManagerBot.service.impl.AnswerConsumerImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class MenuCommand implements Command {
    private final AnswerConsumerImpl answerConsumer;
    @Autowired
    public MenuCommand(AnswerConsumerImpl answerConsumer) {
        this.answerConsumer = answerConsumer;
    }
    @Override
    public SendMessage apply(Update update) {
        //Генерируем сообщения меню
        Long chatId = 0L;
        if(update.hasMessage()){
            chatId = update.getMessage().getChatId();
        }else if(update.hasCallbackQuery()){
            chatId = update.getCallbackQuery().getMessage().getChatId();
        }
        return answerConsumer.generateNewMenuCommandMessage(chatId);
    }
}
