package com.codemastersTournament.PersonnelManagerBot.controller.callbacks;

import com.codemastersTournament.PersonnelManagerBot.controller.sender.SubmittingAdditionalMessage;
import com.codemastersTournament.PersonnelManagerBot.service.impl.AnswerConsumerImpl;
import com.codemastersTournament.PersonnelManagerBot.utils.StateForEmployeeData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class BackChooseCallback implements CallbackHandler{
    private final AnswerConsumerImpl answerConsumer;
    private final SubmittingAdditionalMessage message;
    @Autowired
    public BackChooseCallback(AnswerConsumerImpl answerConsumer, SubmittingAdditionalMessage message) {
        this.answerConsumer = answerConsumer;
        this.message = message;
    }
    @Override
    public void apply(Callback callback, Update update) {
        Long chatId = 0L;
        if(update.hasMessage()){
            chatId = update.getMessage().getChatId();
        }else if(update.hasCallbackQuery()){
            chatId = update.getCallbackQuery().getMessage().getChatId();
        }
        StateForEmployeeData.stateAndCard.clear();
        //return answerConsumer.generateNewMenuCommandMessage(chatId);
        message.sendMessage(answerConsumer.generateMenu(chatId));
    }
}
