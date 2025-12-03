package com.amkor.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PoGiStagingVO {
    private String plant="";
    private String poNo="";
    private String fgMtrlNo="";
    private String batchNo="";
    private int serialNo=0;
    private long postingDate=0L;
    private String movementType="";
    private String locStorage="";
    private double giQty=0.0d;
    private String uom="";
    private String salesOrder="";
    private int salesOrderItem=0;
    private String toDocNo="";
    private String rfmdDepLot="";
    private String transactionType="";
    private String ifTimeStamp="";
    private long ifDate=0L;
    private String ifStatus="";
    private String ifErrorDesc="";
    private String ifUser="";
    private String ifTimeExt="";
}
