package com.codemastersTournament.PersonnelManagerBot.controller;

import com.codemastersTournament.PersonnelManagerBot.config.BotConfig;
import com.codemastersTournament.PersonnelManagerBot.controller.callbacks.CallbacksHandler;
import com.codemastersTournament.PersonnelManagerBot.controller.enter_data.ChangeAvatarEmployee;
import com.codemastersTournament.PersonnelManagerBot.controller.sender.MessageSender;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

@Log4j
@Component
public class TelegramBot extends TelegramLongPollingBot {
    private final BotConfig config;
    private final HandlerUpdate handlerUpdate;
    public final CallbacksHandler callbacksHandler;
    public final MessageSender messageSender;
    public final ChangeAvatarEmployee avatarEmployee;
    @Autowired
    public TelegramBot(HandlerUpdate handlerUpdate, BotConfig config, CallbacksHandler callbacksHandler, MessageSender messageSender, ChangeAvatarEmployee avatarEmployee) {
        this.handlerUpdate = handlerUpdate;
        this.config = config;
        this.callbacksHandler = callbacksHandler;
        this.messageSender = messageSender;
        this.avatarEmployee = avatarEmployee;
        this.messageSender.registerBot(this);
        this.avatarEmployee.registerBot(this);
        setListOfCommand();
    }

    public void setListOfCommand(){
        List<BotCommand> listOfCommand = new ArrayList<>();
        listOfCommand.add(new BotCommand("/start","Запуск бота"));
        listOfCommand.add(new BotCommand("/stop","Стоп"));
        listOfCommand.add(new BotCommand("/menu","Основные команды"));
        listOfCommand.add(new BotCommand("/info","Информация о боте"));
        SetMyCommands setMyCommands = new SetMyCommands();
        setMyCommands.setCommands(listOfCommand);
        try {
            execute(setMyCommands);
        } catch (TelegramApiException e) {
            log.error("Ошибка настройки списка команд бота: " + e.getMessage());
        }
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        handlerUpdate.processUpdate(update);
    }
}
