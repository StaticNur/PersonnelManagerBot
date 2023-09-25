package com.codemastersTournament.PersonnelManagerBot.controller.callbacks;

import com.codemastersTournament.PersonnelManagerBot.service.impl.AnswerConsumerImpl;
import com.codemastersTournament.PersonnelManagerBot.utils.StateForEmployeeData;
import com.codemastersTournament.PersonnelManagerBot.utils.enums.BotInputState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class AddChooseCallback implements CallbackHandler{
    private final AnswerConsumerImpl answerConsumer;
    @Autowired
    public AddChooseCallback(AnswerConsumerImpl answerConsumer) {
        this.answerConsumer = answerConsumer;
    }

    @Override
    public SendMessage apply(Callback callback, Update update) {
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        StateForEmployeeData.stateAndCard.clear();
        StateForEmployeeData.stateAndCard.put(BotInputState.WAITING_FOR_EMPLOYEE_DATA_FOR_ADD, null);
        return answerConsumer.generateNewAddCommandMessage(chatId);
    }
}
