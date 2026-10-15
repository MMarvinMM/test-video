package com.chinatower.platform.project.monitor.domain;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.annotation.JSONField;
import com.chinatower.common.core.annotation.Excel;
import com.chinatower.mongodb.dto.BaseMongoDTO;
import com.chinatower.mongodb.request.SortEntity;
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
@Document("log_operlog")
public class SysOperLog extends BaseMongoDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private static final String TABLE_NAME = "LOG_SysOperLog";

    /** 日志主键 */
    @Id
    @Excel(name = "操作编号")
    private String operId;

    /** 操作模块 */
    @Excel(name = "操作模块")
    private String title;

    /** 业务类型（0其它 1新增 2修改 3删除） */
    @Excel(name = "业务类型",readConverterExp = "0=其它,1=新增,2=修改,3=删除")
    private Integer businessType;

    /** 请求方法 */
    @Excel(name = "请求方法")
    private String method;

    /** 请求方式 */
    @Excel(name = "请求方式")
    private String requestMethod;

    /** 操作类别（0其它 1后台用户 2手机端用户） */
    @Excel(name = "操作类别", readConverterExp = "0=其它,1=后台用户,2=手机端用户")
    private Integer operatorType;

    /** 操作人员 */
    @Excel(name = "操作人员")
    private String operName;

    /** 部门名称 */
    @Excel(name = "部门名称")
    private String deptName;

    /** 请求url */
    @Excel(name = "请求url")
    private String operUrl;

    /** 操作地址 */
    @Excel(name = "操作地址")
    private String operIp;

    /** 操作地点 */
    @Excel(name = "操作地点")
    private String operLocation;

    /** 请求参数 */
    @Excel(name = "请求参数")
    private String operParam;

    /** 返回参数 */
    @Excel(name = "返回参数")
    private String jsonResult;

    /** 操作状态（0正常 1异常） */
    @Excel(name = "操作状态", readConverterExp = "0=正常,1=异常")
    private Integer status;

    /** 错误消息 */
    @Excel(name = "errorMsg")
    private String errorMsg;

    /** 操作时间 JsonField 格式化前端日期*/
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "操作时间", dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date operTime;

    @Excel(name = "操作月份")
    private String operMonth;

    @Excel(name = "消息ID")
    private String traceId;

    public String getOperMonth() {
        return StrUtil.isNotBlank(operMonth)?operMonth: DateUtil.format(new Date(),"yyyyMM");
    }

    @Override
    public String getCollectionName() {
        if(this.collectionName == null){
            this.collectionName = TABLE_NAME;
        }
        return this.collectionName;
    }

    public SortEntity getSortEntity(){
        SortEntity sort = new SortEntity();
        sort.desc("operTime");
        return sort;
    }
}
