package com.foxconn.fii.data.primary.model.agile;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "agile_bom")
public class AgileBom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "model_name")
    private String modelName;

    @Column(name = "type")
    private String type;

    @Column(name = "part_type")
    private String partType;

    @Column(name = "description")
    private String description;

    @Column(name = "lifecycle_phase")
    private String lifeCyclePhase;

    @Column(name = "rev")
    private String rev;

    @Column(name = "rev_incorp")
    private String revIncorp;

    @Column(name = "rev_release")
    private Date revRelease;

    @Column(name = "effectivity_date")
    private Date effectivityDate;

    @CreationTimestamp
    @Column(name = "create_at", updatable = false)
    private Date createAt;

    @UpdateTimestamp
    @Column(name = "update_at")
    private Date updateAt;
}
