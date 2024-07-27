package com.amkor.common.response;

import lombok.Value;
import org.springframework.http.HttpStatus;

@Value(staticConstructor = "of")
public class CommonResponse<T> {

    private HttpStatus status;

    private ResponseCode code;

    private String message;

    private T result;

}
