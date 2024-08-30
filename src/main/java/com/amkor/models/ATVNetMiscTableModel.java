package com.amkor.models;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "misc_table")
public class ATVNetMiscTableModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "factory_id")
    private int factoryId;

    @Column(name = "table_id")
    private String tableId;

    @Column(name = "table_code_01")
    private String tableCode01;

    @Column(name = "table_code_02")
    private String tableCode02;

    @Column(name = "short_desc")
    private String shortDesc;

    @Column(name = "long_desc")
    private String longDesc;
}
