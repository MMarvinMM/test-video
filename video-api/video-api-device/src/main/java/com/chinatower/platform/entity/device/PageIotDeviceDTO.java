package com.chinatower.platform.entity.device;


import com.chinatower.common.core.entity.BaseDataScopeEntity;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class PageIotDeviceDTO extends BaseDataScopeEntity implements Serializable {


    /**
     * 行业应用编码
     */
    private String industryCode;

    /**
     * 租户编码
     */
    private String tenantId;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 设备型号编码集合
     */
    private List<String> deviceTypeList;

    /**
     * 当前页
     */
    private Integer pageNum;

    /**
     * 每页条数
     */
    private Integer pageSize;

    /**
     * 是否分页
     */
    private Boolean enablePage = true;

    /**
     * 设备类型  --8  物联设备
     */
    private String deviceType;

}
