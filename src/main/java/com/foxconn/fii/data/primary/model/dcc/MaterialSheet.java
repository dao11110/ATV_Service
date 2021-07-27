package com.foxconn.fii.data.primary.model.dcc;

import lombok.Data;

import java.util.Map;

@Data
public class MaterialSheet extends Sheet{
    private String mfgNo;
    private String qty;
    private String customerNo;
    private String mfgName;
    private String dc;
    private String materialName;
    private String isChargeVendor;
    private String isIpqNo;
    private String isBuyPart;
    private String isMrb;

    public MaterialSheet(Map<String, Object> mMaterialSheet){
        super(mMaterialSheet);
        this.mfgNo = (String) mMaterialSheet.get("mfg_no");
        this.qty = (String) mMaterialSheet.get("qty");
        this.customerNo = (String) mMaterialSheet.get("customer_no");
        this.mfgName = (String) mMaterialSheet.get("mfg_name");
        this.dc = (String) mMaterialSheet.get("dc");
        this.materialName = (String) mMaterialSheet.get("material_name");
        this.isChargeVendor = (String) mMaterialSheet.get("is_charge_vendor");
        this.isIpqNo = (String) mMaterialSheet.get("is_ipq_no_avl_hwt");
        this.isBuyPart = (String) mMaterialSheet.get("is_buy_part");
        this.isMrb = (String) mMaterialSheet.get("is_mrb");
    }
}
