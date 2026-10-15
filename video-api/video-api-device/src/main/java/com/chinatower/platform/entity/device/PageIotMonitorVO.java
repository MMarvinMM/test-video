package com.chinatower.platform.entity.device;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class PageIotMonitorVO implements Serializable {

    /**
     * 设备编码
     * @required true
     */
    private String deviceCode;

    /**
     * 物联因子信息
     * @required true
     */
    private List<PageIotMonitorSubVO> monitorInfoList;
}
