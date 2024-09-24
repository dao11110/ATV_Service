package com.amkor.models;

import lombok.Data;

@Data
public class OnLineScheduleSheetFileModel {
    private int factoryID = 0;
    private String type = "";
    private String recordID = "";
    private String path = "";
    private String file = "";
    private long effectiveTo = 0L;
    private long createDateTime = 0L;
    private String createBadge = "";
    private long maintDateTime = 0L;
    private String maintBadge = "";
}
