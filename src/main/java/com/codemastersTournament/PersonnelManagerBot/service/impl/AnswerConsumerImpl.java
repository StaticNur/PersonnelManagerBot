package com.codemastersTournament.PersonnelManagerBot.service.impl;

import com.codemastersTournament.PersonnelManagerBot.models.Employee;
import com.codemastersTournament.PersonnelManagerBot.repository.EmployeeRepository;
import com.codemastersTournament.PersonnelManagerBot.service.AnswerConsumer;
import com.codemastersTournament.PersonnelManagerBot.utils.Consts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
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

    public SendMessage generateNewMenuCommandMessage(Long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("Количество сотрудников: " + employeeRepository.findAll().size());

        sendMessage.setReplyMarkup(generateButtonMenu());
        return sendMessage;
    }

    public EditMessageText generateEditMenuCommandMessage(Long chatId, Integer messageId) {
        EditMessageText message = new EditMessageText();
        message.setMessageId(messageId);
        message.setChatId(String.valueOf(chatId));
        message.setText("Количество сотрудников: " + employeeRepository.findAll().size());

        message.setReplyMarkup(generateButtonMenu());
        return message;
    }

    private InlineKeyboardMarkup generateButtonMenu() {
        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        rowsInLine.add(generateButtonVertical("Добавить сотрудника ✅", "ADD_EMPLOYEE"));
        rowsInLine.add(generateButtonVertical("Открыть карточку сотрудника ✍\uFE0F", "OPEN_CARD_EMPLOYEE"));
        rowsInLine.add(generateButtonVertical("Поиск сотрудника \uD83D\uDD0D", "SEARCH_EMPLOYEE"));
        markupInLine.setKeyboard(rowsInLine);
        return markupInLine;
    }

    public SendMessage generateNewAddCommandMessage(Long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(Consts.TEXT_MESSAGE_ADD);
        sendMessage.setChatId(chatId);

        sendMessage.setReplyMarkup(generateButtonBack());
        return sendMessage;
    }

    public EditMessageText generateEditAddCommandMessage(Long chatId, Integer messageId) {
        EditMessageText message = new EditMessageText();
        message.setMessageId(messageId);
        message.setChatId(String.valueOf(chatId));
        message.setText(Consts.TEXT_MESSAGE_ADD);

        message.setReplyMarkup(generateButtonBack());
        return message;
    }

    public SendMessage generateNewInfoMessage(Long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(Consts.TEXT_MESSAGE_INFO);

        message.setReplyMarkup(generateButtonBack());
        return message;
    }
    public SendMessage generateListEmployMessage(Long chatId, List<Employee> employeeList) {
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
    public SendPhoto sendNewPhoto(Long chatId, Employee employee){
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
        sendMessage.setReplyMarkup(generateButtonCardEmployee());
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
    public SendMessage generateNewSearchCommandMessage(Long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(Consts.TEXT_MESSAGE_SEARCH);
        message.setReplyMarkup(generateButtonBack());
        return message;
    }
    public EditMessageText generateEditSearchCommandMessage(Long chatId, Integer messageId) {
        EditMessageText message = new EditMessageText();
        message.setMessageId(messageId);
        message.setChatId(String.valueOf(chatId));
        message.setText(Consts.TEXT_MESSAGE_SEARCH);

        message.setReplyMarkup(generateButtonBack());
        return message;
    }

    private InlineKeyboardMarkup generateButtonBack() {
        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        rowsInLine.add(generateButtonVertical("«Назад\uFE0F", "BACK"));
        markupInLine.setKeyboard(rowsInLine);
        return markupInLine;
    }

    private List<InlineKeyboardButton> generateButtonVertical(String textButton, String callbackData) {
        List<InlineKeyboardButton> rowInLine = new ArrayList<>();
        var addButton = new InlineKeyboardButton();
        addButton.setText(textButton);
        addButton.setCallbackData(callbackData);
        rowInLine.add(addButton);
        return rowInLine;
    }
}
