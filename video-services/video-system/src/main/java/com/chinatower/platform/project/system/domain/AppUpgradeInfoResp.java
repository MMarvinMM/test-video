package com.chinatower.platform.project.system.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@ApiModel(value="应用升级参数出参", description="应用升级参数信息相关出参")
public class AppUpgradeInfoResp implements Serializable {
    /**
     * APP应用标识
     */
    @ApiModelProperty(value="APP应用标识")
    private String appCode;

    /**
     * APP应用名称
     */
    @ApiModelProperty(value="APP应用名称")
    private String appName;

    /**
     * APP应用版本 （查询升级取最新的）
     */
    @ApiModelProperty(value="APP应用版本 （查询升级取最新的）")
    private String appVersion;

    /**
     * APP应用版本号 （查询升级取最新的）
     */
    @ApiModelProperty(value="APP应用版本号 （前端查询对比大小升级取最新的）")
    private String appVersionNo;

    /**
     * 版本升级URL
     */
    @ApiModelProperty(value="版本升级URL")
    private String upgradeUrl;

    /**
     * 是否强制更新（0 非强制更新    1 强制更新）
     */
    @ApiModelProperty(value="是否强制更新（0 非强制更新    1 强制更新）",example = "0")
    private String isForceUpgrade;



    /**
     * 升级描述信息
     */
    @ApiModelProperty(value="升级描述信息")
    private String upgradeDescribe;


    /**
     * 创建时间
     */
    @ApiModelProperty(value="创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date createTime;



}
