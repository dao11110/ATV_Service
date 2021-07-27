package com.foxconn.fii.data.primary.model;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;
import java.util.Map;

@Data
@Entity
@Table(name = "r_smt_fai_station")
public class RSmtFaiStation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "id_fai_config")
    private Integer idFaiConfig;

    @Column(name = "station")
    private String station;

    @Column(name = "station_name")
    private String stationName;

    @Column(name = "program_name")
    private String programName;

    @Column(name = "wo")
    private String wo;

    @Column(name = "ecn_no")
    private String ecnNo;

    @Column(name = "dev")
    private String dev;

    @CreationTimestamp
    @Column(name = "create_at", updatable = false)
    private Date createAt;

    public RSmtFaiStation(){}

    public RSmtFaiStation(Integer idConfig, String lineName, Map<String, Object> mStation){
        this.idFaiConfig = idConfig;
        this.station = (String) mStation.get("STATION");
        this.stationName = this.station.replace(lineName, "");
        this.programName = (String) mStation.get("PROGRAM_NAME");
        this.wo = (String) mStation.get("WO");
        this.ecnNo = (String) mStation.get("ECN_NO");
        this.dev = (String) mStation.get("DEV");
    }
}
