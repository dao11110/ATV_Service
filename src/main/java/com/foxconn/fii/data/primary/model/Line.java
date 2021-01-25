package com.foxconn.fii.data.primary.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "line")
public class Line {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name")
    private String name;

    @Column(name = "id_bu")
    private int idBu;

    @Column(name = "id_team")
    private int idTeam;

    @Column(name = "id_flag")
    private int idFlag;

    @Column(name = "position")
    private int position;

    @Column(name = "floor")
    private String floor;

    @Column(name = "description")
    private String description;

    @Column(name = "display_name")
    private String displayName;

    @Column(name = "[group]")
    private Integer idGroup;

    @Column(name = "id_status")
    private Integer idStatus;
}
