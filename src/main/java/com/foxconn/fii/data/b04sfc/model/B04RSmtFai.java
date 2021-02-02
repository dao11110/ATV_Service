package com.foxconn.fii.data.b04sfc.model;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@IdClass(B04RSmtFai.RSmtFaiId.class)
@Table(schema = "MES4", name = "R_SMT_FAI")
public class B04RSmtFai {
    @Column(name = "STATION")
    private String station;

    @Id
    @Column(name = "WO")
    private String wo;

    @Column(name = "MODEL_NAME")
    private String modelName;

    @Column(name = "PROCESS_FLAG")
    private String processFlag;

    @Id
    @Column(name = "REQUEST_TIME")
    private Date requestTime;

    @Column(name = "REASON")
    private String reason;

    @Data
    public static class RSmtFaiId implements Serializable {
        private String wo;
        private Date requestTime;
    }
}
