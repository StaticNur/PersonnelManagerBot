package com.codemastersTournament.PersonnelManagerBot.controller;

import com.codemastersTournament.PersonnelManagerBot.controller.callbacks.CallbacksHandler;
import com.codemastersTournament.PersonnelManagerBot.controller.commands.CommandsHandler;
import com.codemastersTournament.PersonnelManagerBot.controller.enter_data.InputHandler;
import com.codemastersTournament.PersonnelManagerBot.controller.sender.SubmittingAdditionalMessage;
import com.codemastersTournament.PersonnelManagerBot.utils.MessageUtils;
import com.codemastersTournament.PersonnelManagerBot.utils.StateForEmployeeData;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Log4j
@Component
public class HandlerUpdate {
    private final CommandsHandler commandsHandler;
    private final CallbacksHandler callbacksHandler;
    private final InputHandler inputHandler;
    private final SubmittingAdditionalMessage message;
    private final MessageUtils messageUtils;
    @Autowired
    public HandlerUpdate(CommandsHandler commandsHandler, CallbacksHandler callbacksHandler, InputHandler inputHandler, SubmittingAdditionalMessage message, MessageUtils messageUtils) {
        this.commandsHandler = commandsHandler;
        this.callbacksHandler = callbacksHandler;
        this.inputHandler = inputHandler;
        this.message = message;
        this.messageUtils = messageUtils;
    }
    public void processUpdate(Update update) {
        if (update == null) {
            log.error("Полученное сообщение пустое");
            return;
        }
        if (update.hasCallbackQuery()) {
            System.out.println("Нажимаем на кнопки");
            setAnswerMessage(callbacksHandler.handleCallbacks(update));
        } else if (update.hasMessage()) {
            System.out.println("Отправка сообщения ");
            distributeMessagesByType(update);
        } else {
            log.error("Получен неподдерживаемый тип сообщения: " + update);
        }
    }

    private void distributeMessagesByType(Update update) {
        var message = update.getMessage();
        if (message.hasText()) {
            if (message.hasText() && (!StateForEmployeeData.stateAndCard.isEmpty())) {
                System.out.println("Вводим данные пользователья");
                setAnswerMessage(inputHandler.handleInputs(update));
            } else {
                System.out.println("Вводим какую-то команду: /command");
                setAnswerMessage(commandsHandler.handleCommands(update));
            }
        } else if (message.hasPhoto() && (!StateForEmployeeData.stateAndCard.isEmpty())) {
            System.out.println("отправляем photo");
            setAnswerMessage(inputHandler.handleInputs(update));
        } else if (message.hasDocument()) {
            System.out.println("отправляем Document");
            setFileIsReceivedView(update);
        } else {
            setUnsupportedMessageTypeView(update);
        }
    }
    public void setAnswerMessage(SendMessage sendMessage) {
        message.sendMessage(sendMessage);
    }
    private void setUnsupportedMessageTypeView(Update update) {
        var sendMessage = messageUtils.generateSendMessageWithText(update,
                "Неподдерживаемый тип сообщения!");
        setAnswerMessage(sendMessage);
    }
    private void setFileIsReceivedView(Update update) {
        var sendMessage = messageUtils.generateSendMessageWithText(update,
                "Сообщения в виде файлов не обрабатываются!");
        setAnswerMessage(sendMessage);
    }
}
