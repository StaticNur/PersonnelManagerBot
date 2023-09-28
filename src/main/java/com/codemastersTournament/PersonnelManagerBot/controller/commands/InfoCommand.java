package com.codemastersTournament.PersonnelManagerBot.controller.commands;

import com.codemastersTournament.PersonnelManagerBot.controller.sender.SubmittingAdditionalMessage;
import com.codemastersTournament.PersonnelManagerBot.service.impl.AnswerConsumerImpl;
import com.codemastersTournament.PersonnelManagerBot.utils.StateForEmployeeData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class InfoCommand implements Command{
    private final AnswerConsumerImpl answerConsumer;
    private final SubmittingAdditionalMessage message;
    @Autowired
    public InfoCommand(AnswerConsumerImpl answerConsumer, SubmittingAdditionalMessage message) {
        this.answerConsumer = answerConsumer;
        this.message = message;
    }
    @Override
    public void apply(Update update) {
        long chatId = update.getMessage().getChatId();
        //return answerConsumer.generateNewInfoMessage(chatId);
        StateForEmployeeData.stateAndCard.clear();
        message.sendMessage(answerConsumer.generateInfoMessage(chatId));

    }
}
