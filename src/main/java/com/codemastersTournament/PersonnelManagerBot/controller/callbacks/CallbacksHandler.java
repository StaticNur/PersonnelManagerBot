package com.codemastersTournament.PersonnelManagerBot.controller.callbacks;

import com.codemastersTournament.PersonnelManagerBot.controller.sender.SubmittingAdditionalMessage;
import com.codemastersTournament.PersonnelManagerBot.utils.Consts;
import com.codemastersTournament.PersonnelManagerBot.utils.enums.ButtonCallBackQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.HashMap;
import java.util.Map;

@Component
public class CallbacksHandler {

    private final Map<ButtonCallBackQuery, CallbackHandler> callbacks;
    private final SubmittingAdditionalMessage message;
    public CallbacksHandler(@Autowired AddChooseCallback addChooseCallback,
                            @Autowired DeleteChooseCallback deleteChooseCallback,
                            @Autowired OpenCardChooseCallback openCardChooseCallback,
                            @Autowired SearchByFIOChooseCallback searchByFIOChooseCallback,
                            @Autowired SearchByProjectChooseCallback searchByProjectChooseCallback,
                            @Autowired SearchByPositionChooseCallback searchByPositionChooseCallback,
                            @Autowired SearchByProjectListChooseCallback searchByProjectListChooseCallback,
                            @Autowired SearchByPositionListChooseCallback searchByPositionListChooseCallback,
                            @Autowired SearchByDateChooseCallback searchByDateChooseCallback,
                            @Autowired ViewAllChooseCallback viewAllChooseCallback,
                            @Autowired BackChooseCallback backChooseCallback,
                            @Autowired ChangeNameChooseCallback changeNameChooseCallback,
                            @Autowired ChangePatronymicChooseCallback changePatronymicChooseCallback,
                            @Autowired ChangeLastNameChooseCallback changeLastNameChooseCallback,
                            @Autowired ChangePositionChooseCallback changePositionChooseCallback,
                            @Autowired ChangeProjectChooseCallback changeProjectChooseCallback,
                            @Autowired ChangeAvatarChooseCallback avatarChooseCallback, SubmittingAdditionalMessage message) {
        this.message = message;
        this.callbacks = new HashMap<>();
        this.callbacks.put(ButtonCallBackQuery.ADD_EMPLOYEE, addChooseCallback);
        this.callbacks.put(ButtonCallBackQuery.DELETE_EMPLOYEE, deleteChooseCallback);
        this.callbacks.put(ButtonCallBackQuery.OPEN_CARD_EMPLOYEE, openCardChooseCallback);
        this.callbacks.put(ButtonCallBackQuery.SEARCH_EMPLOYEE_BY_FIO, searchByFIOChooseCallback);
        this.callbacks.put(ButtonCallBackQuery.SEARCH_EMPLOYEE_BY_PROJECT, searchByProjectChooseCallback);
        this.callbacks.put(ButtonCallBackQuery.SEARCH_EMPLOYEE_BY_POSITION, searchByPositionChooseCallback);
        this.callbacks.put(ButtonCallBackQuery.SEARCH_EMPLOYEE_BY_PROJECT_LIST, searchByProjectListChooseCallback);
        this.callbacks.put(ButtonCallBackQuery.SEARCH_EMPLOYEE_BY_POSITION_LIST, searchByPositionListChooseCallback);
        this.callbacks.put(ButtonCallBackQuery.SEARCH_EMPLOYEE_BY_DATE, searchByDateChooseCallback);
        this.callbacks.put(ButtonCallBackQuery.VIEW_ALL, viewAllChooseCallback);
        this.callbacks.put(ButtonCallBackQuery.BACK, backChooseCallback);
        this.callbacks.put(ButtonCallBackQuery.CHANGE_NAME, changeNameChooseCallback);
        this.callbacks.put(ButtonCallBackQuery.CHANGE_PATRONYMIC, changePatronymicChooseCallback);
        this.callbacks.put(ButtonCallBackQuery.CHANGE_LAST_NAME, changeLastNameChooseCallback);
        this.callbacks.put(ButtonCallBackQuery.CHANGE_POSITION, changePositionChooseCallback);
        this.callbacks.put(ButtonCallBackQuery.CHANGE_PROJECT, changeProjectChooseCallback);
        this.callbacks.put(ButtonCallBackQuery.CHANGE_AVATAR, avatarChooseCallback);
    }
    public void handleCallbacks(Update update) {
        String dataCallback = update.getCallbackQuery().getData();
        long chatId = update.getCallbackQuery().getMessage().getChatId();

        if (dataCallback.isEmpty()) {
             message.sendMessage(new SendMessage(String.valueOf(chatId), Consts.ERROR));
        } else {
            Callback callback = Callback.builder()
                    .buttonCallBackQuery(ButtonCallBackQuery.valueOf(dataCallback))
                    .data(dataCallback)
                    .build();
            CallbackHandler callbackBiFunction = callbacks.get(callback.getButtonCallBackQuery());
            callbackBiFunction.apply(callback, update);
        }
    }

}