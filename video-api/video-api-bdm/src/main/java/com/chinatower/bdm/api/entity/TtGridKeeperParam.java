package com.chinatower.bdm.api.entity;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import lombok.Data;

@Data
public class TtGridKeeperParam extends BaseDataScopeEntity {

    /**
     * 经度
     */
    private String longitude;
    /**
     * 维度
     */
    private String latitude;
    /**
     * 区编码
     */
    private String cityId;
    /**
     * 地图类型: 01-高德;02-百度;03-天地图
     */
    private String mapType;
    /**
     * 租户ID
     */
    private String tenantId;
    /**
     * 租户ID
     */
    private String tenantName;
    /**
     * 0-启用 1-停用
     */
    private String status;
    /**
     * 网格员编号
     */
    private String keeperCode;
    /**
     * 网格员姓名
     */
    private String keeperName;
    /**
     * 登录名
     */
    private String loginName;
    /**
     * 联系方式
     */
    private String keeperPhoneNumber;
    /**
     * 网格编码
     */
    private String gridId;
    /**
     * 关键字：app用
     */
    private String keyword;
    /**
     * 是否开启定位：0-是；1-否
     */
    private String locationFlag;
    /**
     * 网格员类型：1-网格长；2-网格员
     */
    private String deptId;
    private String keeperType;
    private Integer pageNum = 1;
    private Integer pageSize = 10;
}
