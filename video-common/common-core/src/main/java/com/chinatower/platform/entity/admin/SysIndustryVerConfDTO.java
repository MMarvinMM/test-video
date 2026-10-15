package com.chinatower.platform.entity.admin;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 功能说明:行业应用类别 sys_industry_ver_conf
 * @param:
 * @return:
 * @author: 王梦迪
 * @date: 2022/3/17 10:00
 * @requestID  需求单号及需求名称
 */
@Data
public class SysIndustryVerConfDTO implements Serializable {
    private static final long serialVersionUID = 2881663621257511762L;
    /** 行业配置ID */
    private Long id;
    private String industryCode;

    private String appVerCode;
    private String appVerName;
    private String appVerDesc;
    /** 创建人 */
    private String creator;
    /** 修改人 */
    private String modifier;
    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date createTime;
    /** 修改时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date updateTime;
    /** 1生效 0失效 */
    private String status;

    /**
     * 删除标识 正常0  删除1
     */
    private String deleteFlag;

    /** 应用图标URL */
    private String imgUrl;

    /**
     * bannerId
     */
    private Long bannerId;

    /**
     * banner标题
     */
    private String bannerTitle;

    /**
     * 大屏banner风格 wiseblue-睿智蓝 aquamarine-碧山绿 terracotta-赤土黄
     */
    private String bannerType;

}
