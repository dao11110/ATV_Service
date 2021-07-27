package com.foxconn.fii.data.primary.model.dcc;

import lombok.Data;

import java.util.Map;

@Data
public class ProductSheet extends Sheet{
    public ProductSheet(Map<String, Object> mProductSheet){
        super(mProductSheet);
    }
}
