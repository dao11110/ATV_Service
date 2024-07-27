package com.amkor.common.response;

import lombok.Value;
import org.springframework.http.HttpStatus;

import java.util.List;

@Value(staticConstructor = "of")
public class ListResponse<T> {

    private HttpStatus status;

    private ResponseCode code;

    private String message;

    private List<T> data;

    private int size;

    public static <T> ListResponse<T> of (HttpStatus status, ResponseCode code, String message, List<T> data) {
        return ListResponse.of(status, code, message, data, data.size());
    }
}
