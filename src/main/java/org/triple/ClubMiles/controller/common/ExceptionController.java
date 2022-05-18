package org.triple.ClubMiles.controller.common;

import com.fasterxml.jackson.core.JsonParseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.triple.ClubMiles.exception.CustomException;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Enumeration;

@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class ExceptionController {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<Response> handleCustomException(final CustomException ex, HttpServletRequest req) {
        log.error("CustomException occurs.=[{}],[{}]", req.getRequestURI(), ex.getMessage());
        return ResponseEntity.badRequest()
                .body(Response.ERROR_WITH(ex.getCode(), ex.getMessage()));
    }

    @ExceptionHandler(JsonParseException.class)
    protected ResponseEntity<Response> handleJsonParseException(JsonParseException ex, HttpServletRequest req) {
        log.error("JsonParseException occurs.=[{}],[{}]", req.getRequestURI(), ex.getMessage());
        return ResponseEntity.badRequest()
                .body(Response.ERROR("JSON Parsing 에러. [" + ex.getMessage() + "]"));
    }

    //그외 모든 에러
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<Response> handleGeneralException(Exception ex, HttpServletRequest req) {
        log.error("General Exception occurs.=[{}],[{}]", req.getRequestURI(), ex.getMessage());
        return ResponseEntity.internalServerError()
                .body(Response.ERROR("서버 에러입니다. [" + ex.getMessage() + "]"));
    }
}