package com.chinatower.platform.dto;

import com.alibaba.fastjson.annotation.JSONField;
import com.chinatower.common.core.utils.DateUtils;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.mongodb.dto.BaseMongoDTO;
import com.chinatower.mongodb.request.QueryEntity;
import com.chinatower.mongodb.request.SortEntity;
import com.chinatower.platform.entity.SysOperLogEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 操作日志记录表 oper_log
 *
 * @author chinatower
 */
@Data
public class SysOperLogDTO extends BaseMongoDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 日志主键 */
    private String operId;

    /** 操作模块 */
    private String title;

    /** 业务类型（0其它 1新增 2修改 3删除） */
    private Integer businessType;

    private String userType;

    /** 业务类型,多个业务类型 */
    private String businessTypes;

    /** 操作人员 */
    private String operName;

    /** 请求参数 */
    private String operParam;

    /** 操作月份 */
    private String operMonth;

    /** 操作状态（0正常 1异常） */
    private Integer status;

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

    /**
     * 客户名称/ID
     */
    private String tenantIdOrName;


    public QueryEntity getQueryEntity(){
        QueryEntity query = new QueryEntity(SysOperLogEntity.class);
        if(StringUtils.isNotBlank(operId)){
            query.eq("_id",operId);
        }
        if(StringUtils.isNotBlank(title)){
            query.regex("title","^.*"+title.trim()+".*$");
        }
        if(businessType != null){
            query.eq("businessType",businessType);
        }
        if(status != null){
            query.eq("status",status);
        }
        if(StringUtils.isNotBlank(operMonth)){
            query.eq("operMonth",operMonth);
        }
        if(StringUtils.isNotBlank(businessTypes)){
            String[] split = businessTypes.split(",");
            Integer[] nums= Arrays.asList(split).stream().mapToInt(Integer::parseInt).boxed().toArray(Integer[]::new);
            query.in("businessType",nums);
        }
        if(StringUtils.isNotBlank(operName)){
            query.regex("operName","^.*"+operName.trim()+".*$");
        }
        if(StringUtils.isNotBlank(operParam)){
            query.regex("operParam","^.*"+operParam.trim()+".*$");
        }
        if(beginTime != null && endTime != null){
            query.between("operTime",  DateUtils.parseDate(DateUtils.parseDateToStr("yyyy-MM-dd 00:00:00",beginTime)),DateUtils.parseDate(DateUtils.parseDateToStr("yyyy-MM-dd 23:59:59",endTime)));
        }
        if(StringUtils.isNotBlank(userType)){
            query.eq("userType",userType);
        }
        if(StringUtils.isNotBlank(operateTypeDesc)){
            query.regex("operateTypeDesc","^.*"+operateTypeDesc.trim()+".*$");
        }
        //add by liuxt 20231115
        if(StringUtils.isNotBlank(tenantIdOrName)){
            query.eq("tenantId",tenantIdOrName);
        }
        return query;
    }

    public SortEntity getSortEntity(){
        SortEntity sort = new SortEntity();
        if(StringUtils.isNotBlank(orderByColumn)){
            if(StringUtils.isNotBlank(isAsc)){
                if(isAsc.equalsIgnoreCase("desc")){
                    sort.desc(orderByColumn);
                }else{
                    sort.asc(orderByColumn);
                }
            }else{
                sort.desc(orderByColumn);
            }
        }else{
            sort.desc("operId");
        }
        return sort;
    }
}
