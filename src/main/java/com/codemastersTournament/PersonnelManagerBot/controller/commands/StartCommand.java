package com.codemastersTournament.PersonnelManagerBot.controller.commands;

import com.codemastersTournament.PersonnelManagerBot.controller.sender.SubmittingAdditionalMessage;
import com.codemastersTournament.PersonnelManagerBot.service.impl.AnswerConsumerImpl;
import com.codemastersTournament.PersonnelManagerBot.utils.MessageUtils;
import com.codemastersTournament.PersonnelManagerBot.utils.StateForEmployeeData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class StartCommand implements Command{
    private final SubmittingAdditionalMessage message;
    private final AnswerConsumerImpl answerConsumer;
    private final MessageUtils messageUtils;
    @Autowired
    public StartCommand(SubmittingAdditionalMessage message, AnswerConsumerImpl answerConsumer, MessageUtils messageUtils) {
        this.message = message;
        this.answerConsumer = answerConsumer;
        this.messageUtils = messageUtils;
    }

    @Override
    public void apply(Update update) {
        long chatId = update.getMessage().getChatId();
        var chat = update.getMessage().getChat();
        messageUtils.generateSendMessageWithText(update,"Привет, " + chat.getFirstName() +
                " " + chat.getLastName() + "! Я помогу тебе управленять информацией о сотрудниках." +
                "/admin - Войти как администратор" +
                "/user - Войти как обычный пользователь");
        message.sendMessage(messageUtils);

        //message.sendMessage(answerConsumer.generateNewMenuCommandMessage(chatId));
        StateForEmployeeData.stateAndCard.clear();
        StateForEmployeeData.role = null;
    }
}
