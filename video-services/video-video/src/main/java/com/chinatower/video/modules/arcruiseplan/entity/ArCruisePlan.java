package com.chinatower.video.modules.arcruiseplan.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.chinatower.video.modules.artags.dataobject.ArTagsInfo;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>
 * AR巡航计划表
 * </p>
 *
 * @author songcl
 * @since 2023-04-18
 */
@TableName("vm_ar_cruise_plan")
@ApiModel(value = "ArCruisePlan对象", description = "AR巡航计划表")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ArCruisePlan implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("唯一标识")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("AR实景ID")
    private String arViewId;

    @ApiModelProperty("通道编码")
    private String channelCode;

    @ApiModelProperty("巡航计划编号")
    private String cruisePlanNumber;

    @ApiModelProperty("巡航计划名称")
    private String cruisePlanName;

    @ApiModelProperty("执行状态0未执行1等待中2执行中")
    private String executionStatus;

    @ApiModelProperty("执行位置（tag_id）")
    private String executionStep;

    @ApiModelProperty("删除标志位：0正常、1：删除")
    private Integer deleteFlag;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("创建人")
    private String creator;

    @ApiModelProperty("创建人")
    @TableField(exist = false)
    private String creatorName;

    @ApiModelProperty("修改人")
    private String modifier;

    @ApiModelProperty("修改人")
    @TableField(exist = false)
    private String modifierName;

    @TableField(exist = false)
    private List<ArCruisePlanRel> arCruisePlanRelList;

    @ApiModelProperty("客户编码")
    private String tenantId;

    @ApiModelProperty("客户名称")
    @TableField(exist = false)
    private String tenantName;

    @ApiModelProperty("行业版本编码")
    private String appVerCode;

    @ApiModelProperty("行业编码")
    private String industryCode;

    @TableField(exist = false)
    private String tagId;

    @TableField(exist = false)
    private Integer isOpen;

    @TableField(exist = false)
    private String stayDuration;

    @TableField(exist = false)
    private Integer orderNum;

    @TableField(exist = false)
    private String tagsType;
    @TableField(exist = false)
    private String tagsPosition;
    @TableField(exist = false)
    private String tagsName;
    @TableField(exist = false)
    private String tagsDesc;
    @TableField(exist = false)
    private String coordinate;
    @TableField(exist = false)
    private String resolutionX;
    @TableField(exist = false)
    private String resolutionY;

    @TableField(exist = false)
    private String presetId;
    @TableField(exist = false)
    private List<ArTagsInfo> tagsList;

    @TableField(exist = false)
    private String tagClass;

    public String getPresetId() {
        return presetId;
    }

    public void setPresetId(String presetId) {
        this.presetId = presetId;
    }

    public List<ArTagsInfo> getTagsList() {
        return tagsList;
    }

    public void setTagsList(List<ArTagsInfo> tagsList) {
        this.tagsList = tagsList;
    }

    public String getStayDuration() {
        return stayDuration;
    }

    public void setStayDuration(String stayDuration) {
        this.stayDuration = stayDuration;
    }

    public String getTagId() {
        return tagId;
    }

    public void setTagId(String tagId) {
        this.tagId = tagId;
    }

    public Integer getIsOpen() {
        return isOpen;
    }

    public void setIsOpen(Integer isOpen) {
        this.isOpen = isOpen;
    }

    public String getTagsType() {
        return tagsType;
    }

    public void setTagsType(String tagsType) {
        this.tagsType = tagsType;
    }

    public String getTagsPosition() {
        return tagsPosition;
    }

    public void setTagsPosition(String tagsPosition) {
        this.tagsPosition = tagsPosition;
    }

    public String getTagsName() {
        return tagsName;
    }

    public void setTagsName(String tagsName) {
        this.tagsName = tagsName;
    }

    public String getTagsDesc() {
        return tagsDesc;
    }

    public void setTagsDesc(String tagsDesc) {
        this.tagsDesc = tagsDesc;
    }

    public String getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(String coordinate) {
        this.coordinate = coordinate;
    }

    public String getResolutionX() {
        return resolutionX;
    }

    public void setResolutionX(String resolutionX) {
        this.resolutionX = resolutionX;
    }

    public String getResolutionY() {
        return resolutionY;
    }

    public void setResolutionY(String resolutionY) {
        this.resolutionY = resolutionY;
    }

    public ArCruisePlan() {
    }

    public ArCruisePlan(Long id, String executionStatus, Integer orderNum) {
        this.id = id;
        this.executionStatus = executionStatus;
        this.orderNum = orderNum;
    }

    public Integer getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(Integer orderNum) {
        this.orderNum = orderNum;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getArViewId() {
        return arViewId;
    }

    public void setArViewId(String arViewId) {
        this.arViewId = arViewId;
    }

    public String getCruisePlanNumber() {
        return cruisePlanNumber;
    }

    public void setCruisePlanNumber(String cruisePlanNumber) {
        this.cruisePlanNumber = cruisePlanNumber;
    }

    public String getCruisePlanName() {
        return cruisePlanName;
    }

    public void setCruisePlanName(String cruisePlanName) {
        this.cruisePlanName = cruisePlanName;
    }

    public String getExecutionStatus() {
        return executionStatus;
    }

    public void setExecutionStatus(String executionStatus) {
        this.executionStatus = executionStatus;
    }

    public String getExecutionStep() {
        return executionStep;
    }

    public void setExecutionStep(String executionStep) {
        this.executionStep = executionStep;
    }

    public Integer getDeleteFlag() {
        return deleteFlag;
    }

    public void setDeleteFlag(Integer deleteFlag) {
        this.deleteFlag = deleteFlag;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public String getModifier() {
        return modifier;
    }

    public void setModifier(String modifier) {
        this.modifier = modifier;
    }

    public String getModifierName() {
        return modifierName;
    }

    public void setModifierName(String modifierName) {
        this.modifierName = modifierName;
    }

    public List<ArCruisePlanRel> getArCruisePlanRelList() {
        return arCruisePlanRelList;
    }

    public void setArCruisePlanRelList(List<ArCruisePlanRel> arCruisePlanRelList) {
        this.arCruisePlanRelList = arCruisePlanRelList;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getAppVerCode() {
        return appVerCode;
    }

    public void setAppVerCode(String appVerCode) {
        this.appVerCode = appVerCode;
    }

    public String getIndustryCode() {
        return industryCode;
    }

    public void setIndustryCode(String industryCode) {
        this.industryCode = industryCode;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    @Override
    public String toString() {
        return "ArCruisePlan{" +
        "id=" + id +
        ", arViewId=" + arViewId +
        ", cruisePlanNumber=" + cruisePlanNumber +
        ", cruisePlanName=" + cruisePlanName +
        ", executionStatus=" + executionStatus +
        ", executionStep=" + executionStep +
        ", deleteFlag=" + deleteFlag +
        ", createTime=" + createTime +
        ", updateTime=" + updateTime +
        ", creator=" + creator +
        ", modifier=" + modifier +
        "}";
    }

    public String getChannelCode() {
        return channelCode;
    }

    public void setChannelCode(String channelCode) {
        this.channelCode = channelCode;
    }

    public String getTagClass() {
        return tagClass;
    }

    public void setTagClass(String tagClass) {
        this.tagClass = tagClass;
    }
}
