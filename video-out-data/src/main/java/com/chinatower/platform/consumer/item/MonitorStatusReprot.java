package com.chinatower.platform.consumer.item;

import com.chinatower.mongodb.dto.BaseMongoDTO;
import lombok.Data;

/**
 * 传感器监测因子状态
 * Created by zhujun on 2022/3/31.
 */
@Data
public class MonitorStatusReprot extends BaseMongoDTO {

    // 监测因子中文描述
    private String monitorDiscription;

    // 监测因子名称
    private String monitorName;

    // 监测因子状态 有效状态：0-无效；1-有效
    private int status;

    // 监测时间 格式yyyy-MM-dd HH:mm:ss
    private String monitorTime;

    //设备编码
    private String deviceCode;

    //删除表示
    private String deleteFlag;

    //canal类型
    private String canalType;
}
