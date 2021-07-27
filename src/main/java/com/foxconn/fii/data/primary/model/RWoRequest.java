package com.foxconn.fii.data.primary.model;

import com.foxconn.fii.DataStatic;
import com.foxconn.fii.data.b04sfc.model.B04RWoRequest;
import com.foxconn.fii.data.f12sfc.model.F12RWoRequest;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "r_wo_request")
public class RWoRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "wo")
    private String wo;

    @Column(name = "wo_qty")
    private Integer woQty;

    @Column(name = "p_no")
    private String pNo;

    @Column(name = "p_name")
    private String pName;

    @Column(name = "cust_kp_no")
    private String custKpNo;

    @Column(name = "standard_qty")
    private Integer standardQty;

    @Column(name = "wo_request")
    private Integer woRequest;

    @Column(name = "deliver_qty")
    private Integer deliverQty;

    @Column(name = "checkout_qty")
    private Integer checkoutQty;

    @Column(name = "return_qty")
    private Integer returnQty;

    @Column(name = "download_time")
    private Date downloadTime;

    @Column(name = "build")
    private String build;

    public RWoRequest(){ }

    public RWoRequest(B04RWoRequest mData){
        this.wo = mData.getWo();
        this.woQty = mData.getWoQty();
        this.pNo = mData.getPNo();
        this.pName = mData.getPName();
        this.custKpNo = mData.getCustKpNo();
        this.standardQty = mData.getStandardQty();
        this.woRequest = mData.getWoRequest();
        this.deliverQty = mData.getDeliverQty();
        this.checkoutQty = mData.getCheckoutQty();
        this.returnQty = mData.getReturnQty();
        this.downloadTime = mData.getDownloadTime();
        this.build = DataStatic.ITSFC.FACTORY.B04;
    }

    public RWoRequest(F12RWoRequest mData){
        this.wo = mData.getWo();
        this.woQty = mData.getWoQty();
        this.pNo = mData.getPNo();
        this.pName = mData.getPName();
        this.custKpNo = mData.getCustKpNo();
        this.standardQty = mData.getStandardQty();
        this.woRequest = mData.getWoRequest();
        this.deliverQty = mData.getDeliverQty();
        this.checkoutQty = mData.getCheckoutQty();
        this.returnQty = mData.getReturnQty();
        this.downloadTime = mData.getDownloadTime();
        this.build = DataStatic.ITSFC.FACTORY.F12;
    }
}