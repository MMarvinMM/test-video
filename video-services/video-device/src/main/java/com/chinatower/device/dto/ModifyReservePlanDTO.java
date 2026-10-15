package com.chinatower.device.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

/**
 * @ClassName: ReservePlanVO
 * @Description: 视频预案实体
 * @Author: zhaozj
 * @Date: 2022/4/6 16:07
 **/
@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ModifyReservePlanDTO implements Serializable {
    
    /**
     * 预案id
     */
    @NotNull(message = "预案id不能为空")
    @ApiModelProperty(value = "预案id", required = true)
    private String planId;
    
    //预案名称
    @NotNull(message = "预案名称不能为空")
    @ApiModelProperty(value = "预案名称", required = true)
    private String planName;

    //预案类型：1公有预案；2私有预案
    @NotNull(message = "预案类型不能为空")
    @ApiModelProperty(value = "预案类型", required = true)
    private String planType;

    //轮巡间隔:单位秒（s）
    @ApiModelProperty(value = "轮巡间隔:单位秒（s）")
    private String patrolInterval;
    
    //是否自动执行预案
    @ApiModelProperty(value = "是否自动执行预案")
    private String ifAutoExecute;

    //执行开始时间(格式：HH:mm:ss）
    @ApiModelProperty(value = "执行开始时间(格式：HH:mm:ss）")
    private String planStartTime;
    
    //执行结束时间(格式：HH:mm:ss）
    @ApiModelProperty(value = "执行结束时间(格式：HH:mm:ss）")
    private String planEndTime;

    @Size(max=200,message ="设备数量不可超过200个!" )
    @ApiModelProperty(value = "预案通道列表")
    private List<ReservePlanChannelDTO> planChannelList;

}
