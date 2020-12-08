package com.foxconn.fii.data.primary.model;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;
import java.util.Map;

@Data
@Entity
@Table(name = "map_qc_sfc")
public class MapQcSfc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "id_qc")
    private Integer idQc;

    @Column(name = "station")
    private String station;


}
