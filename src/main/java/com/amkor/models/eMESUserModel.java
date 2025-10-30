package com.amkor.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class eMESUserModel {
    private int factoryID = 0;
    private String userID = "";
    private String password = "";
    private long passExpireDate = 0L;
    private String plant = "";
    private String lineCode = "";
    private String outQ = "";
    private String userGroup = "";
    private String name = "";
    private int badge = 0;
    private String email = "";
    private long officeTel = 0L;
    private long cellTel = 0L;
    private long lastLoginDateTime = 0L;
    private long lastLogoutDateTime = 0L;
    private String remark = "";
    private long createdDateTime = 0L;
    private long changedDateTime = 0L;

    private String isProcessingName = "";

    private String temp = "";

    private String system = "";
    private String module = "";
    private String txnName = "";
    private String level = "";
}
