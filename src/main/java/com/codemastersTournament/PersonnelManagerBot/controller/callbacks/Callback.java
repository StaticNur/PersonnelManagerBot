package com.codemastersTournament.PersonnelManagerBot.controller.callbacks;

import com.codemastersTournament.PersonnelManagerBot.utils.enums.ButtonCallBackQuery;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class Callback {
    private ButtonCallBackQuery buttonCallBackQuery;
    private String data;
}
