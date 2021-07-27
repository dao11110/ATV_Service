package com.foxconn.fii.data.primary.model.dcc;

import lombok.Data;

import java.util.Map;

@Data
public class Sheet {
    private String applicant;
    private String applicantTel;
    private String docNo;
    private String plant;
    private String bu;
    private String cft;
    private String applicantDept;
    private String applicantDate;
    private String effectiveDate;
    private String urgentType;
    private String productPn;
    private String customerPn;
    private String reason;
    private String description;
    private String controlRun;
    private Condition condition;

    public Sheet(Map<String, Object> mInput){
        this.applicant = (String) mInput.get("applicant");
        this.applicantTel = (String) mInput.get("applicant_tel");
        this.docNo = (String) mInput.get("doc_no");
        this.plant = (String) mInput.get("plant");
        this.bu = (String) mInput.get("bu");
        this.cft = (String) mInput.get("cft");
        this.applicantDept = (String) mInput.get("applicant_dept");
        this.applicantDate = (String) mInput.get("applicant_date");
        this.effectiveDate = (String) mInput.get("effective_date");
        this.urgentType = (String) mInput.get("urgent_type");
        this.productPn = (String) mInput.get("product_pn");
        this.customerPn = (String) mInput.get("customer_pn");
        this.reason = (String) mInput.get("reason");
        this.description = (String) mInput.get("description");
        this.controlRun = (String) mInput.get("control_run");
        this.condition = new Condition((Map<String, Object>) mInput.get("condition"));
    }
}
