package com.amkor.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HoldReleaseLogModel {

    private int factoryID = 0;
    private int siteID = 0;
    private String stationCode = "";
    private long amkorID = 0L;
    private int amkorSubID = 0;
    private String status = "";
    private String futureHoldRequested = "";
    private float operationStep = 0.00f;
    private int operation = 0;//add
    private String holdReason = "";
    private String holdRemark = "";
    private long holdDateTime = 0L;
    private String holdProgramStamp = "";
    private int holdBadge = 0;
    private long futureHoldDateTime = 0L;//add
    private String releaseRemark = "";
    private long releaseDateTime = 0L;
    private String releaseProgramStamp = "";
    private int releaseBadge = 0;

}
