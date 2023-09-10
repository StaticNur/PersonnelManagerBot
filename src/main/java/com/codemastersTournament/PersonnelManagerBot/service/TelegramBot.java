package com.codemastersTournament.PersonnelManagerBot.service;

import com.codemastersTournament.PersonnelManagerBot.config.BotConfig;
import com.codemastersTournament.PersonnelManagerBot.models.Manager;
import com.codemastersTournament.PersonnelManagerBot.models.ManagerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import java.time.Instant;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {
    private final BotConfig config;
    private final ManagerRepository managerRepository;
    static final String HELP_TEXT = "This bot is created to demonstrate Spring capabilities.\n\n" +
            "You can execute commands from the main menu on the left or by typing a command:\n\n" +
            "Type /start to see a welcome message\n\n" +
            "Type /mydata to see data stored about yourself\n\n" +
            "Type /help to see this message again";
    @Autowired
    public TelegramBot(BotConfig config,ManagerRepository managerRepository) {
        this.config = config;
        this.managerRepository = managerRepository;
        List<BotCommand> listOfCommand = new ArrayList<>();
        listOfCommand.add(new BotCommand("/start","start bot"));
        listOfCommand.add(new BotCommand("/edit","edit text"));
        listOfCommand.add(new BotCommand("/add","add text"));
        listOfCommand.add(new BotCommand("/delete","delete text"));
        listOfCommand.add(new BotCommand("/help","help"));
        try {
            execute(new SetMyCommands(listOfCommand, new BotCommandScopeDefault(),null));
        } catch (TelegramApiException e) {
            log.error("Error setting bot's command list: " + e.getMessage());
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        if(update.hasMessage() && update.getMessage().hasText()){
            String messageText = update.getMessage().getText();
            var chatId = update.getMessage().getChatId();
            switch (messageText){
                case "/start":
                    registerManager(update.getMessage());
                    startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                    break;
                case "/help":
                    startCommandReceived(chatId, HELP_TEXT);
                    break;
                default:
                    startCommandReceived(chatId, "Sorry, command was not recognized");

            }
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
    private void startCommandReceived(long chatId, String name) {
        String answer = "Hi, "+name+". How are you?";
        sendMessage(chatId, answer);
    }
    private void sendMessage(long chatId, String textToSend) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(textToSend);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error("Error occurred: "+e.getMessage());
        }
    }
    private void registerManager(Message manager){
        if(managerRepository.findById(manager.getChatId()).isEmpty()){
            var chatId = manager.getChatId();
            var chat = manager.getChat();

            Manager user = new Manager();

            user.setChatId(chatId);
            user.setFirstName(chat.getFirstName());
            user.setLastName(chat.getLastName());
            user.setUserName(chat.getUserName());
            Instant instant = Instant.now();
            Timestamp timestamp = Timestamp.from(instant);
            user.setRegisteredAt(timestamp);

            managerRepository.save(user);
        }
    }
}
