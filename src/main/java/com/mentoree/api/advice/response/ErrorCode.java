package com.mentoree.api.advice.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ErrorCode {

    /**
     * U - User 관련 코드 (auth, token)
     * D - Data 관련 코드 (DataBinding, Param)
     * F - File 관련 코드 (FileUpload)
     */
    BAD_CREDENTIALS(401, "U001", " Bad Credentials"),
    INVALID_TOKEN(401, "U002", " Invalid token was used"),
    NO_AUTHORITY(401, "U003", " non-authority user access"),
    EXPIRED_TOKEN(401, "U004", " Expired token was used"),
    ACCESS_DENIED(401, "U000", "Access denied"),
    ILLEGAL_PARAMS(400, "D001", " Illegal argument binding"),
    ILLEGAL_STATEMENT(400, "D002", " Entity cannot perform method due to statement"),
    FILE_UPLOAD_FAILED(400, "F001", " File upload failed"),
    ;

    private final String code;
    private final String message;
    private int status;

    ErrorCode(final int status, final String code, final String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
