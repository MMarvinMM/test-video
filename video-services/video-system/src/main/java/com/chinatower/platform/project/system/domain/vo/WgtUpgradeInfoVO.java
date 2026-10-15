package com.chinatower.platform.project.system.domain.vo;

import com.chinatower.common.core.dto.PageDTO;
import com.chinatower.common.core.entity.BaseDataScopeEntity;
import com.chinatower.common.utils.StringUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * WGT版本对象 BS_WGT_UPGRADE_INFO
 * @author lzt
 * @date 2024-01-31
 */
@ApiModel("WGt版本管理")
@Data
public class WgtUpgradeInfoVO extends PageDTO {

    private Long id;

    @ApiModelProperty(value = "属地版本")
    private String colonyVersion;

    @ApiModelProperty(value = "WGT应用版本号")
    private String wgtVersion;

    @ApiModelProperty(value = "版本升级URL")
    private String upgradeUrl;

    @ApiModelProperty(value = "是否强制更新（0,非强制更新,1,强制更新）")
    private String isForceUpgrade;

    @ApiModelProperty(value = "版本描述")
    private String upgradeDescribe;

    @ApiModelProperty(value = "wgt包名")
    private String wgtPackageName;

    private String colonyCode;

    private String wgtStatus;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date createTimeStart;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date createTimeEnd;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date updateTimeStart;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date updateTimeEnd;

    @ApiModelProperty(value = "类型 1-ios 2-android")
    private Integer type;

    /**
     * 节点编码
     */
    private String platformCode;

    /**
     * 节点名称
     */
    private String platformName;

    /**
     * 父级id
     */
    private Long parentId;

    /**
     * 子节点列表
     */
    private List<WgtUpgradeInfoVO> children;

    /**
     * 查询类型：0-父级
     */
    private Integer queryType;


    private List<Long> parentIds;

}
