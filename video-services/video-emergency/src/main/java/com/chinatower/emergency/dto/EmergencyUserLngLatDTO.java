package com.chinatower.emergency.dto;

import java.io.Serializable;
import java.util.Date;

import com.chinatower.mongodb.dto.BaseMongoDTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * 用户打点记录表
 * vm_emergency_user_lngLat_info
 * @author Liqi
 */
@Data
public class EmergencyUserLngLatDTO extends BaseMongoDTO implements Serializable {

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 用户昵称
     */
    private String nickName;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 行业编码
     */
    private String industryCode;

    /**
     * 用户性别（0男 1女）
     */
    private String sex;

    /**
     * 行业版本编码
     */
    private String appVerCode;

    /**
     * 经度
     */
    private String longitude;

    /**
     * 纬度
     */
    private String latitude;

    /**
     * 电话号码
     */
    private String phoneNumber;

    /**
     * 备注
     */
    private String remark;

    /**
     * 打点时间
     */
    @JsonFormat(pattern= "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date takePointTime;

    /**
     * 创建时间
     */
    @JsonFormat(pattern= "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createTime;

    /**
     * 1 在线 0 离线
     */
    private String isLine;

    /**
     * 租户
     */
    private String tenantId;

    /**
     *   移动端号码
     */
    private String mobileTerminalNum;

    /**
     *   控制台号码
     */
    private String dispatchDeskNum;

    /**
     *   地址
     */
    private String address;

    private static final long serialVersionUID = 1L;
}