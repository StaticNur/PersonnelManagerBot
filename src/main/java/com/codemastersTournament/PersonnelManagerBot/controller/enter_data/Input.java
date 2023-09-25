package com.codemastersTournament.PersonnelManagerBot.controller.enter_data;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface Input {
    void handle(Update update);
}
