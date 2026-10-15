package com.chinatower.video.api.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.ibatis.type.Alias;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;

/**
 * @ClassName BatchDeleteChannelVideoPlanVO
 * @Deacription 批量删除路学计划对象
 * @Author chenmf
 * @Date 2022/1/27 10:40
 * @Version 1.0
 **/
@Data
@Alias("BatchDeleteChannelVideoPlanVOVideo")
public class BatchDeleteChannelVideoPlanVO implements Serializable {

    /**
     *录像计划ids
     * @required true
     */
    @NotEmpty(message = "录像计划id列表不能为空")
    private List<Long> recordPlanIdList;

    /**
     *设备编号
     *
     */
    private String deviceCode;


    /**
     *通道编号
     *
     */
    private String channelCode;

    /**
     *客户唯一编码
     *
     */
    private String tenantId;

    /**
     *用户编码
     *
     */
    private String userCode;
    

    /**
     *订购关系编码
     *
     */
    private String orderCode;

    /**
     *行业编码
     *
     */
    private String industryCode;
}
