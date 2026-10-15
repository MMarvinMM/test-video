package com.chinatower.outdata.api.entity;

import com.chinatower.platform.model.dto.CommonFeignEntity;
import lombok.Data;

import java.util.List;

@Data
public class QueryStatusFakeReq extends CommonFeignEntity {

    /**
     * 传感器编码
     * @required false
     */
    private String siteCodes;
    /**
     * 传感器编码集合
     * @required false
     */
    private List<String> siteCodeList;
    /**
     * 省份id
     * @required false
     */
    private String provinceId;
    /**
     * 市id
     * @required false
     */
    private String cityId;
    /**
     * 县id
     * @required false
     */
    private String countyId;

    /**
     * 设备编码
     * @required false
     */
    private String deviceCode;
    /**
     * 设备名称
     * @required false
     */
    private String deviceName;
    /**
     * 状态
     * @required false
     */
    private String status;

    /**
     * 组织id列表
     * @required false
     */
    private List<String> deptIdList;

    /**
     * 当前页数
     * @required false
     */
    private Integer offset;
    /**
     * 每页限制条数
     * @required false
     */
    private Integer limit;

    /**
     * 查询开启分页：true-开启，false-关闭
     * @required false
     */
    private boolean enablePage;
}
