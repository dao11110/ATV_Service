package com.foxconn.fii.data.primary.model.agile;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

@Data
@Entity
@Table(name = "agile_ecn")
public class AgileEcn {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "ecn_no")
    private String ecnNo;

    @Column(name = "status")
    private String status;

    @Column(name = "change_type")
    private String changeType;

    @Column(name = "description")
    private String description;

    @Column(name = "reason")
    private String reason;

    @Column(name = "change_analyst")
    private String changeAnalyst;

    @Column(name = "originator")
    private String originator;

    @Column(name = "date_originated")
    private String dateOriginated;

    @Column(name = "_date_originated")
    private Date _dateOriginated;

    @Column(name = "date_released")
    private String dateReleased;

    @Column(name = "_date_released")
    private Date _dateReleased;

    @Column(name = "final_complete_date")
    private String finalCompleteDate;

    @Column(name = "product_line")
    private String productLine;

    @Column(name = "error_count")
    private Integer errorCount;

    @CreationTimestamp
    @Column(name = "create_at", updatable = false)
    private Date createAt;

    public AgileEcn(Map<String, Object> mData){
        this.ecnNo = (String) mData.get("number");
        this.status = (String) mData.get("status");
        this.changeType = (String) mData.get("change_type");
        this.description = (String) mData.get("description_of_change");
        this.reason = (String) mData.get("reason_for_change");
        this.changeAnalyst = (String) mData.get("change_analyst");
        this.originator = (String) mData.get("originator");
        this.dateOriginated = (String) mData.get("date_originated");
        this.dateReleased = (String) mData.get("date_released");
        this.finalCompleteDate = (String) mData.get("final_complete_date");
        this.productLine = (String) mData.get("productline");
        this.errorCount = (Integer) mData.get("error_count");
        try { this._dateOriginated = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy").parse(this.dateOriginated); }catch (Exception e){ this._dateOriginated = null; }
        try { this._dateReleased = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy").parse(this.dateReleased); }catch (Exception e){ this._dateReleased = null; }
    }

    public AgileEcn(String mEcnNo, String mStatus){
        this.ecnNo = mEcnNo;
        this.status = mStatus;
    }

}
