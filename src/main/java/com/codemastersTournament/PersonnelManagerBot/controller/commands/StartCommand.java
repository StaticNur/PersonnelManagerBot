package com.codemastersTournament.PersonnelManagerBot.controller.commands;

import com.codemastersTournament.PersonnelManagerBot.controller.sender.SubmittingAdditionalMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class StartCommand implements Command{
    private final SubmittingAdditionalMessage message;
    private final Command command;
    @Autowired
    public StartCommand(SubmittingAdditionalMessage message, @Qualifier("menuCommand") Command command) {
        this.message = message;
        this.command = command;
    }

    @Override
    public SendMessage apply(Update update) {
        long chatId = update.getMessage().getChatId();
        var chat = update.getMessage().getChat();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        //TODO добавить роли
        sendMessage.setText("Привет, " + chat.getFirstName() +
                " " + chat.getLastName() + "! Я помогу тебе управленять информацией о сотрудниках.");
        message.sendMessage(sendMessage);

        return command.apply(update);// Возвращаем null, так как сообщение уже отправлено асинхронно
    }
}
