package com.amkor.models;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "vehicle_item")
public class VehicleItemModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "id_header")
    private int idHeader;
    @Column(name = "seq")
    private int seq;
    @Column(name = "forwarder_code")

    private String forwarderCode;
    @Column(name = "user_name")
    private String userName;
    @Column(name = "user_id")
    private String userID;
    @Column(name = "license_number")
    private String licenseNumber;
    @Column(name = "phone_number")
    private String phoneNumber;
    @Column(name = "shipment_date")
    private String shipmentDate;
    @Column(name = "cus_code")
    private int custCode;
    @Column(name = "carton_no")
    private int cartonNo;
    @Column(name = "carton_sequence")
    private int cartonSequence;
    @Column(name = "invoice_no")
    private String invoiceNo;
    @Column(name = "shipping_plant")
    private String shippingPlant;
    @Column(name = "pickup_location")
    private String pickupLocation;
    @Column(name = "cust_name")
    private String custName;
    @Column(name = "seq_btn_color")
    private int seqBtnColor;

}
