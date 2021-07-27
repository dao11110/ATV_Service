package com.foxconn.fii.data.primary.model.agile;

import lombok.Data;

import javax.persistence.*;
import java.util.Map;

@Data
@Entity
@Table(name = "agile_bom_pn_mfr")
public class AgileBomPnMfr {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "mfr_name")
    private String mfrName;

    @Column(name = "mfr_pn")
    private String mfrPn;

    @Column(name = "qualification_status")
    private String qualificationStatus;

    @Column(name = "rohs_status")
    private String rohsStatus;

    @Column(name = "mpn_assembly")
    private String mpnAssembly;

    @Column(name = "reference_notes")
    private String referenceNotes;

    @Column(name = "id_bom_pn")
    private Integer idBomPn;

    public AgileBomPnMfr(){ }

    public AgileBomPnMfr(Map<String, Object> mData){
        this.mfrName = (String) mData.get("Mfr. Name");
        this.mfrPn = (String) mData.get("Mfr. Part Number");
        this.qualificationStatus = (String) mData.get("Qualification Status");
        this.rohsStatus = (String) mData.get("RoHS status (Customer)");
        this.mpnAssembly = (String) mData.get("MPN Assembly Solder");
        this.referenceNotes = (String) mData.get("Reference Notes");
    }
}
