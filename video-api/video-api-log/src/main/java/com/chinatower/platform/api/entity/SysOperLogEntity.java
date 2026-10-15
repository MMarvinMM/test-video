package com.chinatower.platform.api.entity;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.annotation.JSONField;
import com.chinatower.common.annotation.Excel;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * 操作日志记录表 oper_log
 *
 * @author chinatower
 */
@Data
public class SysOperLogEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 客户id */
    @Excel(name = "客户ID")
    private String tenantId;

    /** 客户名称 */
    @Excel(name = "客户名称")
    private String tenantName;

    @Excel(name = "账号类型",readConverterExp = "0=铁塔,2=客户")
    private String userType;

    /** 部门名称 */
    @Excel(name = "操作用户组织")
    private String deptName;

    /** 日志主键 */
    private String operId;

    @Excel(name = "客户端")
    private String clientName;

    /** 操作模块 */
    @Excel(name = "操作模块")
    private String title;


    @Excel(name = "操作类型")
    private String operateTypeDesc;

    /** 操作类型 */
    //@Excel(name = "操作类型",readConverterExp = "0=其他,1=查询,2=新增,3=修改,4=删除,5=授权,6=导出,7=导入,8=强退,9=生成代码,10=清空数据,11=异常")
    private Integer businessType;

    /** 请求url */
    private String operUrl;

    /** 请求参数 */
    @Excel(name = "操作内容")
    private String operParam;

    @Excel(name = "操作对象")
    private String operateDesc;

    /** 操作地址 */
    @Excel(name = "操作地址")
    private String operIp;

    /** 操作状态（0正常 1异常） */
    @Excel(name = "操作状态",readConverterExp = "0=成功,1=失败")
    private Integer status;

    /** 操作人员 */
    @Excel(name = "操作人")
    private String operName;

    @Excel(name = "操作时间",dateFormat = "yyyy-MM-dd HH:mm:ss")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date operTime;

    /** 请求方法 */
    private String method;

    /** 请求方式 */
    private String requestMethod;

    /** 操作类别（0其它 1后台用户 2手机端用户） */
    private Integer operatorType;



    /** 操作地点 */
    private String operLocation;

    /** 返回参数 */
    private String jsonResult;


    /** 错误消息 */
    private String errorMsg;

    private String operMonth;

    private String traceId;



    public String getOperMonth() {
        return StrUtil.isNotBlank(operMonth)?operMonth: DateUtil.format(new Date(),"yyyyMM");
    }

}
