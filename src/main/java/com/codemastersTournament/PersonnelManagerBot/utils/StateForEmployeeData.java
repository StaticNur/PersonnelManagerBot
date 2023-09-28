package com.codemastersTournament.PersonnelManagerBot.utils;

import com.codemastersTournament.PersonnelManagerBot.models.Employee;
import com.codemastersTournament.PersonnelManagerBot.utils.enums.BotInputState;
import com.codemastersTournament.PersonnelManagerBot.utils.enums.Role;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class StateForEmployeeData {
    public static Role role;
    public static Map<BotInputState, Employee> stateAndCard = new HashMap<>();
}
