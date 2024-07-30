package com.amkor.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProcessNoteModel {
    private int factoryId;
    private int customerId;
    private String classify;
    private String pkg;
    private String dim;
    private String lead;
    private String targetDevice;
    private int operation;
    private String optionId;
    private String engNote;
    private int seq;
    private String userBadge;

    @Override
    public String toString() {
        return "{" +
                "factoryId=" + factoryId +
                ", customerId=" + customerId +
                ", classify='" + classify + '\'' +
                ", pkg='" + pkg + '\'' +
                ", dim='" + dim + '\'' +
                ", lead='" + lead + '\'' +
                ", targetDevice='" + targetDevice + '\'' +
                ", operation=" + operation +
                ", optionId='" + optionId + '\'' +
                ", engNote='" + engNote + '\'' +
                ", seq=" + seq +
                ", userBadge='" + userBadge + '\'' +
                '}';
    }
}
