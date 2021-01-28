package com.foxconn.fii.data.b04stencil.model;

import lombok.Data;
import javax.persistence.*;

@Data
@Entity
@Table(name = "TTensioning")
public class TTensioning {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String SN;

    @Column(name = "Model")
    private String model;

    @Column(name = "Product")
    private String product;

    @Column(name = "PCB")
    private String PCB;

    @Column(name = "Value_1")
    private String value1;

    @Column(name = "Value_2")
    private String value2;

    @Column(name = "Value_3")
    private String value3;

    @Column(name = "Value_4")
    private String value4;

    @Column(name = "Value_5")
    private String value5;

    @Column(name = "Value_6")
    private String value6;

    @Column(name = "Value_7")
    private String value7;

    @Column(name = "Value_8")
    private String value8;

    @Column(name = "Value_9")
    private String value9;

    @Column(name = "Result")
    private String result;

    @Column(name = "Testtime")
    private String testTime;

//    public String getValue1() {
//        return toString(value1);
//    }
//
//    public String getValue2() {
//        return toString(value2);
//    }
//
//    public String getValue3() {
//        return toString(value3);
//    }
//
//    public String getValue4() {
//        return toString(value4);
//    }
//
//    public String getValue5() {
//        return toString(value5);
//    }
//
//    public String getValue6() {
//        return toString(value6);
//    }
//
//    public String getValue7() {
//        return toString(value7);
//    }
//
//    public String getValue8() {
//        return toString(value8);
//    }
//
//    public String getValue9() {
//        return toString(value9);
//    }
//
//    public String toString(String value){
//        return value == null ? "N/A" : value;
//    }
}
