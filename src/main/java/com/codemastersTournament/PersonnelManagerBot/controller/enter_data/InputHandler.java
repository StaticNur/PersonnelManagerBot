package com.codemastersTournament.PersonnelManagerBot.controller.enter_data;

import com.codemastersTournament.PersonnelManagerBot.models.Employee;
import com.codemastersTournament.PersonnelManagerBot.utils.StateForEmployeeData;
import com.codemastersTournament.PersonnelManagerBot.utils.enums.BotInputState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class InputHandler {
    private final Map<BotInputState, Input> entered;

    public InputHandler(@Autowired OpenCardEmployee openCardEmployee,
                        @Autowired AddNewEmployee addNewEmployee,
                        @Autowired SearchEmployeesByFIO searchEmployeesByFIO,
                        @Autowired SearchEmployeesByPosition employeesByPosition,
                        @Autowired SearchEmployeesByProject employeesByProject,
                        @Autowired SearchEmployeesByPositionList employeesByPositionlList,
                        @Autowired SearchEmployeesByProjectList employeesByProjectList,
                        @Autowired SearchEmployeesByDate employeesByDate,
                        @Autowired EditNameEmployee nameEmployee,
                        @Autowired EditLastNameEmployee lastNameEmployee,
                        @Autowired EditPatronymicEmployee patronymicEmployee,
                        @Autowired EditPositionEmployee positionEmployee,
                        @Autowired EditProjectEmployee projectEmployee,
                        @Autowired ChangeAvatarEmployee avatarEmployee) {
        this.entered = new HashMap<>();
        this.entered.put(BotInputState.OPEN_CARD, openCardEmployee);
        this.entered.put(BotInputState.WAITING_FOR_EMPLOYEE_DATA_FOR_ADD, addNewEmployee);
        this.entered.put(BotInputState.WAITING_FOR_EMPLOYEE_DATA_FOR_SEARCH_FIO, searchEmployeesByFIO);
        this.entered.put(BotInputState.WAITING_FOR_EMPLOYEE_DATA_FOR_SEARCH_BY_POSITION, employeesByPosition);
        this.entered.put(BotInputState.WAITING_FOR_EMPLOYEE_DATA_FOR_SEARCH_BY_PROJECT, employeesByProject);
        this.entered.put(BotInputState.WAITING_FOR_EMPLOYEE_DATA_FOR_SEARCH_BY_POSITION_LIST, employeesByPositionlList);
        this.entered.put(BotInputState.WAITING_FOR_EMPLOYEE_DATA_FOR_SEARCH_BY_PROJECT_LIST, employeesByProjectList);
        this.entered.put(BotInputState.WAITING_FOR_EMPLOYEE_DATA_FOR_SEARCH_BY_DATE, employeesByDate);
        this.entered.put(BotInputState.WAITING_FOR_EMPLOYEE_DATA_FOR_EDIT_NAME, nameEmployee);
        this.entered.put(BotInputState.WAITING_FOR_EMPLOYEE_DATA_FOR_EDIT_LAST_NAME, lastNameEmployee);
        this.entered.put(BotInputState.WAITING_FOR_EMPLOYEE_DATA_FOR_EDIT_PATRONYMIC, patronymicEmployee);
        this.entered.put(BotInputState.WAITING_FOR_EMPLOYEE_DATA_FOR_EDIT_POSITION, positionEmployee);
        this.entered.put(BotInputState.WAITING_FOR_EMPLOYEE_DATA_FOR_EDIT_PROJECT, projectEmployee);
        this.entered.put(BotInputState.WAITING_FOR_EMPLOYEE_DATA_FOR_EDIT_AVATAR, avatarEmployee);
    }

    public void handleInputs(Update update) {
        Map.Entry<BotInputState, Employee> stateForEmployeeData = StateForEmployeeData.stateAndCard.entrySet().iterator().next();
        BotInputState messageText = stateForEmployeeData.getKey();
        var commandHandler = entered.get(messageText);
        if (commandHandler != null) {
            commandHandler.handle(update);
        } else {
        }
    }
}