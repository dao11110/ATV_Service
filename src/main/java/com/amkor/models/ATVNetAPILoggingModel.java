package com.amkor.models;


import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "api_logging")
public class ATVNetAPILoggingModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "user_badge")
    private String userBadge;

    @Column(name = "date_time")
    private long datetime;

    @Column(name = "logging")
    private String logging;

    @Column(name = "note")
    private String note;

    public ATVNetAPILoggingModel(String userBadge, long datetime, String logging, String note) {
        this.datetime = datetime;
        this.userBadge = userBadge;
        this.logging = logging;
        this.note = note;
    }
}
