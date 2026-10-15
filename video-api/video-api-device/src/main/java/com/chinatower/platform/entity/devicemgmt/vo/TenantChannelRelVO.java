package com.chinatower.platform.entity.devicemgmt.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author chmod
 * @version 1.0
 * @date 2021/12/6 15:52
 */
@Data
public class TenantChannelRelVO implements Serializable {
    private static final long serialVersionUID = -6070839171625641513L;

    private Long id;
    private String deviceCode;	//设备编码
    private String tenantId; //租户ID
    private String channelCode; //通道编码
    private String channelName; //通道名称
    private Date creatorDate; //创建日期
    private Date modifierDate; //更新日期
    private String deleteFlag; //有效标志位: 1-正常 0-删除

}
