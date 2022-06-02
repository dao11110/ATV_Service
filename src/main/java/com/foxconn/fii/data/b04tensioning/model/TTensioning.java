package com.foxconn.fii.data.b04tensioning.model;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@IdClass(TTensioning.TTensioningId.class)
@Table(name = "TTensioning)")
public class TTensioning {

    @Id
    @Column(name = "SN")
    private String SN;

    @Column(name ="All_parts_code")
    private String AllPartsCode;

    @Column(name ="Product_Name")
    private String ProductName;

    @Column(name ="PCB_Rev")
    private String PCBRev;

    @Column(name ="Value_1")
    private String Value1;

    @Column(name ="Value_2")
    private String Value2;

    @Column(name ="Value_3")
    private String Value3;

    @Column(name ="Value_4")
    private String Value4;

    @Column(name ="Value_5")
    private String Value5;

    @Column(name ="Value_6")
    private String Value6;

    @Column(name ="Value_7")
    private String Value7;

    @Column(name ="Value_8")
    private String Value8;

    @Column(name ="Value_9")
    private String Value9;

    @Column(name ="Result")
    private String Result;

    @Column(name ="Flag_Type")
    private String FlagType;

    @Id
    @Column(name ="Testtime")
    private String TestTime;

    @Data
    public  class  TTensioningId implements Serializable {
        private String SN;
        private String TestTime;
    }
}
