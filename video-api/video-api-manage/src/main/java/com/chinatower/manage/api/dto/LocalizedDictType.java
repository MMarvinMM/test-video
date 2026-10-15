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
public class LocalizedDictType extends PageDTO implements Serializable {




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


    /** 字典主键 */
    private Long dictId;

    /** 字典名称 */
    private String dictName;

    /** 字典类型 */
    private String dictType;

    /** 状态（0正常 1停用） */
    private String status;

    /** 备注 */
    private String remark;

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
     * 比对结果 1：新增字典类型 2：新增字典数据
     */
    private String compareResult;



}
