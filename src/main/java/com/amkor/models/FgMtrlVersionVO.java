package com.amkor.models;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FgMtrlVersionVO {
    private String plant = "";
    private String mtrlNo = "";
    private String mtrlType = "";
    private String mtrlDesc = "";
    private String version = "";
    private String shortTextPRVer = "";
    private int cust = 0;
    private String pdl = "";
    private String testDevice = "";
    private String motherDevice = "";
    private String sourceDevice = "";
    private String bdNo = "";
    private String bdRev = "";
    private String markSpec = "";
    private String markRev = "";
    private String cpfNo = "";
    private String cpfRev = "";
    private String pinComment = "";
    private double pinNo = 0.0d;
    private double numOfTray = 0.0d;
    private double loadTrayQty = 0.0d;
    private double numOfTube = 0.0d;
    private double loadTubeQty = 0.0d;
    private double coverTray = 0.0d;
    private double innerTray = 0.0d;
    private String domesticFlag = "";
    private String ifTimeStamp = "";
    private long ifDateTime = 0l;
    private String ifStatus = "";
    private String ifError = "";
    private String ifUser = "";
    private String pakNo = "";
    private String pakRev = "";
    private String pasteStDwgNo = "";
    private String pasteStDwgRev = "";
    private String fluxStDwgNo = "";
    private String fluxStDwgRev = "";

    private String pasteStDwgNoBt = "";
    private String pasteStDwgRevBt = "";
    private String fluxStDwgNoBt = "";
    private String fluxStDwgRevBt = "";

    private String backSidePD = "";
    private String backSidePDRev = "";

    private String mtrlGroup = "";
    private String assyFlag = "";
    private String deletionFlag = "";
    private String charName = "";
    private String charValue = "";
}
