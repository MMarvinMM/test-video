package com.chinatower.device.entity.vo;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import lombok.Data;

import java.util.List;


@Data
public class DeviceYjbReq extends BaseDataScopeEntity {
    /**
     *  省份编码
     */
//    @NotBlank(message = "省份编码不能为空！")
    private String provinceCode;

    /**
     *  地市编码
     */
//    @NotBlank(message = "地市编码不能为空！")
    private String cityCode;
    /**
     *  区县编码
     */
//    @NotBlank(message = "区县编码不能为空！")
    private String countyCode;

    /**
     *  街道编码
     */
//    @NotBlank(message = "街道编码不能为空！")
    private String townCode;

    /**
     *  开始时间
     */
//    @NotBlank(message = "开始时间不能为空！")
    private String startDate;

    /**
     *  结束时间
     */
//    @NotBlank(message = "结束时间不能为空！")
    private String endDate;

    private List<String> deviceCodeList;

    /**
     *  标签名称，支持多标签查询，用逗号分隔
     */
    private String labelName;

    /**
     *  码流类型
     */
    private Integer streamType;

    /**
     *  协议类型
     */
    private Integer protocolType;

    /**
     *  网域类型
     */
    private Integer netType;

    /**
     *  当前页码
     */
    private Integer pageNum;

    /**
     *  每页条数
     */
    private Integer pageSize;

    /**
     *  标签名称集合
     *  标签名称
     */
    private List<String> labelNames;
}
