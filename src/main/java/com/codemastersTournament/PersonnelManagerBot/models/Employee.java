package com.codemastersTournament.PersonnelManagerBot.models;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

@Entity
@Data
@Table(name = "Employee")
public class Employee {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
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
    /*@ManyToOne
    @JoinColumn(name = "fk_id",referencedColumnName = "id")
    private Person owner;*/
}
