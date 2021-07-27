package com.foxconn.fii.data.primary.model.agile;

import lombok.Data;

import javax.persistence.*;
import java.util.Map;

@Data
@Entity
@Table(name = "agile_bom_pn")
public class AgileBomPn {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "level")
    private String level;

    @Column(name = "pn")
    private String pn;

    @Column(name = "description")
    private String description;

    @Column(name = "product_line")
    private String productLine;

    @Column(name = "rev")
    private String rev;

    @Column(name = "rev_release_date")
    private String revRelease;

    @Column(name = "bom_qty")
    private String bomQty;

    @Column(name = "bom_find_num")
    private String bomFindNum;

    @Column(name = "bom_plan")
    private String bomPlan;

    @Column(name = "type")
    private String type;

    @Column(name = "type_item")
    private String typeItem;

    @Column(name = "mfr")
    private String mfr;

    @Column(name = "product")
    private String product;

    @Column(name = "location")
    private String location;

    @Column(name = "id_bom_version")
    private Integer idBomVersion;

    @Column(name = "version")
    private String version;

    @Column(name = "ecn_no")
    private String ecnNo;

    public AgileBomPn(){ }

    public AgileBomPn(Map<String, Object> itemPn){
        this.level = (String) itemPn.get("Level");
        this.pn = (String) itemPn.get("Item Number");
        this.description = (String) itemPn.get("Item Description");
        this.rev = (String) itemPn.get("Item Rev");
        this.bomQty = (String) itemPn.get("Qty");
        this.bomFindNum = (String) itemPn.get("Find Num");
        this.location = (String) itemPn.get("Ref Des");
        this.bomPlan = (String) itemPn.get("Plant");
        if(location.trim().length() > 0){
            this.typeItem = "PN";
        }else{
            this.typeItem = "DOCUMENT";
        }
        getRevDetail(this.rev);
//        itemPn.get("Effective Date");
    }

    private void getRevDetail(String mInput){
        String arr[] = mInput.split(" ");
        String mVersion = "";
        String mEcn = "";
        if(arr.length > 0){
            for(int i = 0; i < arr.length; i++) {
                if (arr[i].trim().length() > 0) {
                    if(mVersion.length() == 0){
                        mVersion = arr[i].trim();
                    } else {
                        mEcn = arr[i].trim();
                    }
                }
            }
        }
        this.version = mVersion;
        this.ecnNo = mEcn;
    }
}
