package org.ctc.config;


import io.jsonwebtoken.ExpiredJwtException;
import org.ctc.dto.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import static org.ctc.costant.Constance.JWT_EXPIRED;

@ControllerAdvice
public class ExceptionHandle {

    private Logger log = LoggerFactory.getLogger(ExceptionHandle.class);

     //設定一個錯誤處理機

    @ExceptionHandler
    public ResponseEntity<String> expiredJwtException(ExpiredJwtException expiredJwtException){

        log.info("ExpiredJwtException{}",expiredJwtException);

        return new ResponseEntity( new Result(JWT_EXPIRED, "登入逾期，請重新登入"), HttpStatus.INTERNAL_SERVER_ERROR);
    }




}
