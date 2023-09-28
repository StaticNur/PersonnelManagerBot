package com.codemastersTournament.PersonnelManagerBot.controller.callbacks;

import com.codemastersTournament.PersonnelManagerBot.controller.sender.SubmittingAdditionalMessage;
import com.codemastersTournament.PersonnelManagerBot.models.Employee;
import com.codemastersTournament.PersonnelManagerBot.utils.StateForEmployeeData;
import com.codemastersTournament.PersonnelManagerBot.utils.enums.BotInputState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Map;
@Component
public class ChangeAvatarChooseCallback implements CallbackHandler{
    private final SubmittingAdditionalMessage message;
    @Autowired
    public ChangeAvatarChooseCallback(SubmittingAdditionalMessage message) {
        this.message = message;
    }

    @Override
    public void apply(Callback callback, Update update) {
        Long chatId = update.getCallbackQuery().getMessage().getChatId();

        //меняем ключь, сохроняя Employee
        Map.Entry<BotInputState, Employee> stateForEmployeeData = StateForEmployeeData.stateAndCard.entrySet().iterator().next();
        StateForEmployeeData.stateAndCard.clear();
        StateForEmployeeData.stateAndCard.put(BotInputState.WAITING_FOR_EMPLOYEE_DATA_FOR_EDIT_AVATAR, stateForEmployeeData.getValue());
        //return new SendMessage(chatId.toString(), "Отправьте фото:");
        message.sendMessage(new SendMessage(chatId.toString(), "Отправьте фото:"));

    }
}
