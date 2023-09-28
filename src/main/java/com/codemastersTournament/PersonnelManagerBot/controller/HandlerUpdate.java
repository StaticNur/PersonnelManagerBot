package com.codemastersTournament.PersonnelManagerBot.controller;

import com.codemastersTournament.PersonnelManagerBot.controller.callbacks.CallbacksHandler;
import com.codemastersTournament.PersonnelManagerBot.controller.commands.CommandsHandler;
import com.codemastersTournament.PersonnelManagerBot.controller.enter_data.InputHandler;
import com.codemastersTournament.PersonnelManagerBot.controller.sender.SubmittingAdditionalMessage;
import com.codemastersTournament.PersonnelManagerBot.models.Employee;
import com.codemastersTournament.PersonnelManagerBot.service.impl.AnswerConsumerImpl;
import com.codemastersTournament.PersonnelManagerBot.utils.Consts;
import com.codemastersTournament.PersonnelManagerBot.utils.MessageUtils;
import com.codemastersTournament.PersonnelManagerBot.utils.StateForEmployeeData;
import com.codemastersTournament.PersonnelManagerBot.utils.enums.BotInputState;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Map;

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
            callbacksHandler.handleCallbacks(update);
        } else if (update.hasMessage()) {
            distributeMessagesByType(update);
        } else {
            log.error("Получен неподдерживаемый тип сообщения: " + update);
        }
    }

    private void distributeMessagesByType(Update update) {
        var message = update.getMessage();
        if (message.hasText()) {
            checkTextCommandProcess(update);
        } else if (message.hasPhoto()) {
            checkPhotoCommandProcess(update);
        } else if (message.hasDocument()) {
            setFileIsReceivedView(update);
        } else {
            setUnsupportedMessageTypeView(update);
        }
    }

    private void checkTextCommandProcess(Update update){
        System.out.println("Вводим");
        if(update.getMessage().getText().startsWith("/")){
            commandsHandler.handleCommands(update);
        }else {
            Map.Entry<BotInputState, Employee> stateForEmployeeData = StateForEmployeeData.stateAndCard.entrySet().iterator().next();
            System.out.println("Вводим данные "+stateForEmployeeData.getValue().getId());//заместо id мог быть любое объязательное поля
            if (stateForEmployeeData.getValue().getId() == null) {
                System.out.println("Вводим данные пользователья");
                inputHandler.handleInputs(update);
            }else {//внутри карточки вводим что-то, без команд и кнопок
                setUnsupportedMessageTypeView(update);
            }
        }
    }

    private void checkPhotoCommandProcess(Update update){
        if(!StateForEmployeeData.stateAndCard.isEmpty()){
            Map.Entry<BotInputState, Employee> stateForEmployeeData = StateForEmployeeData.stateAndCard.entrySet().iterator().next();
            if (stateForEmployeeData.getValue() == null) {
                System.out.println("Вводим данные пользователья");
                inputHandler.handleInputs(update);
            }else {//внутри карточки отправляем что-то, без команд и кнопок
                setUnsupportedMessageTypeView(update);
            }
        }else {
            generateAndSendMessageError(update,Consts.ERROR_PHOTO);
        }
    }
    private void setFileIsReceivedView(Update update) {
        /*System.out.println(update.getMessage().getDocument().getFileName());
        String nameFile = update.getMessage().getDocument().getFileName();
        String format = nameFile.split(".")[0];
        System.out.println(format);*/
        generateAndSendMessageError(update,Consts.ERROR_FILE);
    }

    private void setUnsupportedMessageTypeView(Update update) {
        generateAndSendMessageError(update,Consts.CANT_UNDERSTAND);
    }

    private void generateAndSendMessageError(Update update, String messageError){
        var error = messageUtils.generateSendMessageWithText(update, messageError);
        var buttonBack = AnswerConsumerImpl.generateButtonBack();
        error.setReplyMarkup(buttonBack);
        message.sendMessage(error);
    }

}
