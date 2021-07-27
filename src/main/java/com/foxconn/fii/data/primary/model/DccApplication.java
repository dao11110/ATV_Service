package com.foxconn.fii.data.primary.model;

import com.foxconn.fii.data.primary.model.dcc.Sheet;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "dcc_application")
public class DccApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "applicant")
    private String applicant;

    @Column(name = "applicant_tel")
    private String applicantTel;

    @Column(name = "doc_no")
    private String docNo;

    @Column(name = "plant")
    private String plant;

    @Column(name = "bu")
    private String bu;

    @Column(name = "cft")
    private String cft;

    @Column(name = "applicant_dept")
    private String applicantDept;

    @Column(name = "applicant_date")
    private String applicantDate;

    @Column(name = "effective_date")
    private String effectiveDate;

    @Column(name = "urgent_type")
    private String urgentType;

    @Column(name = "product_pn")
    private String productPn;

    @Column(name = "customer_pn")
    private String customerPn;

    @Column(name = "reason")
    private String reason;

    @Column(name = "description")
    private String description;

    @Column(name = "control_run")
    private String controlRun;

    @Column(name = "limited_wo")
    private String limitedWo;

    @Column(name = "limited_sn")
    private String limitedSn;

    @Column(name = "limited_quantity")
    private String limitedQuantity;

    @Column(name = "limited_time")
    private String limitedTime;

    @Column(name = "type")
    private String type;

    @CreationTimestamp
    @Column(name = "create_at", updatable = false)
    private Date createAt;

    @Transient
    private Object metaData;

    public DccApplication(){}

    public DccApplication(Sheet mDccReport){
        mapData(mDccReport);
//        this.applicantTel
    }

    private void mapData(Sheet mSheet){
        this.applicant = mSheet.getApplicant();
        this.applicantTel = mSheet.getApplicantTel();
        this.docNo = mSheet.getDocNo();
        this.plant = mSheet.getPlant();
        this.bu = mSheet.getBu();
        this.cft = mSheet.getCft();
        this.applicantDept = mSheet.getApplicantDept();
        this.applicantDate = mSheet.getApplicantDate();
        this.effectiveDate = mSheet.getEffectiveDate();
        this.urgentType = mSheet.getUrgentType();
        this.productPn = mSheet.getProductPn();
        this.customerPn = mSheet.getCustomerPn();
        this.reason = mSheet.getReason();
        this.description = mSheet.getDescription();
        this.controlRun = mSheet.getControlRun();
        this.limitedWo = mSheet.getCondition().getWo();
        this.limitedQuantity = mSheet.getCondition().getQuantity();
        this.limitedTime = mSheet.getCondition().getFrom() + " - " + mSheet.getCondition().getTo();
        this.limitedSn = mSheet.getCondition().getSn();
    }


}
