package com.foxconn.fii.data.primary.model.dcc;

import lombok.Data;

import java.util.Map;

@Data
public class Condition {
    private String wo;
    private String sn;
    private String quantity;
    private String from;
    private String to;

    public Condition(Map<String, Object> mCondition){
        this.wo = (String) mCondition.get("wo");
        this.sn = (String) mCondition.get("sn");
        this.quantity = (String) mCondition.get("quantity");
        this.from = (String) mCondition.get("from");
        this.to = (String) mCondition.get("to");
    }
}
