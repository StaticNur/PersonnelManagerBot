package com.codemastersTournament.PersonnelManagerBot.controller;

import com.codemastersTournament.PersonnelManagerBot.models.Employee;
import com.codemastersTournament.PersonnelManagerBot.service.impl.EmployeeService;
import com.codemastersTournament.PersonnelManagerBot.service.impl.ManagerService;
import com.codemastersTournament.PersonnelManagerBot.service.impl.AnswerConsumerImpl;
import com.codemastersTournament.PersonnelManagerBot.utils.MessageUtils;
import com.codemastersTournament.PersonnelManagerBot.utils.enums.BotState;
import jakarta.ws.rs.NotFoundException;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static com.codemastersTournament.PersonnelManagerBot.utils.enums.BotState.*;

@Log4j
@Component
public class UpdateProcessor {
    private TelegramBot telegramBot;
    private final MessageUtils messageUtils;
    private final AnswerConsumerImpl answerConsumer;
    private final ManagerService managerService;
    private final EmployeeService employeeService;
    private final Map<Long, BotState> managerStates = new HashMap<>();

    @Autowired
    public UpdateProcessor(MessageUtils messageUtils, AnswerConsumerImpl answerConsumer,
                           ManagerService managerService, EmployeeService employeeService) {
        this.messageUtils = messageUtils;
        this.answerConsumer = answerConsumer;
        this.managerService = managerService;
        this.employeeService = employeeService;
    }

