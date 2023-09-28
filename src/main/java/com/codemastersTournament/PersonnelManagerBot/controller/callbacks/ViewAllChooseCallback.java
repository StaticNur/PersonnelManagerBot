package com.codemastersTournament.PersonnelManagerBot.controller.callbacks;

import com.codemastersTournament.PersonnelManagerBot.controller.sender.SubmittingAdditionalMessage;
import com.codemastersTournament.PersonnelManagerBot.models.Employee;
import com.codemastersTournament.PersonnelManagerBot.service.impl.AnswerConsumerImpl;
import com.codemastersTournament.PersonnelManagerBot.service.impl.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Component
public class ViewAllChooseCallback implements CallbackHandler{
    private final AnswerConsumerImpl answerConsumer;
    private final EmployeeService employeeService;
    private final SubmittingAdditionalMessage message;
    @Autowired
    public ViewAllChooseCallback(AnswerConsumerImpl answerConsumer, EmployeeService employeeService, SubmittingAdditionalMessage message) {
        this.answerConsumer = answerConsumer;
        this.employeeService = employeeService;
        this.message = message;
    }
    @Override
    public void apply(Callback callback, Update update) {
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        List<Employee> listEmployView = employeeService.viewAll();
        message.sendMessage(answerConsumer.generateListEmployMessage(chatId, listEmployView));
    }
}
