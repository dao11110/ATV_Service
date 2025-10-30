package com.amkor.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ScheduleSubMasterModel {
    private int factoryId;
    private int siteId;
    private long amkId;
    private int subId;
    private long shipBackDate;
    private long rvShipBackDate;
    private int dateCode;
}
