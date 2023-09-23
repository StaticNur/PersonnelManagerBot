package com.codemastersTournament.PersonnelManagerBot.controller;

import com.codemastersTournament.PersonnelManagerBot.config.BotConfig;
import lombok.extern.log4j.Log4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Log4j
@Component
public class TelegramBot extends TelegramLongPollingBot {
    private final BotConfig config;
    private final UpdateProcessor updateProcessor;

    @Autowired
    public TelegramBot(UpdateProcessor updateProcessor,BotConfig config) {
        this.updateProcessor = updateProcessor;
        this.config = config;
        this.updateProcessor.registerBot(this);
        List<BotCommand> listOfCommand = new ArrayList<>();
        listOfCommand.add(new BotCommand("/start","Запуск бота"));
        listOfCommand.add(new BotCommand("/menu","Основные команды"));
        listOfCommand.add(new BotCommand("/add","Добавить сотрудника"));
        listOfCommand.add(new BotCommand("/delete","Удалить сотрудника"));
        listOfCommand.add(new BotCommand("/edit","Редактировать данные сотрудника"));
        listOfCommand.add(new BotCommand("/search","Поиск сотрудника"));
        listOfCommand.add(new BotCommand("/info","Информация о боте"));
        SetMyCommands setMyCommands = new SetMyCommands();
        setMyCommands.setCommands(listOfCommand);
        try {
            execute(setMyCommands);
        } catch (TelegramApiException e) {
            log.error("Ошибка настройки списка команд бота: " + e.getMessage());
        }
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        updateProcessor.processUpdate(update);
    }

    public void sendAnswerMessage(SendMessage message) {
        if (message != null) {
            try {
                execute(message);
            } catch (TelegramApiException e) {
                log.error(e);
            }
        }
    }

    public void sendAnswerMessage(SendPhoto sendPhoto) {
        if (sendPhoto != null) {
            try {
                execute(sendPhoto);
            } catch (TelegramApiException e) {
                log.error(e);
            }
        }
    }

    public  void sendAnswerMessage(EditMessageText message){
        if (message != null) {
            try {
                execute(message);
            } catch (TelegramApiException e) {
                log.error(e);
            }
        }
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
        HttpGet request = new HttpGet("https://api.telegram.org/file/bot" + getBotToken() + "/" + filePath);
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

    private byte[] downloadPhoto(PhotoSize photo){
        try {
            File file = execute(new GetFile(photo.getFileId()));
            String filePath = file.getFilePath();
            return downloadFileWithPhoto(filePath);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
