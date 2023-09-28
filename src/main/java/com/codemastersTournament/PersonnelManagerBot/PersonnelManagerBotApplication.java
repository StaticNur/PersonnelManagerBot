package com.codemastersTournament.PersonnelManagerBot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class PersonnelManagerBotApplication {
	public static void main(String[] args) {
		SpringApplication.run(PersonnelManagerBotApplication.class, args);
	}    
}
