package com.foxconn.fii.common.response;

import lombok.Value;
import org.springframework.http.HttpStatus;

import java.util.Map;

@Value(staticConstructor = "of")
public class MapResponse<T> {

    private HttpStatus status;

    private ResponseCode code;

    private String message;

    private Map<String, T> data;

    private int size;

    public static <T> MapResponse<T> of(HttpStatus status, ResponseCode code, String message, Map<String, T> data) {
        return MapResponse.of(status, code, message, data, data.size());
    }
}
