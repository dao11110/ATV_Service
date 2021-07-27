package com.foxconn.fii.data.primary.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "paperless_log")
public class PaperLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name")
    private String name;

    @Column(name = "log")
    private String log;

    @Column(name = "_function")
    private String function;

    @Column(name = "factory")
    private String factory;

    @Column(name = "team")
    private String team;

    @Override
    public String toString() {
        return "PaperLog{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", log='" + log + '\'' +
                ", function='" + function + '\'' +
                ", factory='" + factory + '\'' +
                ", team='" + team + '\'' +
                '}';
    }
}
