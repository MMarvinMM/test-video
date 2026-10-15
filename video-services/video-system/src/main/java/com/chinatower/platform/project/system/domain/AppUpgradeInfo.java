package com.chinatower.platform.project.system.domain;

import com.chinatower.common.annotation.Excel;
import com.chinatower.common.core.dto.PageDTO;
import com.chinatower.common.core.entity.BaseDataScopeEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * APP版本升级对象 BS_APP_UPGRADE_INFO
 * @author lwz
 * @date 2020-09-15
 */
@ApiModel("APP版本升级")
@Data
public class AppUpgradeInfo extends PageDTO {

    private static final long serialVersionUID = 1L;

    private Long id;

    @ApiModelProperty(value = "应用标识")
    @Excel(name = "应用标识")
    private String appCode;


    @ApiModelProperty(value = "应用名称")
    @Excel(name = "应用名称")
    private String appName;


    @ApiModelProperty(value = "应用版本号 ",allowableValues = "查询升级取最新的")
    @Excel(name = "APP应用版本号 ", readConverterExp = "查询升级取最新的")
    private String appVersion;

    @ApiModelProperty(value = "升级内容")
    @Excel(name = "升级内容",width = 100)
    private String upgradeDescribe;



    @ApiModelProperty(value = "是否强制更新",allowableValues = "0,非强制更新,1,强制更新")
    @Excel(name = "是否强制更新", readConverterExp = "0=否,1=是")
    private String isForceUpgrade;


    @ApiModelProperty(value = "版本升级URL")
    @Excel(name = "版本升级URL",width = 60)
    private String upgradeUrl;


    @ApiModelProperty(value = "是否删除 ",allowableValues = "0,未删除,2删除")
    private String delFlag;


    @ApiModelProperty(value = "系部")
    private String deptId;

    /**
     * R-ZBZB-202110250799360-APP版本管理新增本地上传apk
     */
    @ApiModelProperty(value = "上传标识")
    private String uploadFlag;

    /**
     * R-ZBZB-202110250799360-APP版本管理新增本地上传apk
     */
    @ApiModelProperty(value = "文件ID")
    private String fileId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date beginCreateTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date endCreateTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date beginUpdateTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date endUpdateTime;

    private String fileName;

    private String fileUrl;

    private String encryptionUrl;

}
