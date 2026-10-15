package com.chinatower.device.entity.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.List;

/**
 * 无人机相机型号查询请求参数类
 */
@Data
public class UavCameraModelQryReq {
    /**
     * 型号编码
     */
    private String modelCode;

    /**
     * 相机型号
     */
    private String modelName;

    /**
     * 厂家编码
     */
    private List<String> manufacturerCode;

    /**
     * 厂家名称
     */
    private String manufacturerName;

    /**
     * 创建人
     */
    private String creator;

    /**
     * 创建起始时间.
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private String createTimeStart;

    /**
     * 创建结束时间.
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private String createTimeEnd;

    /**
     * 修改人
     */
    private String modifier;

    /**
     * 修改开始时间.
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private String updateTimeStart;

    /**
     * 修改结束时间.
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private String updateTimeEnd;

    /**
     * 排序标识：1字母升序排列
     */
    private String orderFlag;

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
     * 型号编码
     */
    private List<String> modelCodeList;
}
