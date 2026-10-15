package com.chinatower.platform.entity.admin;

import com.chinatower.common.annotation.Excel;
import com.chinatower.platform.model.dto.CommonFeignEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 行政区域表
 * @TableName sys_area
 */
@Data
@ApiModel(value = "SysArea",description = "行政区域实体")
public class SysArea extends CommonFeignEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 区域编码
     */
    @Excel(name = "区域编码")
    @ApiModelProperty(value = "行政区域编码")
    private String areaCode;

    /**
     * 区域名称
     */
    @Excel(name = "区域名称")
    @ApiModelProperty(value = "区域名称")
    private String areaName;

    /**
     * 区域等级
     */
    @Excel(name = "区域等级")
    @ApiModelProperty(value = "区域等级")
    private String areaLevel;

    /**
     * 父级编码
     */
    @Excel(name = "上级区域编码")
    @ApiModelProperty(value = "上级区域编码")
    private String areaParentCode;

    /**
     * 父级名称
     */
    @Excel(name = "上级区域名称")
    @ApiModelProperty(value = "上级区域名称")
    private String areaParentName;

    /**
     * 状态（0正常 1停用）
     */
    private String status;

    /**
     * 经度
     */
    @Excel(name = "经度坐标")
    @ApiModelProperty(value = "经度坐标")
    private String longitude;

    /**
     * 纬度
     */
    @Excel(name = "纬度坐标")
    @ApiModelProperty(value = "纬度坐标")
    private String latitude;

    /**
     * 删除标志（0未删除 2已删除）
     */
    private String delFlag;

    /**
     * 部门ID
     */
    private String deptId;

    /**
     * 归属主体
     */
    private String bussnessUnit;

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        SysArea other = (SysArea) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getAreaCode() == null ? other.getAreaCode() == null : this.getAreaCode().equals(other.getAreaCode()))
            && (this.getAreaName() == null ? other.getAreaName() == null : this.getAreaName().equals(other.getAreaName()))
            && (this.getAreaLevel() == null ? other.getAreaLevel() == null : this.getAreaLevel().equals(other.getAreaLevel()))
            && (this.getAreaParentCode() == null ? other.getAreaParentCode() == null : this.getAreaParentCode().equals(other.getAreaParentCode()))
            && (this.getAreaParentName() == null ? other.getAreaParentName() == null : this.getAreaParentName().equals(other.getAreaParentName()))
            && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()))
            && (this.getLongitude() == null ? other.getLongitude() == null : this.getLongitude().equals(other.getLongitude()))
            && (this.getLatitude() == null ? other.getLatitude() == null : this.getLatitude().equals(other.getLatitude()))
            && (this.getCreateBy() == null ? other.getCreateBy() == null : this.getCreateBy().equals(other.getCreateBy()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
            && (this.getUpdateBy() == null ? other.getUpdateBy() == null : this.getUpdateBy().equals(other.getUpdateBy()))
            && (this.getUpdateTime() == null ? other.getUpdateTime() == null : this.getUpdateTime().equals(other.getUpdateTime()))
            && (this.getDelFlag() == null ? other.getDelFlag() == null : this.getDelFlag().equals(other.getDelFlag()))
            && (this.getDeptId() == null ? other.getDeptId() == null : this.getDeptId().equals(other.getDeptId()))
            && (this.getBussnessUnit() == null ? other.getBussnessUnit() == null : this.getBussnessUnit().equals(other.getBussnessUnit()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getAreaCode() == null) ? 0 : getAreaCode().hashCode());
        result = prime * result + ((getAreaName() == null) ? 0 : getAreaName().hashCode());
        result = prime * result + ((getAreaLevel() == null) ? 0 : getAreaLevel().hashCode());
        result = prime * result + ((getAreaParentCode() == null) ? 0 : getAreaParentCode().hashCode());
        result = prime * result + ((getAreaParentName() == null) ? 0 : getAreaParentName().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
        result = prime * result + ((getLongitude() == null) ? 0 : getLongitude().hashCode());
        result = prime * result + ((getLatitude() == null) ? 0 : getLatitude().hashCode());
        result = prime * result + ((getCreateBy() == null) ? 0 : getCreateBy().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        result = prime * result + ((getUpdateBy() == null) ? 0 : getUpdateBy().hashCode());
        result = prime * result + ((getUpdateTime() == null) ? 0 : getUpdateTime().hashCode());
        result = prime * result + ((getDelFlag() == null) ? 0 : getDelFlag().hashCode());
        result = prime * result + ((getDeptId() == null) ? 0 : getDeptId().hashCode());
        result = prime * result + ((getBussnessUnit() == null) ? 0 : getBussnessUnit().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", areaCode=").append(areaCode);
        sb.append(", areaName=").append(areaName);
        sb.append(", areaLevel=").append(areaLevel);
        sb.append(", areaParentCode=").append(areaParentCode);
        sb.append(", areaParentName=").append(areaParentName);
        sb.append(", status=").append(status);
        sb.append(", longitude=").append(longitude);
        sb.append(", latitude=").append(latitude);
        sb.append(", delFlag=").append(delFlag);
        sb.append(", deptId=").append(deptId);
        sb.append(", bussnessUnit=").append(bussnessUnit);
        sb.append("]");
        return sb.toString();
    }
}
