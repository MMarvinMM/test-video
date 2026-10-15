package com.chinatower.platform.project.system.domain;

import javax.validation.constraints.Size;
import javax.validation.constraints.NotNull;

import java.io.Serializable;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
* WGT版本管理表
* @TableName bs_wgt_upgrade_info
*/
@Data
@TableName("bs_wgt_upgrade_info")
public class WgtUpgradeInfoDO implements Serializable {

    /**
    * 主键
    */
    @ApiModelProperty("主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
    * 属地版本
    */
    @ApiModelProperty("属地版本")
    private String colonyVersion;
    /**
    * WGT应用版本号
    */
    @ApiModelProperty("WGT应用版本号")
    private String wgtVersion;
    /**
    * 版本升级URL
    */
    @ApiModelProperty("版本升级URL")
    private String upgradeUrl;
    /**
    * 是否强制更新（0 否 1 是）
    */
    @ApiModelProperty("是否强制更新（0 否 1 是）")
    private Integer isForceUpgrade;
    /**
    * 版本描述
    */
    @ApiModelProperty("版本描述")
    private String upgradeDescribe;
    /**
    * 删除标识 正常0  删除1
    */
    @ApiModelProperty("删除标识 正常0  删除1")
    private Integer deleteFlag;
    /**
    * 创建人
    */
    @ApiModelProperty("创建人")
    private String creator;
    /**
    * 创建时间
    */
    @ApiModelProperty("创建时间")
    private Date createTime;
    /**
    * 更新人
    */
    @ApiModelProperty("更新人")
    private String modifier;
    /**
    * 更新时间
    */
    @ApiModelProperty("更新时间")
    private Date updateTime;

}
