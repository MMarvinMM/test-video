package com.chinatower.device.entity.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class UavMountModelQueryDTO implements Serializable {

    private String modelCode;

    private String modelName;

    private List<String> manufacturerCodeList;

    private List<String> mountTypeList;

    private String supportFunction;

    private String creator;

    private String modifier;

    /**
     * 创建开始时间
     */
    private String createTimeStart;
    /**
     * 创建结束时间
     */
    private String createTimeEnd;
    /**
     * 修改开始时间
     */
    private String updateTimeStart;
    /**
     * 修改结束时间
     */
    private String updateTimeEnd;

    /**
     * 排序字段
     */
    private String sortName;

    /**
     * 排序规则
     */
    private String sort;

    private Integer pageSize;

    private Integer pageNum;

    /**
     * 默认开启分页
     */
    private boolean enablePage = true;
    /**
     * 挂载型号
     */
    private List<String> modelCodeList;
}
