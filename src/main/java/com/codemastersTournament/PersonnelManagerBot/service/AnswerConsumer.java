package com.codemastersTournament.PersonnelManagerBot.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public interface AnswerConsumer {
    SendMessage consume(SendMessage sendMessage);
}
