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
public class AddNewEmployee implements Input {
    private final AnswerConsumerImpl answerConsumer;
    private final EmployeeService employeeService;
    private final SubmittingAdditionalMessage message;
    private final CallbackHandler callbackHandler;
    @Autowired
    public AddNewEmployee(AnswerConsumerImpl answerConsumer, EmployeeService employeeService,
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
            Employee employee = employeeService.addEmployee(messageText);
            message.sendMessage(new SendMessage(chatId.toString(),"Сотрудник успешно добавлен."));

            Map.Entry<BotInputState, Employee> stateForEmployeeData = StateForEmployeeData.stateAndCard.entrySet().iterator().next();
            StateForEmployeeData.stateAndCard.put(stateForEmployeeData.getKey(),employee);
            message.sendMessage(answerConsumer.generateEmployCard(chatId,employee));
        }catch (Exception e){
            message.sendMessage(new SendMessage(chatId.toString(),"Введено не корректные данные.\nВведите как в примере!"));
            message.sendMessage(answerConsumer.generateNewAddCommandMessage(chatId));
        }
    }
}
