package com.amkor.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LotInformationModel {
    private int seq = 0;
    private int factoryID = 0;
    private int siteID = 0;
    private int custCode = 0;
    private String custLot = "";
    private String custDcc = "";
    private String wipLot = "";
    private String wipDcc = "";
    private String prevBizWipLot = "";
    private String prevBizWipDcc = "";
    private String pkg = "";
    private String dms = "";
    private String lead = "";
    private String station = "";
    private String status1 = "";
    private String status2 = "";
    private String stripMark = "";
    private String originalPlant = "";
    private String currentPlant = "";
    private String fromPlant = "";
    private String toPlant = "";
    private String sourceDevice = "";
    private String targetDevice = "";
    private String dateCode = "";
    private long eohQty = 0L;
    private int eohWaferQty = 0;
    private long custAmkorID = 0L;
    private long wipAmkorID = 0L;
    private int wipAmkorSubID = 0;
    private int intColor = 2131099711;
    private String rackLocationCode = "";
    private String shelfLocationCode = "";
    private int binding = 0;
    private String lineCode = "";
    private String fgNo = "";
    private String bizType = "";
    private String isTurnkey = "";
    private String lotType = "";
    private String invoice = "";
    private int operationNo = 0;
    private String requestNo = "";
    private long requestDateTime = 0L;
    private String requestUser = "";
    private String executeType = "";
    private String fgsNo = "";
    private String binNo = "";
    private String groupNo = "";
    private String inOut = "";
    private long transferInDateTime = 0L;
    private long transferOutDateTime = 0L;
    private long checkDateTime = 0L;
    private long confirmDateTime = 0L;
    private long createDateTime = 0L;
    private long updateDateTime = 0L;
    private long splitLogDateTime = 0L;
    private long returnDateTime = 0L;
    private int splitLogQty = 0;
    private int returnQty = 0;
    private int returnWaferQty = 0;
    private int badge = 0;
    private String boxNo = "";
    private String boxID = "";
    private int boxSerialNo = 0;
    private int boxQty = 0;
    private int boxTotalCount = 0;
    private int scannedBoxQty = 0;
    private String carton = "";
    private String nation = "";
    private String region = "";
    private String traceCode = "";
    private String custPO = "";

    private int scannedEohQty = 0;
    private int count = 0;
    private int totalCount = 0;

    // Request and Response
    private String responseType = "";
    private String requestMessage  = "";
    private String requestMessageDesc  = "";
    private String responseMessage = "";
    private String responseMessageDesc = "";

    private String input00 = "";
    private String input01 = "";
    private String input02 = "";

    private String field00 = "";
    private String field01 = "";
    private String field02 = "";

    private String changeValue00 = "";
    private String changeValue01 = "";
    private boolean isScanned=false;
}
