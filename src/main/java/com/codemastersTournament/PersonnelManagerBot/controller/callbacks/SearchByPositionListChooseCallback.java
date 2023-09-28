package com.codemastersTournament.PersonnelManagerBot.controller.callbacks;

import com.codemastersTournament.PersonnelManagerBot.controller.sender.SubmittingAdditionalMessage;
import com.codemastersTournament.PersonnelManagerBot.models.Employee;
import com.codemastersTournament.PersonnelManagerBot.service.impl.AnswerConsumerImpl;
import com.codemastersTournament.PersonnelManagerBot.utils.StateForEmployeeData;
import com.codemastersTournament.PersonnelManagerBot.utils.enums.BotInputState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class SearchByPositionListChooseCallback implements CallbackHandler{
    private final AnswerConsumerImpl answerConsumer;
    private final SubmittingAdditionalMessage message;
    @Autowired
    public SearchByPositionListChooseCallback(AnswerConsumerImpl answerConsumer, SubmittingAdditionalMessage message) {
        this.answerConsumer = answerConsumer;
        this.message = message;
    }
    @Override
    public void apply(Callback callback, Update update) {
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        StateForEmployeeData.stateAndCard.clear();
        StateForEmployeeData.stateAndCard.put(BotInputState.WAITING_FOR_EMPLOYEE_DATA_FOR_SEARCH_BY_POSITION_LIST,new Employee());
        //return answerConsumer.generateNewSearchCommandMessage(chatId);
        message.sendMessage(answerConsumer.generateSearchByPositionMessage(chatId));
    }
}