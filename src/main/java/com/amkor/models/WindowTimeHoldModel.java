package com.amkor.models;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class WindowTimeHoldModel {
 private int nFid;
 private int nSid;
 private long waferAmKorID;
 private int nSub;
 private String sLot;
 private String sDcc;
 private String sPkg;
 private String sDms;
 private String sLead;
 private long inDate;
 private long outDate;
 private int cusCode;



}
