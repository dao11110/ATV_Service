package com.foxconn.fii.data.primary.model;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "models")
public class Model {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "code")
    private String code;

    @Column(name = "name")
    private String name;

    @Column(name = "id_bu")
    private Integer idBu;

    @Column(name = "id_team")
    private Integer idTeam;

    @Column(name = "id_section")
    private  Integer idSection;

    @Column(name = "id_status")
    private Integer idStatus;

    @Column(name = "description")
    private String description;

    @CreationTimestamp
    @Column(name = "create_at", updatable = false)
    private Date createAt;

    @UpdateTimestamp
    @Column(name = "update_at")
    private Date updateAt;
}
