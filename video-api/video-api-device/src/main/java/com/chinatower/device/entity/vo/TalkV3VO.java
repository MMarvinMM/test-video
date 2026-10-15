package com.chinatower.device.entity.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 创建口播V3返回
 * @author lc
 */
@Data
public class TalkV3VO implements Serializable {
    /**
     * 播放列表
     */
    private List<TalkV2VO> liveList;

    /**
     * 工作中广播设备名称列表
     */
    private List<String> isWorkingDeviceNameList;
}
