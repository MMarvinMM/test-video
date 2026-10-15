package com.chinatower.platform.entity.device;


import com.chinatower.common.core.entity.BaseDataScopeEntity;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class PageIotMonitorDTO extends BaseDataScopeEntity implements Serializable {


    /**
     * 告警因子编码
     * @required false
     */
    private String monitorCode;

    /**
     * 告警类型编码
     * @required false
     */
    private String alarmType;

    /**
     * 设备编码列表
     * @required true
     */
    private List<String> deviceCodeList;

    /**
     * 当前页
     * @required true
     */
    private Integer pageNum;

    /**
     * 每页条数
     * @required true
     */
    private Integer pageSize;

}
