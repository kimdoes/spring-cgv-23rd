package com.ceos23.spring_cgv_23rd.global.Exception;

import com.ceos23.spring_cgv_23rd.global.DTO.ErrDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrDTO> handleCustomException(CustomException e) {
        ErrorCode code = e.getCode();

        return ResponseEntity
                .status(code.getStatus())
                .body(ErrDTO.create(code));
    }
}
