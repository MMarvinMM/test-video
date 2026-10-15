package com.chinatower.event.entity.vo;

import lombok.Data;
/**
 * @Auther: luo.zhenghong
 * @Date: 2022-7-25
 * @Description:
 * 物联设备告警接收请求实体
 */
@Data
public class IotAlarmSuppressVO {
    private static final long serialVersionUID = 1L;
    /**
     * 物联配置id
     */
    private String configId;

    /**
     * 告警压制时间
     */
    private Long suppressTime;
}
