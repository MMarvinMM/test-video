package com.chinatower.bdm.api.entity;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GridKeeperListVO extends BaseDataScopeEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 网格员id
     * @required true
     */
    private String keeperId;
    /**
     * 网格员编码
     * @required true
     */
    private String keeperCode;
    /**
     * 网格员名称
     * @required true
     */
    private String keeperName;
    /**
     * 登录人
     * @required true
     */
    private String loginName;
    /**
     * 身份证号
     * @required true
     */
    private String keeperIdCardNumber;
    /**
     * 手机号码
     * @required true
     */
    private String keeperPhoneNumber;
    /**
     * 邮箱
     * @required false
     */
    private String keeperEmail;
    /**
     * 状态
     * @required true
     */
    private String status;
    /**
     * 经度
     * @required false
     */
    private String longitude;
    /**
     * 纬度
     * @required false
     */
    private String latitude;
    /**
     * 地址
     * @required false
     */
    private String address;
    /**
     * 省份
     * @required false
     */
    private String provinceId;
    /**
     * 地市
     * @required false
     */
    private String cityId;
    /**
     * 区县
     * @required false
     */
    private String countyId;
    /**
     * 性别: 0-男；1-女
     */
    private String keeperGender;
    private String userId;
    private String aesPhoneNumber;
    /**
     * 1 在线 0 离线
     */
    private String isLine;
    /**
     * 是否开启定位：0-是；1-否
     */
    private String locationFlag;
    /**
     * 网格员类型：1-网格长；2-网格员
     */
    private String keeperType;

    private String provinceName;

    private String cityName;

    private String tenantId;

    private String tenantName;

    private String industryCode;

    private String industryName;

    private String appVerCode;

    private String color;
    /**
     * 所属网格，多个逗号分割
     */
    private String gridId;

    private String keeperPhotoUrl;



    private List<RegionInfo> regionList;

    /**
     * 内部类，用于存储省市区信息
     */
    @Data
    public static class RegionInfo implements Serializable{
        private static final long serialVersionUID = 1L;

        private String provinceId;
        private String cityId;
        private String countyId;
    }

}
