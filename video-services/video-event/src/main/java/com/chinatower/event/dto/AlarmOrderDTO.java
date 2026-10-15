package com.chinatower.event.dto;

import com.chinatower.event.entity.dto.AlarmLableRelDTO;
import com.chinatower.event.entity.dto.BaseAlarmOrderDTO;
import com.chinatower.platform.entity.admin.SysUser;
import lombok.Data;

import java.util.List;

/**
 * Copyright: Copyright(c) 2019 iwhalecloud
 * <p>
 * 类说明：TODO
 * <p>
 * 类名称: AlarmOrderDTO.java
 *
 * @author su.tingning
 * @version v1.0.0
 * @date 2022/1/21 09:34
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ------------------------------------------------------------
 */
@Data
public class AlarmOrderDTO extends BaseAlarmOrderDTO {

    private String[] orderIds;

    /**
     * 是否开启权限调用（1：开启 空：不开启）
     */
    private String powerFlag;

    private String processId;

    /**
     * 是否指定选择人
     */
    private String beforeDispatchFlag;

    /**
     * 加密deOrderId
     */
    private String deOrderId;

    private String timeLimit;

    /**
     * 预置位ID List
     */
    private List<String> presetIdList;

    /**
     * 预置位id
     */
    private String presetId;


    /**
     * 用户经度
     */
    private String userLongitude;
    /**
     * 用户维度
     */
    private String userLatitude;

    /**
     * 排序方式（1：事发点距离 2：告警时间）
     */
    private String sortFlag;

    private SysUser sysUser;

    /**
     * 小程序排序标识
     */
    private String applet;

    /**
     * 告警类型模糊查询关键字
     */
    private String warningTypeNameKeyWord;

    /**
     * 告警点关键字
     */
    private String addressKeyWord;

}
