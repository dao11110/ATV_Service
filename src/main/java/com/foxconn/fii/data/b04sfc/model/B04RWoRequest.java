package com.foxconn.fii.data.b04sfc.model;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@IdClass(B04RWoRequest.RWoRequestId.class)
@Table(schema = "MES4", name = "R_WO_REQUEST")
public class B04RWoRequest {

    @Id
    @Column(name = "WO")
    private String wo;

    @Column(name = "WO_QTY")
    private Integer woQty;

    @Column(name = "P_NO")
    private String pNo;

    @Column(name = "P_NAME")
    private String pName;

    @Id
    @Column(name = "CUST_KP_NO")
    private String custKpNo;

    @Column(name = "STANDARD_QTY")
    private Integer standardQty;

    @Column(name = "WO_REQUEST")
    private Integer woRequest;

    @Column(name = "DELIVER_QTY")
    private Integer deliverQty;

    @Column(name = "DOWNLOAD_TIME")
    private Date downloadTime;

    @Column(name = "CHECKOUT_QTY")
    private Integer checkoutQty;

    @Column(name = "RETURN_QTY")
    private Integer returnQty;

    @Data
    public static class RWoRequestId implements Serializable {
        private String wo;
        private String custKpNo;
    }
}