    public void registerBot(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    public void processUpdate(Update update) {
        if (update == null) {
            log.error("Полученное сообщение пустое");
            return;
        }
        if (update.hasCallbackQuery()) {
            System.out.println("Нажимаем на кнопки");
            processCallbackQuery(update);
        }else if (update.hasMessage()) {
            System.out.println("Вводим ");
            distributeMessagesByType(update);
        } else {
            log.error("Получен неподдерживаемый тип сообщения: " + update);
        }
    }

    private void distributeMessagesByType(Update update) {
        var message = update.getMessage();
        if( (message.hasText() && (!update.getMessage().getText().startsWith("/"))) && (!managerStates.isEmpty()) ){
            System.out.println("Вводим данные пользователья");
            processEnteredEmployeeData(update);
        }else if (message.hasText()) {
            System.out.println("Вводим какой-то текст");
            processTextMessage(update);
        }else if (message.hasPhoto()) {
            System.out.println("отправляем photo");
            processPhotoMessage(update);
        } else if (message.hasDocument()) {
            System.out.println("отправляем Document");
            processDocMessage(update);
        } else {
            setUnsupportedMessageTypeView(update);
        }
    }

    public void setEditMessageView(EditMessageText sendMessage) {
        telegramBot.sendAnswerMessage(sendMessage);
    }

    public void setNewMessageView(SendMessage sendMessage) {
        telegramBot.sendAnswerMessage(sendMessage);
    }
    public void setNewPhotoView(SendPhoto sendPhoto) {
        telegramBot.sendAnswerMessage(sendPhoto);
    }
    private void setUnsupportedMessageTypeView(Update update) {
        var sendMessage = messageUtils.generateSendMessageWithText(update,
                "Неподдерживаемый тип сообщения!");
        setNewMessageView(sendMessage);
    }

    private void setFileIsReceivedView(Update update) {
        var sendMessage = messageUtils.generateSendMessageWithText(update,
                "Сообщения в виде файлов не обрабатываются!");
        processPhotoMessage(update);
        setNewMessageView(sendMessage);
    }

    private void processPhotoMessage(Update update) {
        Long chatId = update.getMessage().getChatId();

        Map.Entry<Long, BotState> valueManagerStates;
        Long idEmployee = null;
        if(!managerStates.isEmpty()){
            valueManagerStates = managerStates.entrySet().iterator().next(); // Получаем первую запись из HashMap
            idEmployee = valueManagerStates.getKey();
        }
        BotState currentState = managerStates.getOrDefault(idEmployee, BotState.START);// Проверяем текущее состояние пользователя
        if(currentState.equals(WAITING_FOR_EMPLOYEE_DATA_FOR_EDIT_AVATAR)) {
            byte[] photoByte;
            try {
                photoByte = telegramBot.getPhotoFile(update);
                employeeService.editEmployeeAvatar(idEmployee, photoByte);
                sendSuccessMessage(chatId,"Аватарка, сотрудника успешно изменено!");
            } catch (Exception e) {
                log.error(e);
            }
            Employee employeeAfterChanger = employeeService.searchEmployeeById(idEmployee);
            setNewPhotoView(answerConsumer.sendNewPhoto(chatId, employeeAfterChanger));
            setNewMessageView(answerConsumer.generateEmployCard(chatId, employeeAfterChanger));
        }else {
            setNewMessageView(new SendMessage(chatId.toString(),"Фотографии обрабатываются только в разделе карточка" +
                    " для изменения Аватарки сотрудника❗\uFE0F❗\uFE0F❗\uFE0F"));
            setNewMessageView(answerConsumer.generateNewMenuCommandMessage(chatId));
            managerStates.clear();
        }
    }

    private void processDocMessage(Update update) {
        setFileIsReceivedView(update);
    }

    private void processTextMessage(Update update) {
        var chat = update.getMessage().getChat();
        var messageId = update.getMessage().getMessageId();
        Long chatId = update.getMessage().getChatId();

        Map.Entry<Long, BotState> valueManagerStates;
        Long idEmployee = null;
        if(!managerStates.isEmpty()){
            valueManagerStates = managerStates.entrySet().iterator().next(); // Получаем первую запись из HashMap
            idEmployee = valueManagerStates.getKey();
        }
        switch (update.getMessage().getText()) {
            case "/start":
                if (managerService.checkDatabase(chatId)) {
                    managerService.addNewManager(update.getMessage());
                    try {
                        telegramBot.execute(new SendMessage(chatId.toString(), "Привет, " + chat.getFirstName() +
                                " " + chat.getLastName() + "! Я помогу тебе управленять информацией о сотрудниках."));
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                }
                setNewMessageView(answerConsumer.generateNewMenuCommandMessage(chatId));
                managerStates.clear();
                break;
            case "/menu":
                setNewMessageView(answerConsumer.generateNewMenuCommandMessage(chatId));
                managerStates.clear();
                break;
            case "/add":
                setNewMessageView(answerConsumer.generateNewAddCommandMessage(chatId));
                managerStates.clear();
                managerStates.put(chatId, WAITING_FOR_EMPLOYEE_DATA_FOR_ADD);
                break;
            case "/delete":
                if(managerStates.isEmpty()){
                    setNewMessageView(answerConsumer.generateNewSearchCommandMessage(chatId));
                    managerStates.put(chatId, WAITING_FOR_EMPLOYEE_DATA_FOR_DELETE);
                }else {
                    //удаление внутри карточки
                    Employee employee = employeeService.searchEmployeeById(idEmployee);
                    employeeService.deleteEmployee(employee);
                    setEditMessageView(new EditMessageText("Сотрудник успешно удален."));
                    setNewMessageView(answerConsumer.generateNewMenuCommandMessage(chatId));
                    managerStates.clear();
                }
                break;
            case "/edit":
                if(managerStates.isEmpty()){
                    setNewMessageView(answerConsumer.generateNewSearchCommandMessage(chatId));
                }else {
                    setEditMessageView(answerConsumer.generateEditSearchCommandMessage(chatId, messageId));
                }
                managerStates.put(chatId, WAITING_FOR_EMPLOYEE_DATA_FOR_EDIT);
                break;
            case "/search":
                setNewMessageView(answerConsumer.generateNewSearchCommandMessage(chatId));
                managerStates.put(chatId, WAITING_FOR_EMPLOYEE_DATA_FOR_SEARCH);
                break;
            case "/info":
                setNewMessageView(answerConsumer.generateNewInfoMessage(chatId));
                managerStates.clear();
                break;
            default:
                telegramBot.sendAnswerMessage(new SendMessage(chatId.toString(),
                        "Извините, команда не распознана"));
                managerStates.clear();
        }
    }

    private void processCallbackQuery(Update update) {
        String callbackData = update.getCallbackQuery().getData();
        Integer messageId = update.getCallbackQuery().getMessage().getMessageId();
        Long chatId = update.getCallbackQuery().getMessage().getChatId();

        Map.Entry<Long, BotState> valueManagerStates;
        Long idEmployee = null;
        if(!managerStates.isEmpty()){
            valueManagerStates = managerStates.entrySet().iterator().next(); // Получаем первую запись из HashMap
            idEmployee = valueManagerStates.getKey();
        }

        switch (callbackData) {
            case "ADD_EMPLOYEE":
                setEditMessageView(answerConsumer.generateEditAddCommandMessage(chatId, messageId));
                managerStates.put(chatId, WAITING_FOR_EMPLOYEE_DATA_FOR_ADD);
                break;
            case "DELETE_EMPLOYEE":
                if(managerStates.isEmpty()){
                    setEditMessageView(answerConsumer.generateEditSearchCommandMessage(chatId, messageId));
                    managerStates.put(chatId, WAITING_FOR_EMPLOYEE_DATA_FOR_DELETE);
                }else {
                    //удаление внутри карточки
                    Employee employee = employeeService.searchEmployeeById(idEmployee);
                    employeeService.deleteEmployee(employee);
                    EditMessageText editMessageText = new EditMessageText("Сотрудник успешно удален.");
                    editMessageText.setChatId(chatId);
                    editMessageText.setMessageId(messageId);
                    setEditMessageView(editMessageText);
                    setNewMessageView(answerConsumer.generateNewMenuCommandMessage(chatId));
                    managerStates.clear();
                }
                break;
            case "EDIT_EMPLOYEE_INFORMATION":
                System.out.println("EDIT_EMPLOYEE_INFORMATION");
                setEditMessageView(answerConsumer.generateEditSearchCommandMessage(chatId, messageId));
                managerStates.put(chatId, WAITING_FOR_EMPLOYEE_DATA_FOR_EDIT);
                break;
            case "SEARCH_EMPLOYEE":
                setEditMessageView(answerConsumer.generateEditSearchCommandMessage(chatId, messageId));
                managerStates.put(chatId, WAITING_FOR_EMPLOYEE_DATA_FOR_SEARCH);
                break;
            case "BACK":
                setEditMessageView(answerConsumer.generateEditMenuCommandMessage(chatId, messageId));
                managerStates.clear();
                break;

            case "CHANGE_NAME":
                System.out.println("CHANGE_NAME");
                setNewMessageView(new SendMessage(chatId.toString(), "Введите имя:"));
                managerStates.clear();
                managerStates.put(idEmployee, WAITING_FOR_EMPLOYEE_DATA_FOR_EDIT_NAME); //изменяем значения по ключу
                break;
            case "CHANGE_PATRONYMIC":
                System.out.println("отчество: "+idEmployee);
                setNewMessageView(new SendMessage(chatId.toString(), "Введите отчество:"));
                managerStates.put(idEmployee, WAITING_FOR_EMPLOYEE_DATA_FOR_EDIT_PATRONYMIC);

                break;
            case "CHANGE_LAST_NAME":
                setNewMessageView(new SendMessage(chatId.toString(), "Введите фамилию:"));
                managerStates.put(idEmployee, WAITING_FOR_EMPLOYEE_DATA_FOR_EDIT_LAST_NAME);
                break;
            case "CHANGE_POSITION":
                setNewMessageView(new SendMessage(chatId.toString(), "Введите должность:"));
                managerStates.put(idEmployee, WAITING_FOR_EMPLOYEE_DATA_FOR_EDIT_POSITION);
                break;
            case "CHANGE_PROJECT":
                setNewMessageView(new SendMessage(chatId.toString(), "Введите проект:"));
                managerStates.put(idEmployee, WAITING_FOR_EMPLOYEE_DATA_FOR_EDIT_PROJECT);
                break;
            case "CHANGE_AVATAR":
                setNewMessageView(new SendMessage(chatId.toString(), "Отправьте фото:"));
                managerStates.put(idEmployee, WAITING_FOR_EMPLOYEE_DATA_FOR_EDIT_AVATAR);
                break;
        }
    }

    private void processEnteredEmployeeData(Update update) {//Обработка введенных данных о сотруднике и добавление в базу данных
        Long chatId = update.getMessage().getChatId();
        String messageText = update.getMessage().getText();

        Map.Entry<Long, BotState> valueManagerStates;
        Long idEmployee = null;
        if(!managerStates.isEmpty()){
            valueManagerStates = managerStates.entrySet().iterator().next(); // Получаем первую запись из HashMap
            idEmployee = valueManagerStates.getKey();
        }
        BotState currentState = managerStates.getOrDefault(idEmployee, BotState.START);
        switch (currentState) {
            case WAITING_FOR_EMPLOYEE_DATA_FOR_ADD:
                try {
                    employeeService.addEmployee(messageText);
                    sendSuccessMessage(chatId,"Сотрудник успешно добавлен.");
                    setNewMessageView(answerConsumer.generateNewMenuCommandMessage(chatId));
                    managerStates.clear();
                }catch (Exception e){
                    setNewMessageView(new SendMessage(chatId.toString(),"Введено не корректные данные.\nВведите как в примере!"));
                    setNewMessageView(answerConsumer.generateNewAddCommandMessage(chatId));
                }
                break;
            case WAITING_FOR_EMPLOYEE_DATA_FOR_DELETE:
                try {
                    Employee employeeForDelete = employeeService.searchEmployeeByFirstOrLastName(messageText).get(0);
                    employeeService.deleteEmployee(employeeForDelete);
                    sendSuccessMessage(chatId,"Сотрудник успешно удален.");
                    setNewMessageView(answerConsumer.generateNewMenuCommandMessage(chatId));
                    managerStates.clear();
                }catch (NotFoundException e){
                    setNewMessageView(new SendMessage(chatId.toString(),"Такого сотрудника нет."));
                    setNewMessageView(answerConsumer.generateNewMenuCommandMessage(chatId));
                    managerStates.clear();
                }
                break;
            case WAITING_FOR_EMPLOYEE_DATA_FOR_SEARCH:
                try {
                    List<Employee> listEmployView = employeeService.searchEmployeeByFirstOrLastName(messageText);
                    setNewMessageView(answerConsumer.generateListEmployMessage(chatId, listEmployView));
                    managerStates.remove(chatId);
                }catch (NotFoundException e){
                    setNewMessageView(new SendMessage(chatId.toString(),"Такого сотрудника нет."));
                    setNewMessageView(answerConsumer.generateNewMenuCommandMessage(chatId));
                    managerStates.clear();
                }
                break;
            case WAITING_FOR_EMPLOYEE_DATA_FOR_EDIT: //Карточка сотрудника находим и открываем
                try {
                    Employee employee = employeeService.searchEmployeeByFirstOrLastName(update.getMessage().getText()).get(0); //для отображения карточки первого пользователья
                    sendCardAfterEdit(chatId,employee.getId());
                    managerStates.clear();
                    managerStates.put(employee.getId(), START);
                }catch (NotFoundException e){
                    setNewMessageView(new SendMessage(chatId.toString(),"Такого сотрудника нет."));
                    setNewMessageView(answerConsumer.generateNewMenuCommandMessage(chatId));
                    managerStates.clear();
                }
                break;

            case WAITING_FOR_EMPLOYEE_DATA_FOR_EDIT_NAME:
                employeeService.editEmployeeName(idEmployee, messageText);
                sendSuccessMessage(chatId,"Имя сотрудника успешно изменено.");
                sendCardAfterEdit(chatId,idEmployee);
                break;
            case WAITING_FOR_EMPLOYEE_DATA_FOR_EDIT_PATRONYMIC:
                employeeService.editEmployeePatronymic(idEmployee, messageText);
                sendSuccessMessage(chatId,"Отчество сотрудника успешно изменено.");
                sendCardAfterEdit(chatId,idEmployee);
                break;
            case WAITING_FOR_EMPLOYEE_DATA_FOR_EDIT_LAST_NAME:
                employeeService.editEmployeeLastName(idEmployee, messageText);
                sendSuccessMessage(chatId,"Фамилия сотрудника успешно изменено.");
                sendCardAfterEdit(chatId,idEmployee);
                break;
            case WAITING_FOR_EMPLOYEE_DATA_FOR_EDIT_PROJECT:
                employeeService.editEmployeeProject(idEmployee, messageText);
                sendSuccessMessage(chatId,"Проект сотрудника успешно изменено.");
                sendCardAfterEdit(chatId,idEmployee);
                break;
            case WAITING_FOR_EMPLOYEE_DATA_FOR_EDIT_POSITION:
                employeeService.editEmployeePosition(idEmployee, messageText);
                sendSuccessMessage(chatId,"Должность сотрудника успешно изменено.");
                sendCardAfterEdit(chatId,idEmployee);
                break;
            default:
                log.info(update);
                break;
        }
    }
    private void sendCardAfterEdit(Long chatId, Long id) {
        Employee employeeAfterChanger = employeeService.searchEmployeeById(id);
        setNewPhotoView(answerConsumer.sendNewPhoto(chatId, employeeAfterChanger));
        setNewMessageView(answerConsumer.generateEmployCard(chatId, employeeAfterChanger));
    }
    private void sendSuccessMessage(Long chatId, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId.toString());
        sendMessage.setText(text);
        setNewMessageView(sendMessage);
    }
}
