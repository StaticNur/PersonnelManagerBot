package com.codemastersTournament.PersonnelManagerBot.controller.callbacks;

import com.codemastersTournament.PersonnelManagerBot.controller.sender.SubmittingAdditionalMessage;
import com.codemastersTournament.PersonnelManagerBot.models.Employee;
import com.codemastersTournament.PersonnelManagerBot.service.impl.AnswerConsumerImpl;
import com.codemastersTournament.PersonnelManagerBot.service.impl.EmployeeService;
import com.codemastersTournament.PersonnelManagerBot.utils.StateForEmployeeData;
import com.codemastersTournament.PersonnelManagerBot.utils.enums.BotInputState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Map;

@Component
public class DeleteChooseCallback implements CallbackHandler{
    private final EmployeeService employeeService;
    private final AnswerConsumerImpl answerConsumer;
    private final SubmittingAdditionalMessage message;
    @Autowired
    public DeleteChooseCallback(EmployeeService employeeService,
                                AnswerConsumerImpl answerConsumer, SubmittingAdditionalMessage message) {
        this.employeeService = employeeService;
        this.answerConsumer = answerConsumer;
        this.message = message;
    }
    @Override
    public SendMessage apply(Callback callback, Update update) {
        Long chatId = update.getCallbackQuery().getMessage().getChatId();

        Map.Entry<BotInputState, Employee> stateForEmployeeData = StateForEmployeeData.stateAndCard.entrySet().iterator().next();
        Employee employee = stateForEmployeeData.getValue();
        employeeService.deleteEmployee(employee);

        message.sendMessage(new SendMessage(chatId.toString(),"Сотрудник успешно удален."));
        return answerConsumer.generateNewMenuCommandMessage(chatId);
    }
}
