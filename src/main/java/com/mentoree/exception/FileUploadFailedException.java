package com.mentoree.exception;

import com.mentoree.api.advice.response.ErrorCode;
import lombok.Getter;

@Getter
public class FileUploadFailedException extends RuntimeException {

    ErrorCode errorCode;

    public FileUploadFailedException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public FileUploadFailedException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public FileUploadFailedException(String message, Throwable cause, ErrorCode errorCode) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public FileUploadFailedException(Throwable cause, ErrorCode errorCode) {
        super(cause);
        this.errorCode = errorCode;
    }

    public FileUploadFailedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, ErrorCode errorCode) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.errorCode = errorCode;
    }
}
