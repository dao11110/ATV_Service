package com.foxconn.fii.data.primary.model;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "output")
public class Output {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "id_model")
    private Integer idModel;

    @Column(name = "output_value")
    private Integer outputValue;

    @Column(name = "date_time")
    private String dateTime;

    @Column(name = "id_line")
    private Integer idLine;

    @CreationTimestamp
    @Column(name = "create_at")
    private Date createAt;

    @UpdateTimestamp
    @Column(name = "update_at")
    private Date updateAt;
}
