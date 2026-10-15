package com.chinatower.video.component;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.chinatower.common.entity.Result;

import lombok.extern.slf4j.Slf4j;

/**
 * @ClassName ExceptionHandler
 * @Description 全局异常处理器
 * @Author wanglei
 * @Date 2023/6/23 23:15
 */
@RestControllerAdvice
@Slf4j
public class CustomExceptionHandler {
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
    public Result handleValidationException(MethodArgumentNotValidException e) {
        log.error("数据验证异常:", e);
        return Result.fail(e.getBindingResult().getFieldError().getDefaultMessage());
    }
}
