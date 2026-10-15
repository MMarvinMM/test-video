package com.chinatower.device.dataobject;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;


@Data
@TableName("vm_radar_alarm_black_white_list")
public class RadarAlarmBlackWhiteListDO implements Serializable {

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private String id;

    /**
     * 告警模板编码
     */
    private String templateId;

    /**
     * 船名
     */
    private String shipName;

    /**
     * mmis
     */
    private String mmsi;

    /**
     * 名单类型 1-白名单 2-黑名单
     */
    private Integer listType;

    /**
     * 客户编码
     */
    private Date tenantId;

    /**
     * 行业编码
     */
    private Date industryCode;

    /**
     * 创建者
     */
    private String creator;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private String createTime;
}
