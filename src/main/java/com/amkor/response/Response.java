package com.amkor.response;

import lombok.Data;

@Data
public class Response {
    private Integer status;
    private String message;
    private Integer total;
    private Object data;

    public Response(){
        this.status = 0;
        this.message = "Not found(404) !!!";
        this.total = 0;
        this.data = null;
    }

    public Response(int mStatus, String mMessage, Object mData, int mTotal){
        this.status = mStatus;
        this.message = mMessage;
        this.data = mData;
        this.total = mTotal;
    }
}
