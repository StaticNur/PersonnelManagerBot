package com.codemastersTournament.PersonnelManagerBot.controller.enter_data;

import com.codemastersTournament.PersonnelManagerBot.controller.sender.SubmittingAdditionalMessage;
import com.codemastersTournament.PersonnelManagerBot.models.Employee;
import com.codemastersTournament.PersonnelManagerBot.service.impl.AnswerConsumerImpl;
import com.codemastersTournament.PersonnelManagerBot.service.impl.EmployeeService;
import com.codemastersTournament.PersonnelManagerBot.utils.StateForEmployeeData;
import com.codemastersTournament.PersonnelManagerBot.utils.enums.BotInputState;
import jakarta.ws.rs.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class OpenCardEmployee implements Input {
    private final AnswerConsumerImpl answerConsumer;
    private final EmployeeService employeeService;
    private final SubmittingAdditionalMessage message;
    @Autowired
    public OpenCardEmployee(AnswerConsumerImpl answerConsumer, EmployeeService employeeService,
                          SubmittingAdditionalMessage message) {
        this.answerConsumer = answerConsumer;
        this.employeeService = employeeService;
        this.message = message;
    }
    @Override
    public void handle(Update update) {
        Long chatId = update.getMessage().getChatId();
        String messageText = update.getMessage().getText();
        try {
            Employee employee = employeeService.searchEmployeeByFirstOrLastName(messageText).get(0);
            sendCardAfterEdit(chatId,employee.getId());
        }catch (NotFoundException e){
            message.sendMessage(new SendMessage(chatId.toString(),"Такого сотрудника нет."));
            message.sendMessage(answerConsumer.generateNewMenuCommandMessage(chatId));
            StateForEmployeeData.stateAndCard.clear();
        }
    }
    private void sendCardAfterEdit(Long chatId, Long id) {
        Employee employeeAfterChanger = employeeService.searchEmployeeById(id);
        message.sendMessage(answerConsumer.sendNewPhoto(chatId, employeeAfterChanger));
        message.sendMessage(answerConsumer.generateEmployCard(chatId, employeeAfterChanger));

        StateForEmployeeData.stateAndCard.clear();
        StateForEmployeeData.stateAndCard.put(BotInputState.OPEN_CARD, employeeAfterChanger);
    }
}
