package com.amkor.models;

import lombok.Data;

import javax.persistence.*;
import java.math.BigInteger;

@Data
@Entity
@Table(name = "WIPActionLog")
public class WipActionLogModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SERIAL")
    private BigInteger serial;

    @Column(name = "AMKORID")
    private long amkorId;

    @Column(name = "SUBID")
    private int subId;

    @Column(name = "LOTNO")
    private String lotNo;

    @Column(name = "DCC")
    private String dcc;

    @Column(name = "CUST")
    private int cust;

    @Column(name = "CUSTNAME")
    private String custname;

    @Column(name = "PKG")
    private String pkg;

    @Column(name = "DMS")
    private String dms;

    @Column(name = "LEAD")
    private String lead;

    @Column(name = "DEVICE")
    private String device;

    @Column(name = "OPR#")
    private int OPR;

    @Column(name = "LINECODE")
    private String lineCode;

    @Column(name = "TYPE")
    private String type;

    @Column(name = "SUBTYPE")
    private String subType;

    @Column(name = "REASON")
    private String reason;

    @Column(name = "CONTENTS")
    private String contents;

    @Column(name = "RESULT")
    private String result;

 @Column(name = "CPGM")
    private String cpgm;

 @Column(name = "CRDT")
    private long crdt;

 @Column(name = "CRTU")
    private String crtu;


}
