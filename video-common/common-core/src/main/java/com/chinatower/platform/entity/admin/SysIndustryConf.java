package com.chinatower.platform.entity.admin;

import lombok.Data;
import java.io.Serializable;
import java.util.Date;
/**
 * 功能说明:行业应用类别sys_industry_conf
 * @param:
 * @return:
 * @author: 王梦迪
 * @date: 2022/3/17 10:00
 * @requestID  需求单号及需求名称
 */
@Data
public class SysIndustryConf implements Serializable {
    private static final long serialVersionUID = 2881663621257511762L;
    /** 行业配置ID */
    private Long industryId;
    private String industryName;
    private String industryCode;
    /** 创建人 */
    private String creator;
    /** 修改人 */
    private String modifier;
    /** 创建时间 */
    private Date createTime;
    /** 修改时间 */
    private Date updateTime;
    /** 1生效 0失效 */
    private String status;
    private String industryDesc;

    /** 应用图标 */
    private String imgUrl;

    /**
     * 删除标识 正常0  删除1
     */
    private String deleteFlag;

    /** 接口配置 0-API 1-SDK 2-未配置 */
    private String apiIntfConfigType;

    /** appKey */
    private String appKey;

    /** secretKey */
    private String secretKey;


}
