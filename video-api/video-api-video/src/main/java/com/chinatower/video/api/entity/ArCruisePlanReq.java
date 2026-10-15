package com.chinatower.video.api.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * AR标签信息表
 * </p>
 *
 * @author songcl
 * @since 2023-04-18
 */
@ApiModel(value = "ArCruisePlan对象", description = "AR巡航计划表")
public class ArCruisePlanReq implements Serializable {


    @ApiModelProperty("唯一标识")
    private Long id;

    @ApiModelProperty("AR实景ID")
    private String arViewId;

    @ApiModelProperty("通道编码")
    private String channelCode;

    @ApiModelProperty("巡航计划名称")
    private String cruisePlanName;

    @ApiModelProperty("执行状态0未执行1等待中2执行中")
    private String executionStatus;

    @ApiModelProperty("页码")
    private Integer pageNum;

    @ApiModelProperty("每页条数")
    private Integer pageSize;

    @ApiModelProperty("查询时间")
    private String queryTime;

    private List<ArCruisePlanRelReq> arCruisePlanRelList;

    @ApiModelProperty("是否铁塔视角，1是0否")
    private int isTowerView;

    private Integer orderNum;

    private String tenantId;

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getIndustryCode() {
        return industryCode;
    }

    public void setIndustryCode(String industryCode) {
        this.industryCode = industryCode;
    }

    private String industryCode;

    public Integer getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(Integer orderNum) {
        this.orderNum = orderNum;
    }

    public int getIsTowerView() {
        return isTowerView;
    }

    public void setIsTowerView(int isTowerView) {
        this.isTowerView = isTowerView;
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

    public List<ArCruisePlanRelReq> getArCruisePlanRelList() {
        return arCruisePlanRelList;
    }

    public void setArCruisePlanRelList(List<ArCruisePlanRelReq> arCruisePlanRelList) {
        this.arCruisePlanRelList = arCruisePlanRelList;
    }

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public String getQueryTime() {
        return queryTime;
    }

    public void setQueryTime(String queryTime) {
        this.queryTime = queryTime;
    }

    @Override
    public String toString() {
        return "ArCruisePlan{" +
        "id=" + id +
        ", arViewId=" + arViewId +
        ", cruisePlanName=" + cruisePlanName +
        "}";
    }
    public String getChannelCode() {
        return channelCode;
    }

    public void setChannelCode(String channelCode) {
        this.channelCode = channelCode;
    }
    public static class ArCruisePlanRelReq implements Serializable {

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

        /**
         * 标签类别  101视频画面手动标签、102地图经纬度标签、103摄像机标签、104图层标签、105物联标签、106无人机标签、107雷达标签、108喇叭标签
         */
        private String tagClass;

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

        @Override
        public String toString() {
            return "ArCruisePlanRel{" +
                    ", cruisePlanId=" + cruisePlanId +
                    ", tagId=" + tagId +
                    ", stayDuration=" + stayDuration +
                    ", isOpen=" + isOpen +
                    ", orderNum=" + orderNum +
                    "}";
        }

        public String getTagClass() {
            return tagClass;
        }

        public void setTagClass(String tagClass) {
            this.tagClass = tagClass;
        }
    }
}
