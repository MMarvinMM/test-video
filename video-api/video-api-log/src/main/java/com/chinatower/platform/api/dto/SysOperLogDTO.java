package com.chinatower.platform.api.dto;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.Date;

/**
 * 操作日志记录表 oper_log
 *
 * @author chinatower
 */
@Data
public class SysOperLogDTO  implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 日志主键 */
    private Long operId;

    /** 操作模块 */
    private String title;

    /** 业务类型（0其它 1新增 2修改 3删除） */
    private Integer businessType;

    /** 用户类型 */
    private String userType;

    /** 业务类型,多个业务类型 */
    private String businessTypes;

    /** 操作人员 */
    private String operName;

    /** 请求参数 */
    private String operParam;

    /** 操作状态（0正常 1异常） */
    private Integer status;

    /** 操作月份 */
    private String operMonth;

    /** 开始时间 */
    /** 操作时间 JsonField 格式化前端日期*/
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private Date beginTime;

    /** 结束时间 */
    /** 操作时间 JsonField 格式化前端日期*/
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private Date endTime;

    /** 当前页码 */
    private Integer pageNum;

    /** 页面数量 */
    private Integer pageSize;

    private String orderByColumn;

    @Pattern(regexp = "asc|desc",message = "排序规则不合法")
    private String isAsc;

    private String operateTypeDesc;

    /** 客户id */
    private String tenantId;

    /** 客户名称 */
    private String tenantName;

    /**
     * 客户名称/ID
     */
    private String tenantIdOrName;

}
