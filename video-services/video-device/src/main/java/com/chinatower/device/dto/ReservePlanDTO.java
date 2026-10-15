package com.chinatower.device.dto;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @ClassName ReservePlanDTO
 * @Deacription 视频预案实体
 * @Author chenmf
 * @Date 2022/4/18 17:47
 * @Version 1.0
 **/
@Data
public class ReservePlanDTO extends BaseDataScopeEntity implements Serializable {
    
    //预案id
    private String planId;
    
    //预案名称
    private String planName;
    
    //预案类型：1公有预案；2私有预案
    private String planType;
    
    //轮巡间隔:单位秒（s）
    private String patrolInterval;
    //是否自动执行预案
    private String ifAutoExecute;
    
    //执行开始时间(格式：HH:mm:ss）
    private String planStartTime;
    //执行结束时间(格式：HH:mm:ss）
    private String planEndTime;
    
    //用户编码
    private String userCode;
    
    //租户id
    private String tenantId;

    //租户id
    private String industryCode;
    
    private List<ReservePlanChannelDTO> planChannelList;
    
    private String creatorDate;

    private String creatorDateStart;

    private String creatorDateEnd;
    
    private String modifierDate;

    private String modifierDateStart;

    private String modifierDateEnd;
    
    private String creator;
    
    private String modifier;

    /**
     * 当前页
     * @required true
     */
    protected Integer pageNum;

    /**
     * 每页条数
     * @required true
     */
    protected Integer pageSize;
}
