package com.chinatower.platform.modules;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 属地告警同步失败明细
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlarmSyncFailItem {

    /**
     * 属地编码
     */
    private String platformCode;

    /**
     * 拉取日期 yyyyMMdd
     */
    private String pullDate;

    /**
     * 同步类型：1-告警工单，2-运营研判，3-回收站
     */
    private Integer syncType;

    /**
     * 异常信息
     */
    private String errorMsg;
}
