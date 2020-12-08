package com.foxconn.fii.data.primary.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "material_type")
public class MaterialType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;
}
