package com.amkor.models;


import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "vehicle_header")
public class VehicleHeaderModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "visitor")
    private String visitor;

    @Column(name = "fwdr")
    private String fwdr;

    @Column(name = "cus_code")
    private int cusCode;

    @Column(name = "invoice")
    private String invoice;

    @Column(name = "date_from")
    private String dateFrom;

    @Column(name = "date_to")
    private String dateTo;

    @Column(name = "sequence_from")
    private int sequenceFrom;

    @Column(name = "sequence_to")
    private int sequenceTo;

    @Column(name = "biztype")
    private int bizType;

    @Column(name = "location")
    private String location;

    @Column(name = "plant")
    private String plant;

    @Column(name = "nation")
    private int nation;

    @Column(name = "region")
    private int region;

    @Column(name = "agency")
    private String agency;

    @Column(name = "status")
    private int status;

    @Column(name = "create_by")
    private String createBy;

    @Column(name = "update_by")
    private String updateBy;

    @Column(name = "created_at")
    private Date createdAt;

    @Column(name = "updated_at")
    private Date updatedAt;


    public VehicleHeaderModel() {

    }
}

