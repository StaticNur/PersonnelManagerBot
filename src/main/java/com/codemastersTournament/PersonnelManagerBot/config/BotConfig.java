package com.codemastersTournament.PersonnelManagerBot.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@Data
@PropertySource("classpath:application.properties")
public class BotConfig {
    @Value("${bot.userName}")
    private String botName;
    @Value("${bot.token}")
    private String token;
    @Value("${service.file_info.uri}")
    private String telegramFileUrl;
    @Value("${https://api.telegram.org/file/bot{token}/{filePath}")
    private String destinationPath;
}
