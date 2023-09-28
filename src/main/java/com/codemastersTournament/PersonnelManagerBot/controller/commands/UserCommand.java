package com.codemastersTournament.PersonnelManagerBot.controller.commands;

import com.codemastersTournament.PersonnelManagerBot.controller.sender.SubmittingAdditionalMessage;
import com.codemastersTournament.PersonnelManagerBot.service.impl.AnswerConsumerImpl;
import com.codemastersTournament.PersonnelManagerBot.utils.MessageUtils;
import com.codemastersTournament.PersonnelManagerBot.utils.StateForEmployeeData;
import com.codemastersTournament.PersonnelManagerBot.utils.enums.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class UserCommand implements Command{
    private final AnswerConsumerImpl answerConsumer;
    private final SubmittingAdditionalMessage message;
    private final MessageUtils messageUtils;
    @Autowired
    public UserCommand(AnswerConsumerImpl answerConsumer, SubmittingAdditionalMessage message, MessageUtils messageUtils) {
        this.answerConsumer = answerConsumer;
        this.message = message;
        this.messageUtils = messageUtils;
    }
    @Override
    public void apply(Update update) {
        long chatId = update.getMessage().getChatId();
        //return answerConsumer.generateNewInfoMessage(chatId);
        StateForEmployeeData.stateAndCard.clear();
        StateForEmployeeData.role = Role.USER;
        message.sendMessage(messageUtils.generateSendMessageWithText(update,"Вы вошли как обычный пользователь"));
        message.sendMessage(answerConsumer.generateMenu(chatId));
    }
}
