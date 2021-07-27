package com.foxconn.fii.data.primary.model;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "dcc_application_meta")
public class DccApplicationMeta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "id_dcc_application")
    private Integer idDcc;

    @Column(name = "meta_key")
    private String metaKey;

    @Column(name = "meta_value")
    private String metaValue;

    @Column(name = "description")
    private String description;

    @CreationTimestamp
    @Column(name = "create_at", updatable = false)
    private Date createAt;

    @UpdateTimestamp
    @Column(name = "update_at")
    private Date updateAt;
}
