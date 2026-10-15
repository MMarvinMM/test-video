package com.chinatower.device.config;

import com.chinatower.common.core.exception.CustomException;
import com.chinatower.common.entity.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author yy
 */
@RestControllerAdvice
@Slf4j
public class CustomExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public Result<T> handleGlobalException(Exception e){
        log.error(e.getMessage());
        e.printStackTrace();
        return Result.fail(400, e.getMessage());
    }

}
