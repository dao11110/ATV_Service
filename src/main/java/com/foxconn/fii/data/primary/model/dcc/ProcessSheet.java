package com.foxconn.fii.data.primary.model.dcc;

import lombok.Data;

import java.util.Map;

@Data
public class ProcessSheet extends Sheet{
    private String processAfter;
    private String processBefore;

    public ProcessSheet(Map<String, Object> mProcessShet){
        super(mProcessShet);
        this.processAfter = (String) mProcessShet.get("process_after");
        this.processBefore = (String) mProcessShet.get("process_before");
    }
}
