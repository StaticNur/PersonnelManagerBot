package com.codemastersTournament.PersonnelManagerBot.service.impl;

import com.codemastersTournament.PersonnelManagerBot.models.Manager;
import com.codemastersTournament.PersonnelManagerBot.repository.ManagerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.sql.Timestamp;
import java.time.Instant;

@Service
public class ManagerService {
    private final ManagerRepository managerRepository;
    @Autowired
    public ManagerService(ManagerRepository managerRepository) {
        this.managerRepository = managerRepository;
    }
    public boolean checkDatabase(Long chatId){
        return managerRepository.findById(chatId).isEmpty();
    }
    public void addNewManager(Message message){
        var chat = message.getChat();
        Manager user = new Manager();

        user.setChatId(message.getChatId());
        user.setName(chat.getFirstName());
        user.setLastName(chat.getLastName());
        Instant instant = Instant.now();
        Timestamp timestamp = Timestamp.from(instant);
        user.setArrivalDate(timestamp);
        managerRepository.save(user);
    }
}
