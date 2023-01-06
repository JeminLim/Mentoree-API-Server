package com.mentoree.api.advice;

import com.mentoree.api.advice.response.ErrorCode;
import com.mentoree.api.advice.response.ErrorResponse;
import com.mentoree.exception.DuplicateDataException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ControllerAdvice {
    @ExceptionHandler(DuplicateDataException.class)
    protected ResponseEntity<ErrorResponse> handleDuplicateExistException(DuplicateDataException e) {
        log.error("[DuplicateExistException] Duplicate occur : {}", e.getEntityClass().getSimpleName());
        log.error("[DuplicateExistException] error : {}", e.getMessage());
        final ErrorResponse response = ErrorResponse.of(ErrorCode.ILLEGAL_PARAMS, e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalStateException.class)
    protected ResponseEntity<ErrorResponse> handleIllegalStateException(IllegalStateException e) {
        log.error("[IllegalStateException] error : {}", e.getMessage());
        final ErrorResponse response = ErrorResponse.of(ErrorCode.ILLEGAL_STATEMENT, e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
