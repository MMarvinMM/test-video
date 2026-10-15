//package com.chinatower.common.core.exception.handler;
//
//import com.chinatower.common.core.exception.CustomException;
//import com.chinatower.common.entity.Result;
//import com.sun.org.apache.xml.internal.utils.DefaultErrorHandler;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.boot.context.properties.bind.BindException;
//import org.springframework.web.bind.annotation.ControllerAdvice;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.bind.annotation.ResponseBody;
//
//import java.sql.SQLSyntaxErrorException;
//
//
//@ControllerAdvice
//@Slf4j
//public class GlobalExceptionHandler {
//
//
//    /**
//     * 处理未知异常
//     * @return
//     */
//    @ExceptionHandler({Exception.class})
//    @ResponseBody
//    public Result defaultException(Exception e){
//        log.error("未知异常",e);
//        return Result.fail("系统繁忙！");
//    }
//
//    @ExceptionHandler({SQLSyntaxErrorException.class})
//    @ResponseBody
//    public Result sqlSyntaxErrorException(SQLSyntaxErrorException e){
//        log.error("未知异常",e);
//        return Result.fail("未知异常！");
//    }
//
//    /**
//     * 处理自定义异常
//     * @param ex
//     * @return
//     */
//    @ExceptionHandler({CustomException.class})
//    @ResponseBody
//    public Result customException(CustomException ex){
//
//        return Result.fail(ex.getCode(),ex.getMessage());
//    }
//
//    /**
//     * 处理自定义异常
//     * @param ex
//     * @return
//     */
//    @ExceptionHandler({BindException.class})
//    @ResponseBody
//    public Result bindException(BindException ex){
//
//        return Result.fail(ex.getMessage());
//    }
//}
