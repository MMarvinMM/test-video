package com.chinatower.video.api.entity;

import lombok.Data;
import org.apache.ibatis.type.Alias;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @ClassName DeleteChannelVideoPlanVO
 * @Deacription 删除路学计划对象
 * @Author chenmf
 * @Date 2022/1/27 10:40
 * @Version 1.0
 **/
@Data
@Alias("DeleteChannelVideoPlanVOVideo")
public class DeleteChannelVideoPlanVO implements Serializable {
    
    /**
     *录像计划id
     * @required true
     */
    @NotNull(message = "录像计划id不能为空")
    private Long recordPlanId;

    /**
     *设备编号
     * @required true
     */
    @NotNull(message = "设备编号不能为空")
    private String deviceCode;

    /**
     *通道编号
     * @required true
     */
    @NotNull(message = "通道编号不能为空")
    private String channelCode;

    /**
     *客户唯一编码
     * @required true
     */
    @NotNull(message = "客户编码不能为空")
    private String tenantId;
    

    /**
     *用户编码
     * @required true
     */
    private String userCode;

    /**
     *订购关系编码
     * @required true
     */
    private String orderCode;
    private String industryCode;

}
