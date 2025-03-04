package com.amkor.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DateCodeDiscrepancyModel {
    public String wipAmkorId = "";
    public String wipAmkorSubId = "";
    public String lotName = "";
    public String lotDcc = "";
    public String mesDateCode = "";
    public String minDateCode = "";
}
