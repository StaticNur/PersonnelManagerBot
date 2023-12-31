package com.codemastersTournament.PersonnelManagerBot.controller.enter_data;

import com.codemastersTournament.PersonnelManagerBot.controller.callbacks.CallbackHandler;
import com.codemastersTournament.PersonnelManagerBot.controller.sender.SubmittingAdditionalMessage;
import com.codemastersTournament.PersonnelManagerBot.models.Employee;
import com.codemastersTournament.PersonnelManagerBot.service.impl.AnswerConsumerImpl;
import com.codemastersTournament.PersonnelManagerBot.service.impl.EmployeeService;
import com.codemastersTournament.PersonnelManagerBot.utils.StateForEmployeeData;
import com.codemastersTournament.PersonnelManagerBot.utils.enums.BotInputState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Map;

@Component
public class EditPatronymicEmployee implements Input {
    private final AnswerConsumerImpl answerConsumer;
    private final EmployeeService employeeService;
    private final SubmittingAdditionalMessage message;
    @Autowired
    public EditPatronymicEmployee(AnswerConsumerImpl answerConsumer, EmployeeService employeeService,
                            SubmittingAdditionalMessage message) {
        this.answerConsumer = answerConsumer;
        this.employeeService = employeeService;
        this.message = message;
    }
    @Override
    public void handle(Update update) {
        Long chatId = update.getMessage().getChatId();
        String messageText = update.getMessage().getText();

        Map.Entry<BotInputState, Employee> stateForEmployeeData = StateForEmployeeData.stateAndCard.entrySet().iterator().next();
        Long idEmployee = stateForEmployeeData.getValue().getId();
        employeeService.editEmployeePatronymic(idEmployee, messageText);
        message.sendMessage(new SendMessage(chatId.toString(),"Имя сотрудника успешно изменено."));
        sendCardAfterEdit(chatId,idEmployee);
    }
    private void sendCardAfterEdit(Long chatId, Long id) {
        Employee employeeAfterChanger = employeeService.searchEmployeeById(id);
        message.sendMessage(answerConsumer.sendPhoto(chatId, employeeAfterChanger));
        message.sendMessage(answerConsumer.generateEmployCard(chatId, employeeAfterChanger));

        StateForEmployeeData.stateAndCard.clear();
        StateForEmployeeData.stateAndCard.put(BotInputState.OPEN_CARD, employeeAfterChanger);
    }
}