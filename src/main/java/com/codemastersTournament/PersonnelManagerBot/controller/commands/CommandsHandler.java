package com.codemastersTournament.PersonnelManagerBot.controller.commands;

import com.codemastersTournament.PersonnelManagerBot.utils.Consts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Map;

@Component
@Slf4j
public class CommandsHandler {
    private final Map<String, Command> commands;
    public CommandsHandler(@Autowired StartCommand startCommand,
                           @Autowired StopCommand stopCommand,
                           @Autowired InfoCommand infoCommand,
                           @Autowired MenuCommand menuCommand) {
        this.commands = Map.of(
                "/start", startCommand,
                "/stop", stopCommand,
                "/info", infoCommand,
                "/menu", menuCommand
        );
    }
    public SendMessage handleCommands(Update update) {
        String messageText = update.getMessage().getText();
        long chatId = update.getMessage().getChatId();

        var commandHandler = commands.get(messageText);
        if (commandHandler != null) {
            return commandHandler.apply(update);
        } else {
            return new SendMessage(String.valueOf(chatId), Consts.CANT_UNDERSTAND);
        }
    }
}