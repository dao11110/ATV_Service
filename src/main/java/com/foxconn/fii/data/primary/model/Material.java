package com.foxconn.fii.data.primary.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "material")
public class Material {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "id_bu")
    private Integer idBu;

    @Column(name = "id_team")
    private Integer idTeam;

    @Column(name = "id_type")
    private Integer idType;
}
