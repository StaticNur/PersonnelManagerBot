package com.codemastersTournament.PersonnelManagerBot.controller.commands;

import com.codemastersTournament.PersonnelManagerBot.controller.sender.SubmittingAdditionalMessage;
import com.codemastersTournament.PersonnelManagerBot.service.impl.AnswerConsumerImpl;
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
    private final SubmittingAdditionalMessage message;
    public CommandsHandler(@Autowired StartCommand startCommand,
                           /*@Autowired StopCommand stopCommand,
                           @Autowired InfoCommand infoCommand,
                           @Autowired MenuCommand menuCommand,*/
                            @Autowired AdminCommand adminCommand,
                            @Autowired UserCommand userCommand,
                            @Autowired InfoCommand infoCommand,
                            @Autowired StopCommand stopCommand,
                           SubmittingAdditionalMessage message) {
        this.message = message;
        this.commands = Map.of("/admin", adminCommand,
                "/user", userCommand,
                "/info", infoCommand,
                "/stop", stopCommand,
                "/start", startCommand
                /*
                "/stop", stopCommand,
                "/info", infoCommand,
                "/menu", menuCommand*/
        );
    }
    public void handleCommands(Update update) {
        String messageText = update.getMessage().getText();
        long chatId = update.getMessage().getChatId();

        var commandHandler = commands.get(messageText);
        if (commandHandler != null) {
            commandHandler.apply(update);
        } else {
            var error = new SendMessage(String.valueOf(chatId), Consts.CANT_UNDERSTAND);
            var buttonBack = AnswerConsumerImpl.generateButtonBack();
            error.setReplyMarkup(buttonBack);
            message.sendMessage(error);
        }
    }
}