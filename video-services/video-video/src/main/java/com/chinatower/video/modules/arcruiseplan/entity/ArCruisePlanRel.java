package com.chinatower.video.modules.arcruiseplan.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.chinatower.video.modules.artags.dataobject.ArTagsInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * AR巡航计划表
 * </p>
 *
 * @author songcl
 * @since 2023-04-18
 */
@TableName("vm_ar_cruise_plan_rel")
@ApiModel(value = "ArCruisePlanRel对象", description = "AR巡航计划表")
public class ArCruisePlanRel implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("唯一标识")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("计划ID")
    private Long cruisePlanId;

    @ApiModelProperty("标签ID")
    private String tagId;

    @ApiModelProperty("停留时长秒")
    private Long stayDuration;

    @ApiModelProperty("是否弹出窗口（1是0否）")
    private Integer isOpen;

    @ApiModelProperty("排序列")
    private Integer orderNum;

    @ApiModelProperty("删除标志位：0正常、1：删除")
    private Integer deleteFlag;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("创建人")
    private String creator;

    @ApiModelProperty("修改人")
    private String modifier;

    @ApiModelProperty("标签信息")
    @TableField(exist = false)
    private ArTagsInfo arTagsInfo;

    /**
     * 标签类别  101视频画面手动标签、102地图经纬度标签、103摄像机标签、104图层标签、105物联标签、106无人机标签、107雷达标签、108喇叭标签
     */
    private String tagClass;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCruisePlanId() {
        return cruisePlanId;
    }

    public void setCruisePlanId(Long cruisePlanId) {
        this.cruisePlanId = cruisePlanId;
    }

    public String getTagId() {
        return tagId;
    }

    public void setTagId(String tagId) {
        this.tagId = tagId;
    }

    public Long getStayDuration() {
        return stayDuration;
    }

    public void setStayDuration(Long stayDuration) {
        this.stayDuration = stayDuration;
    }

    public Integer getIsOpen() {
        return isOpen;
    }

    public void setIsOpen(Integer isOpen) {
        this.isOpen = isOpen;
    }

    public Integer getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(Integer orderNum) {
        this.orderNum = orderNum;
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

    public String getModifier() {
        return modifier;
    }

    public void setModifier(String modifier) {
        this.modifier = modifier;
    }

    public ArTagsInfo getArTagsInfo() {
        return arTagsInfo;
    }

    public void setArTagsInfo(ArTagsInfo arTagsInfo) {
        this.arTagsInfo = arTagsInfo;
    }

    @Override
    public String toString() {
        return "ArCruisePlanRel{" +
        "id=" + id +
        ", cruisePlanId=" + cruisePlanId +
        ", tagId=" + tagId +
        ", stayDuration=" + stayDuration +
        ", isOpen=" + isOpen +
        ", orderNum=" + orderNum +
        ", deleteFlag=" + deleteFlag +
        ", createTime=" + createTime +
        ", updateTime=" + updateTime +
        ", creator=" + creator +
        ", modifier=" + modifier +
        "}";
    }

    public String getTagClass() {
        return tagClass;
    }

    public void setTagClass(String tagClass) {
        this.tagClass = tagClass;
    }
}
