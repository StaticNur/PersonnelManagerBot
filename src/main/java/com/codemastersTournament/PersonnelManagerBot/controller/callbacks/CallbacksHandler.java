package com.codemastersTournament.PersonnelManagerBot.controller.callbacks;

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
    public CallbacksHandler(@Autowired AddChooseCallback addChooseCallback,
                            @Autowired DeleteChooseCallback deleteChooseCallback,
                            @Autowired OpenCardChooseCallback openCardChooseCallback,
                            @Autowired SearchChooseCallback searchChooseCallback,
                            @Autowired BackChooseCallback backChooseCallback,
                            @Autowired ChangeNameChooseCallback changeNameChooseCallback,
                            @Autowired ChangePatronymicChooseCallback changePatronymicChooseCallback,
                            @Autowired ChangeLastNameChooseCallback changeLastNameChooseCallback,
                            @Autowired ChangePositionChooseCallback changePositionChooseCallback,
                            @Autowired ChangeProjectChooseCallback changeProjectChooseCallback,
                            @Autowired ChangeAvatarChooseCallback avatarChooseCallback) {
        this.callbacks = new HashMap<>();
        this.callbacks.put(ButtonCallBackQuery.ADD_EMPLOYEE, addChooseCallback);
        this.callbacks.put(ButtonCallBackQuery.DELETE_EMPLOYEE, deleteChooseCallback);
        this.callbacks.put(ButtonCallBackQuery.OPEN_CARD_EMPLOYEE, openCardChooseCallback);
        this.callbacks.put(ButtonCallBackQuery.SEARCH_EMPLOYEE, searchChooseCallback);
        this.callbacks.put(ButtonCallBackQuery.BACK, backChooseCallback);
        this.callbacks.put(ButtonCallBackQuery.CHANGE_NAME, changeNameChooseCallback);
        this.callbacks.put(ButtonCallBackQuery.CHANGE_PATRONYMIC, changePatronymicChooseCallback);
        this.callbacks.put(ButtonCallBackQuery.CHANGE_LAST_NAME, changeLastNameChooseCallback);
        this.callbacks.put(ButtonCallBackQuery.CHANGE_POSITION, changePositionChooseCallback);
        this.callbacks.put(ButtonCallBackQuery.CHANGE_PROJECT, changeProjectChooseCallback);
        this.callbacks.put(ButtonCallBackQuery.CHANGE_AVATAR, avatarChooseCallback);

    }
    public SendMessage handleCallbacks(Update update) {
        String dataCallback = update.getCallbackQuery().getData();
        long chatId = update.getCallbackQuery().getMessage().getChatId();

        SendMessage answer;
        if (dataCallback.isEmpty()) {
            answer = new SendMessage(String.valueOf(chatId), Consts.ERROR);
        } else {
            Callback callback = Callback.builder()
                    .buttonCallBackQuery(ButtonCallBackQuery.valueOf(dataCallback))
                    .data(dataCallback)
                    .build();
            CallbackHandler callbackBiFunction = callbacks.get(callback.getButtonCallBackQuery());
            answer = callbackBiFunction.apply(callback, update);
        }
        return answer;
    }

}