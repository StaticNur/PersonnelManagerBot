package com.codemastersTournament.PersonnelManagerBot.controller.enter_data;

import com.codemastersTournament.PersonnelManagerBot.models.Employee;
import com.codemastersTournament.PersonnelManagerBot.utils.Consts;
import com.codemastersTournament.PersonnelManagerBot.utils.StateForEmployeeData;
import com.codemastersTournament.PersonnelManagerBot.utils.enums.BotInputState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Map;

@Component
@Slf4j
public class InputHandler {
    private final Map<BotInputState, Input> commands;
    public InputHandler(@Autowired OpenCardEmployee openCardEmployee,
                        @Autowired AddNewEmployee addNewEmployee,
                        @Autowired SearchEmployees searchEmployee,
                        @Autowired EditNameEmployee nameEmployee,
                        @Autowired EditLastNameEmployee lastNameEmployee,
                        @Autowired EditPatronymicEmployee patronymicEmployee,
                        @Autowired EditPositionEmployee positionEmployee,
                        @Autowired EditProjectEmployee projectEmployee,
                        @Autowired ChangeAvatarEmployee avatarEmployee) {
        this.commands = Map.of(
                BotInputState.OPEN_CARD, openCardEmployee,
                BotInputState.WAITING_FOR_EMPLOYEE_DATA_FOR_ADD, addNewEmployee,
                BotInputState.WAITING_FOR_EMPLOYEE_DATA_FOR_SEARCH, searchEmployee,
                BotInputState.WAITING_FOR_EMPLOYEE_DATA_FOR_EDIT_NAME, nameEmployee,
                BotInputState.WAITING_FOR_EMPLOYEE_DATA_FOR_EDIT_LAST_NAME, lastNameEmployee,
                BotInputState.WAITING_FOR_EMPLOYEE_DATA_FOR_EDIT_PATRONYMIC, patronymicEmployee,
                BotInputState.WAITING_FOR_EMPLOYEE_DATA_FOR_EDIT_POSITION, positionEmployee,
                BotInputState.WAITING_FOR_EMPLOYEE_DATA_FOR_EDIT_PROJECT, projectEmployee,
                BotInputState.WAITING_FOR_EMPLOYEE_DATA_FOR_EDIT_AVATAR, avatarEmployee
        );
    }
    public SendMessage handleInputs(Update update) {
        Map.Entry<BotInputState, Employee> stateForEmployeeData = StateForEmployeeData.stateAndCard.entrySet().iterator().next();
        BotInputState messageText = stateForEmployeeData.getKey();
        Long chatId = update.getMessage().getChatId();

        var commandHandler = commands.get(messageText);
        if (commandHandler != null) {
            commandHandler.handle(update);
            return new SendMessage(chatId.toString(),"Операция прошла успешно");
        } else {
            return new SendMessage(chatId.toString(), Consts.CANT_UNDERSTAND);
        }
    }
}