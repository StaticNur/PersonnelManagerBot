package com.codemastersTournament.PersonnelManagerBot.service.impl;

import com.codemastersTournament.PersonnelManagerBot.models.Employee;
import com.codemastersTournament.PersonnelManagerBot.repository.EmployeeRepository;
import com.codemastersTournament.PersonnelManagerBot.service.AnswerConsumer;
import com.codemastersTournament.PersonnelManagerBot.utils.Consts;
import com.codemastersTournament.PersonnelManagerBot.utils.StateForEmployeeData;
import com.codemastersTournament.PersonnelManagerBot.utils.enums.Role;
import jakarta.ws.rs.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import java.io.ByteArrayInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class AnswerConsumerImpl implements AnswerConsumer {
    private final EmployeeRepository employeeRepository;

    @Autowired
    public AnswerConsumerImpl(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    public SendMessage consume(SendMessage sendMessage) {
        return sendMessage;
    }

    public SendMessage generateMenu(Long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("Количество сотрудников: " + employeeRepository.findAll().size());

        if(StateForEmployeeData.role == Role.ADMIN){
            sendMessage.setReplyMarkup(generateAdminButtonMenu());
        }else if(StateForEmployeeData.role == Role.USER){
            sendMessage.setReplyMarkup(generateUserButtonMenu());
        }
        return sendMessage;
    }
    private InlineKeyboardMarkup generateAdminButtonMenu() {
        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        rowsInLine.add(generateButtonVertical("Добавить сотрудника ✅", "ADD_EMPLOYEE"));
        rowsInLine.add(generateButtonVertical("Открыть карточку сотрудника ✍\uFE0F", "OPEN_CARD_EMPLOYEE"));
        rowsInLine.add(generateButtonVertical("Поиск сотрудника по ФИО \uD83D\uDD0D", "SEARCH_EMPLOYEE"));
        rowsInLine.add(generateButtonVertical("Все сотрудники \uD83D\uDC40", "VIEW_ALL"));
        markupInLine.setKeyboard(rowsInLine);
        return markupInLine;
    }
    private InlineKeyboardMarkup generateUserButtonMenu() {
        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        rowsInLine.add(generateButtonVertical("Искать по должности \uD83D\uDD0D", "SEARCH_EMPLOYEE_BY_POSITION"));
        rowsInLine.add(generateButtonVertical("Искать по дате прибытия \uD83D\uDD0D", "SEARCH_EMPLOYEE_BY_DATE"));
        rowsInLine.add(generateButtonVertical("Искать по проекту \uD83D\uDD0D", "SEARCH_EMPLOYEE_BY_PROJECT"));
        rowsInLine.add(generateButtonVertical("Найти список по проекту \uD83D\uDD0D", "SEARCH_EMPLOYEE_BY_PROJECT_LIST"));
        rowsInLine.add(generateButtonVertical("Найти список по должности \uD83D\uDD0D", "SEARCH_EMPLOYEE_BY_POSITION_LIST"));
        markupInLine.setKeyboard(rowsInLine);
        return markupInLine;
    }

    public SendMessage generateAddCommandMessage(Long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(Consts.TEXT_MESSAGE_ADD);
        sendMessage.setChatId(chatId);

        sendMessage.setReplyMarkup(generateButtonBack());
        return sendMessage;
    }

    public SendMessage generateInfoMessage(Long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(Consts.TEXT_MESSAGE_INFO);

        message.setReplyMarkup(generateButtonBack());
        return message;
    }
    public SendMessage generateListEmployMessage(Long chatId, List<Employee> employeeList) {
        if(employeeList.isEmpty()){
            throw new NotFoundException();
        }
        SendMessage sendMessage = new SendMessage();
        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append("Результаты поиска: \n");
        for (int i = 0; i < employeeList.size(); i++) {
            Employee employee = employeeList.get(i);
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
            Date date = new Date(employee.getArrivalDate().getTime());
            String formattedDate = sdf.format(date);

            messageBuilder.append(i + 1).append(". ")
                    .append(" имя: ").append(employee.getName()).append("\n")
                    .append("     отчество: ").append(employee.getPatronymic()).append("\n")
                    .append("     фамилия: ").append(employee.getLastName()).append("\n")
                    .append("     должность: ").append(employee.getPosition()).append("\n")
                    .append("     проект: ").append(employee.getProject()).append("\n")
                    .append("     дата прихода: ").append(formattedDate).append("\n\n");
        }
        String message = messageBuilder.toString();
        sendMessage.setText(message);
        sendMessage.setChatId(chatId);

        sendMessage.setReplyMarkup(generateButtonBack());
        return sendMessage;
    }
    public SendPhoto sendPhoto(Long chatId, Employee employee){
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(String.valueOf(chatId));

        byte[] avatarBytes = employee.getAvatar();
        InputFile inputFile = new InputFile();
        inputFile.setMedia(new ByteArrayInputStream(avatarBytes), "avatar.jpg");
        sendPhoto.setPhoto(inputFile);

        return sendPhoto;
    }
    public SendMessage generateEmployCard(Long chatId, Employee employee) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        Date date = new Date(employee.getArrivalDate().getTime());
        String formattedDate = sdf.format(date);

        sendMessage.setText("имя: " +employee.getName()+
                "\nотчество: " + employee.getPatronymic()+
                "\nфамилия: " +employee.getLastName()+
                "\nдолжность: " +employee.getPosition()+
                "\nпроект: " +employee.getProject()+
                "\nдата прихода: "+formattedDate);
        if(StateForEmployeeData.role == Role.ADMIN){
            sendMessage.setReplyMarkup(generateButtonCardEmployee());
        }
        return sendMessage;
    }
    private InlineKeyboardMarkup generateButtonCardEmployee() {
        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        rowsInLine.add(generateButtonVertical("изменить имя", "CHANGE_NAME"));
        rowsInLine.add(generateButtonVertical("изменить отчество", "CHANGE_PATRONYMIC"));
        rowsInLine.add(generateButtonVertical("изменить фамилию", "CHANGE_LAST_NAME"));
        rowsInLine.add(generateButtonVertical("изменить должность", "CHANGE_POSITION"));
        rowsInLine.add(generateButtonVertical("изменить проект", "CHANGE_PROJECT"));
        rowsInLine.add(generateButtonVertical("изменить аватарку", "CHANGE_AVATAR"));
        rowsInLine.add(generateButtonVertical("Удалить сотрудника ❌", "DELETE_EMPLOYEE"));
        rowsInLine.add(generateButtonVertical("«Назад\uFE0F", "BACK"));
        markupInLine.setKeyboard(rowsInLine);
        return markupInLine;
    }
    public SendMessage generateSearchByFIOMessage(Long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(Consts.TEXT_MESSAGE_SEARCH_BY_FIO);
        message.setReplyMarkup(generateButtonBack());
        return message;
    }
    public SendMessage generateSearchByProjectMessage(Long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(Consts.TEXT_MESSAGE_SEARCH_BY_PROJECT);
        message.setReplyMarkup(generateButtonBack());
        return message;
    }
    public SendMessage generateSearchByPositionMessage(Long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(Consts.TEXT_MESSAGE_SEARCH_BY_POSITION);
        message.setReplyMarkup(generateButtonBack());
        return message;
    }
    public SendMessage generateSearchByDateMessage(Long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(Consts.TEXT_MESSAGE_SEARCH_BY_DATE);
        message.setReplyMarkup(generateButtonBack());
        return message;
    }
    public static InlineKeyboardMarkup generateButtonBack() {
        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        rowsInLine.add(generateButtonVertical("«Назад\uFE0F", "BACK"));
        markupInLine.setKeyboard(rowsInLine);
        return markupInLine;
    }

    public static List<InlineKeyboardButton> generateButtonVertical(String textButton, String callbackData) {
        List<InlineKeyboardButton> rowInLine = new ArrayList<>();
        var addButton = new InlineKeyboardButton();
        addButton.setText(textButton);
        addButton.setCallbackData(callbackData);
        rowInLine.add(addButton);
        return rowInLine;
    }
}
