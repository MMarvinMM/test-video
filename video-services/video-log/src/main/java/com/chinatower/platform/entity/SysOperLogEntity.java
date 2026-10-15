package com.chinatower.platform.entity;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.annotation.JSONField;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.core.utils.bean.BeanUtils;
import com.chinatower.common.entity.BaseEntity;
import com.chinatower.mongodb.dto.BaseMongoDTO;
import com.chinatower.mongodb.request.QueryEntity;
import com.chinatower.mongodb.request.SortEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * 操作日志记录表 oper_log
 *
 * @author chinatower
 */
@Data
@Document("log_sysOper")
public class SysOperLogEntity extends BaseMongoDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 日志主键 */
    @Id
    private String operId;

    /** 操作模块 */
    private String title;

    /** 用户类型 */
    private String userType;

    /** 业务类型（0其它 1新增 2修改 3删除） */
    private Integer businessType;

    /** 请求方法 */
    private String method;

    /** 请求方式 */
    private String requestMethod;

    /** 操作类别（0其它 1后台用户 2手机端用户） */
    private Integer operatorType;

    /** 操作人员 */
    private String operName;

    /** 部门名称 */
    private String deptName;

    /** 请求url */
    private String operUrl;

    /** 操作地址 */
    private String operIp;

    /** 操作地点 */
    private String operLocation;

    /** 请求参数 */
    private String operParam;

    /** 返回参数 */
    private String jsonResult;

    /** 操作状态（0正常 1异常） */
    private Integer status;

    /** 错误消息 */
    private String errorMsg;

    /** 操作时间 JsonField 格式化前端日期*/
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date operTime;

    private String operMonth;

    private String traceId;

    /** 客户id */
    private String tenantId;

    /** 客户名称 */
    private String tenantName;

    @ApiModelProperty(
            name = "操作描述"
    )
    private String operateDesc;

    @ApiModelProperty(
            name = "操作类型描述"
    )
    private String operateTypeDesc;

    private String clientName;

    public String getOperMonth() {
        return StrUtil.isNotBlank(operMonth)?operMonth: DateUtil.format(new Date(),"yyyyMM");
    }


    public SortEntity getSortEntity(){
        SortEntity sort = new SortEntity();
        sort.desc("operTime");
        return sort;
    }
}
