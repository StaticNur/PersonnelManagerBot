package com.codemastersTournament.PersonnelManagerBot.controller.enter_data;

import com.codemastersTournament.PersonnelManagerBot.controller.TelegramBot;
import com.codemastersTournament.PersonnelManagerBot.controller.sender.SubmittingAdditionalMessage;
import com.codemastersTournament.PersonnelManagerBot.models.Employee;
import com.codemastersTournament.PersonnelManagerBot.service.impl.AnswerConsumerImpl;
import com.codemastersTournament.PersonnelManagerBot.service.impl.EmployeeService;
import com.codemastersTournament.PersonnelManagerBot.utils.StateForEmployeeData;
import com.codemastersTournament.PersonnelManagerBot.utils.enums.BotInputState;
import lombok.extern.log4j.Log4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
@Component
@Log4j
public class ChangeAvatarEmployee implements Input{
    private final AnswerConsumerImpl answerConsumer;
    private final EmployeeService employeeService;
    private final SubmittingAdditionalMessage message;
    private TelegramBot telegramBot;
    public void registerBot(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }
    @Autowired
    public ChangeAvatarEmployee(AnswerConsumerImpl answerConsumer, EmployeeService employeeService,
                                SubmittingAdditionalMessage message) {
        this.answerConsumer = answerConsumer;
        this.employeeService = employeeService;
        this.message = message;
    }

    @Override
    public void handle(Update update) {
        Long chatId = update.getMessage().getChatId();

        Map.Entry<BotInputState, Employee> stateForEmployeeData = StateForEmployeeData.stateAndCard.entrySet().iterator().next();
        Long idEmployee = stateForEmployeeData.getValue().getId();
        byte[] photoByte;
        try {
            photoByte = getPhotoFile(update);
            employeeService.editEmployeeAvatar(idEmployee, photoByte);
            message.sendMessage(new SendMessage(chatId.toString(), "Аватарка, сотрудника успешно изменено!"));
        } catch (Exception e) {
            log.error(e);
        }
        sendCardAfterEdit(chatId, idEmployee);
    }

    private void sendCardAfterEdit(Long chatId, Long id) {
        Employee employeeAfterChanger = employeeService.searchEmployeeById(id);
        message.sendMessage(answerConsumer.sendNewPhoto(chatId, employeeAfterChanger));
        message.sendMessage(answerConsumer.generateEmployCard(chatId, employeeAfterChanger));

        StateForEmployeeData.stateAndCard.clear();
        StateForEmployeeData.stateAndCard.put(BotInputState.OPEN_CARD, employeeAfterChanger);
    }

    public byte[] getPhotoFile(Update update) {
        PhotoSize photo = update.getMessage().getPhoto().stream()
                .max(java.util.Comparator.comparing(PhotoSize::getFileSize))
                .orElse(null);
        if (photo != null) {
            try {
                byte[] photoBytes = downloadPhoto(photo);
                return photoBytes;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private byte[] downloadFileWithPhoto(String filePath) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet request = new HttpGet("https://api.telegram.org/file/bot5980029815:AAELVVALFXUXjlDJ4EN3xf94TMbEFbPqNFM/" + filePath);
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                // Прочитать содержимое файла из ответа
                HttpEntity entity = response.getEntity();
                InputStream inputStream = entity.getContent();
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                return outputStream.toByteArray();
            } else {
                System.err.println("Ошибка при загрузке файла: " + response.getStatusLine());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private byte[] downloadPhoto(PhotoSize photo) {
        try {
            File file = telegramBot.execute(new GetFile(photo.getFileId()));
            String filePath = file.getFilePath();
            return downloadFileWithPhoto(filePath);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}