package com.chinatower.platform.project.custom.entity;

import com.chinatower.common.annotation.Excel;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.ToString;

import java.util.Date;
import java.util.List;

/**
 * @Author：liuxt
 * @Description：apiVO(对外)
 * @Date：2022/8/5 11:34
 * @requestID  中国铁塔_铁塔视联平台__运营管理-客户管理_需求
 */
@Data
@ToString
public class ApiVO {

    //操作标识 ： 1 订购 2 退订
    private String operateFlag;

    //客户编码
    private String customerCode;

    //是否开放api 0:否  1:是
    private String isOpen;

}