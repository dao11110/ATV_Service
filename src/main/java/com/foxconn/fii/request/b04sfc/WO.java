package com.foxconn.fii.request.b04sfc;

import com.foxconn.fii.data.primary.model.RSmtFaiConfig;
import lombok.Data;

@Data
public class WO {
    private String wo;
    private String build;
    private String lineName;

    public WO(){
        this.wo = "";
        this.build = "";
        this.lineName = "";
    }

    public WO(String mWo, String mFactory){
        this.wo = mWo;
        this.build = mFactory;
        this.lineName = "";
    }

    public WO(RSmtFaiConfig mData, String build){
        this.wo = mData.getWo();
        this.lineName = mData.getStation();
        this.build = build;
    }

    public String toString(){
        return "{\"wo\": \""+this.wo+"\",\n" +
                "    \"linename\": \""+this.lineName+"\",\n" +
                "    \"build\": \""+this.build+"\"}";
    }
}
