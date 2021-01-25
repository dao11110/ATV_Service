package com.foxconn.fii.data.primary.model;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;
import java.util.Map;

@Data
@Entity
@Table(name = "r_smt_fai_config")
public class RSmtFaiConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "id_qc")
    private Integer idQc;

    @Column(name = "station")
    private String station;

    @Column(name = "wo")
    private String wo;

    @Column(name = "model_name")
    private String modelName;

    @Column(name = "process_flag")
    private String processFlag;

    @Column(name = "request_time")
    private Date requestTime;

    @Column(name = "reason")
    private String reason;

    @Column(name = "reason_fill")
    private String reasonFill;

    @Column(name = "materials")
    private String materials;

    @Column(name = "material_fill")
    private String materialFill;

    @Column(name = "ecn_no")
    private String ecnNo;

    @CreationTimestamp
    @Column(name = "create_at", updatable = false)
    private Date createAt;

    public RSmtFaiConfig(){}

    public RSmtFaiConfig(Map<String, Object> mData){
        this.station = (String) mData.get("STATION");
        this.wo = (String) mData.get("WO");
        this.modelName = (String) mData.get("MODEL_NAME");
        this.processFlag = (String) mData.get("PROCESS_FLAG");
        this.requestTime = (Date) mData.get("REQUEST_TIME");
        this.reason = (String) mData.get("REASON");
        this.reasonFill = REASON.getDataFill(this.reason);
    }

    public void setMaterial(Map<String, Object> mData){
        this.materials = (String) mData.get("materials");
        this.materialFill = (String) mData.get("material_fill");
    }

    public static class REASON{
        public static String CHANGE_WO = "Change WO";
        public static String CHANGE_SHIFT = "Change Shift";
        public static String getDataFill(String mReason){
            if(mReason.equals(CHANGE_WO)){
                return "產品更換诀Đổi sản phẩm";
            }else{
                return "復線Mở chuyền";
            }
        }
    }

}
