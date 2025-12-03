package com.amkor.models;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "AMM_LOG_SPLIT")
public class SplitLogAMMHistoryModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "lot_selected")
    private String lotSelected;

    @Column(name = "badge")
    private String badge;
    @Column(name = "stage")
    private String stage;

    @CreationTimestamp
    @Column(name = "created_at")
    private Date createdAt;
}
