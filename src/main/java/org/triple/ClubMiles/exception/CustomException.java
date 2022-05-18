package org.triple.ClubMiles.exception;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {

    private String message = "";
    private String code = "";

    public CustomException() {
    }

    public CustomException(String message) {
        super(message);
        this.code = "ERROR";
        this.message = message;
    }

    public CustomException(String code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public CustomException(String message, Throwable cause) {
        super(message, cause);
    }

    public CustomException(Throwable cause) {
        super(cause);
    }

    public CustomException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

