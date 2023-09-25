package com.codemastersTournament.PersonnelManagerBot.controller.enter_data;

import com.codemastersTournament.PersonnelManagerBot.controller.callbacks.CallbackHandler;
import com.codemastersTournament.PersonnelManagerBot.controller.sender.SubmittingAdditionalMessage;
import com.codemastersTournament.PersonnelManagerBot.models.Employee;
import com.codemastersTournament.PersonnelManagerBot.service.impl.AnswerConsumerImpl;
import com.codemastersTournament.PersonnelManagerBot.service.impl.EmployeeService;
import jakarta.ws.rs.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Component
public class SearchEmployees implements Input {
    private final AnswerConsumerImpl answerConsumer;
    private final EmployeeService employeeService;
    private final SubmittingAdditionalMessage message;
    private final CallbackHandler callbackHandler;
    @Autowired
    public SearchEmployees(AnswerConsumerImpl answerConsumer, EmployeeService employeeService,
                           SubmittingAdditionalMessage message,
                           @Qualifier("openCardChooseCallback") CallbackHandler callbackHandler) {
        this.answerConsumer = answerConsumer;
        this.employeeService = employeeService;
        this.message = message;
        this.callbackHandler = callbackHandler;
    }
    @Override
    public void handle(Update update) {
        Long chatId = update.getMessage().getChatId();
        String messageText = update.getMessage().getText();
        try {
            List<Employee> listEmployView = employeeService.searchEmployeeByFirstOrLastName(messageText);
            message.sendMessage(answerConsumer.generateListEmployMessage(chatId, listEmployView));
        }catch (NotFoundException e){
            message.sendMessage(new SendMessage(chatId.toString(),"Такого сотрудника нет."));
            message.sendMessage(answerConsumer.generateNewMenuCommandMessage(chatId));
        }
    }
}
