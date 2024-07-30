package com.amkor.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AutoLabelModel {
    private int factoryId;
    private int siteId;
    private String businessType;
    private int customerId;
    private String pkg;
    private String dim;
    private String lead;
    private String targetDevice;
    private String keyField1;
    private String keyField2;
    private String fieldName;
    private String fieldValue;
    private int userBadge;

    @Override
    public String toString() {
        return "{" +
                "factoryId=" + factoryId +
                ", siteId=" + siteId +
                ", businessType='" + businessType + '\'' +
                ", customerId=" + customerId +
                ", pkg='" + pkg + '\'' +
                ", dim='" + dim + '\'' +
                ", lead='" + lead + '\'' +
                ", targetDevice='" + targetDevice + '\'' +
                ", keyField1='" + keyField1 + '\'' +
                ", keyField2='" + keyField2 + '\'' +
                ", fieldName='" + fieldName + '\'' +
                ", fieldValue='" + fieldValue + '\'' +
                ", userBadge=" + userBadge +
                '}';
    }
}
