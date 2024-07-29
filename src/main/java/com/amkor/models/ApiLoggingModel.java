package com.amkor.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
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
}
