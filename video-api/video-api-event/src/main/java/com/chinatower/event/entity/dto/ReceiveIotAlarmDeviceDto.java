package com.chinatower.event.entity.dto;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data

public class ReceiveIotAlarmDeviceDto implements Serializable {

    /**
     * 物联告警配置id
     */
    private String groupId;

    /**
     * 物联网设备编码
     */
    private String deviceCode;


}
