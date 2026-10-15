package com.chinatower.manage.api.dto;


import com.chinatower.common.core.dto.PageDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 属地版本管理实体
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LocalizedConfig extends PageDTO implements Serializable {




    /**
     * 新增传add 修改传modify
     */
    private String optType;

    /**
     * 是否选中
     */
    private String isChecked;

    /**
     * 属地版本号id
     */
    private String localizedVersionId;



    /**
     * 修改标识 0非必须修改 1必须修改
     */
    private String modificationFlag;


    private Long configId;

    /** 参数名称 */
    private String configName;

    /** 参数键名 */
    private String configKey;

    /** 参数键值 */
    private String configValue;

    /** 系统内置（Y是 N否） */
    private String configType;

    /** 备注 */
    private String remark;

    /** 行业版本编码*/
    private String appVerCode;

    /** 租户ID */
    private String tenantId;

    /** 用户ID */
    private String userId;

    /** 开始时间 */
    private String beginTime;

    /** 结束时间 */
    private String endTime;

    /**
     * 创建者
     */
    private String creator;


    /**
     * 更新者
     */
    private String modifier;

    /**
     * 比对结果 1：新增
     */
    private String compareResult;


}
