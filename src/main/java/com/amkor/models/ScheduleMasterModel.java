package com.amkor.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ScheduleMasterModel {

    private int factoryId;
    private int siteId;
    private long amkId;
    private int subId;
    private String lotName;
    private String lotDcc;
    private String station;
    private String status1;
    private String status2;
}
