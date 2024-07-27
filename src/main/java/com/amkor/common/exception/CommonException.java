package com.amkor.common.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommonException extends RuntimeException {

    private String message;

    public CommonException(String message) {
        super();
        this.message = message;
    }
}
