package com.codemastersTournament.PersonnelManagerBot.models;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

@Entity
@Data
@Table(name = "Manager")
public class Manager {
    @Id
    @Column(name = "chat_id")
    private Long chatId;
    @Column(name = "name")
    private String name;
    @Column(name = "patronymic")
    private String patronymic;
    @Column(name = "last_name")
    private String lastName;
    @Column(name = "position")
    private String position;
    @Column(name = "project")
    private String project;
    @Lob
    @Column(name = "avatar", columnDefinition = "oid")
    private byte[] avatar;
    @Column(name = "arrival_date")
    private Timestamp arrivalDate;
    /*@OneToMany(mappedBy = "owner",cascade = CascadeType.PERSIST)
    private List<Book> books;*/

}
