package com.foxconn.fii.request.agile;

import lombok.Data;

@Data
public class Bom {
    private String itemNumber;
    private String effectiveDate;
    private String bomDepth;
    private String username;
    private String password;

    public Bom(String mItemNumber){
        this.itemNumber = mItemNumber;
    }

    @Override
    public String toString() {
        return "{\n" +
                " \"itemnumber\":\""+this.itemNumber+"\",\n" +
                " \"site\":\"vn\",\n" +
                " \"bomdepth\": \"1\",\n" +
                " \"username\":\"V0959579\",\n" +
                " \"password\":\"722crlhUZ~j!\"\n" +
                "}";
    }
}
