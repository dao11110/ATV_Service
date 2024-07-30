package com.amkor.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ApiLoggingModel {
    private int cifcid;  // factory id
    private int ciasid;  // site id
    private String cistn;  // station
    private long ciamkr; // amkor id
    private int cisub; // amkor sub id
    private String cibztp;  // business type
    private String cists;  // status
    private int ciseq;  // sequence
    private int ciopr;  // operation
    private String cichfd;  // change field
    private String ciogvl;  // orignal value
    private String cinwvl;  // new value
    private String cirsn;  // reason code
    private int cichbg;  // badge
    private long cichdt;  // change date
    private long cirqdt;  // request date time
    private String cirqpg;  // request pgm
    private int cirqbg;  // request badge
    private long ciacdt;  // accept date time
    private String ciacpg;  // accept pgm
    private int ciacbg;  // accept badge

    public ApiLoggingModel() {
        cifcid = 0; // factory id
        ciasid = 0;// site id
        cistn = ""; // station
        ciamkr = 0;// amkor id
        cisub = 0;// amkor sub id
        cibztp = ""; // business type
        cists = ""; // status
        ciseq = 0;// sequence
        ciopr = 0;// operation
        cichfd = ""; // change field
        ciogvl = ""; // orignal value
        cinwvl = "";// new value
        cirsn = "";// reason code
        cichbg = 0;// badge
        cichdt = 0;// change date
        cirqdt = 0;// request date time
        cirqpg = "";// request pgm
        cirqbg = 0;// request badge
        ciacdt = 0;// accept date time
        ciacpg = "";// accept pgm
        ciacbg = 0;// accept badge
    }
}
