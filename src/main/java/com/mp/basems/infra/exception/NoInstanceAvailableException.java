package com.mp.basems.infra.exception;

import org.springframework.http.HttpStatus;

public class NoInstanceAvailableException extends RuntimeException {

    private static final long serialVersionUID = -1982160847906933785L;
    private String errorMessage;
    private HttpStatus httpStatus;

    public NoInstanceAvailableException(String errorMessage) {
        this.httpStatus = HttpStatus.BAD_REQUEST;
        this.errorMessage = errorMessage;
    }

    public NoInstanceAvailableException(HttpStatus httpStatus, String errorMessage) {
        this.httpStatus = httpStatus;
        this.errorMessage = errorMessage;
    }

    public HttpStatus getStatus() {
        return this.httpStatus;
    }

    public String getMessage() {
        return this.errorMessage;
    }

}
