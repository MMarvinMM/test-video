package com.chinatower.platform.project.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.chinatower.common.annotation.Excel;
import com.chinatower.platform.framework.web.domain.SystemBaseEntity;
/**
 * 对象 sys_area_street
 * @date 2022-03-18
 */
public class SysAreaStreet extends SystemBaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private String id;

    /** 区域编码 */
    @Excel(name = "区域编码")
    private String areaCode;

    /** 区域名称 */
    @Excel(name = "区域名称")
    private String areaName;

    /** 区域等级 */
    @Excel(name = "区域等级")
    private String areaLevel;

    /** 父级编码 */
    @Excel(name = "父级编码")
    private String areaParentCode;

    /** 父级名称 */
    @Excel(name = "父级名称")
    private String areaParentName;

    /** 状态（0正常 1停用） */
    @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
    private String status;

    /** 经度 */
    @Excel(name = "经度")
    private String longitude;

    /** 纬度 */
    @Excel(name = "纬度")
    private String latitude;

    /** 删除标志（0未删除 2已删除） */
    @Excel(name = "删除标志", readConverterExp = "0=未删除,2=已删除")
    private String delFlag;

    /** 部门ID */
    @Excel(name = "部门ID")
    private String deptId;

    /** 归属主体 */
    @Excel(name = "归属主体")
    private String bussnessUnit;

    public void setId(String id)
    {
        this.id = id;
    }

    public String getId()
    {
        return id;
    }
    public void setAreaCode(String areaCode)
    {
        this.areaCode = areaCode;
    }

    public String getAreaCode()
    {
        return areaCode;
    }
    public void setAreaName(String areaName)
    {
        this.areaName = areaName;
    }

    public String getAreaName()
    {
        return areaName;
    }
    public void setAreaLevel(String areaLevel)
    {
        this.areaLevel = areaLevel;
    }

    public String getAreaLevel()
    {
        return areaLevel;
    }
    public void setAreaParentCode(String areaParentCode)
    {
        this.areaParentCode = areaParentCode;
    }

    public String getAreaParentCode()
    {
        return areaParentCode;
    }
    public void setAreaParentName(String areaParentName)
    {
        this.areaParentName = areaParentName;
    }

    public String getAreaParentName()
    {
        return areaParentName;
    }
    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getStatus()
    {
        return status;
    }
    public void setLongitude(String longitude)
    {
        this.longitude = longitude;
    }

    public String getLongitude()
    {
        return longitude;
    }
    public void setLatitude(String latitude)
    {
        this.latitude = latitude;
    }

    public String getLatitude()
    {
        return latitude;
    }
    public void setDelFlag(String delFlag)
    {
        this.delFlag = delFlag;
    }

    public String getDelFlag()
    {
        return delFlag;
    }
    public void setDeptId(String deptId)
    {
        this.deptId = deptId;
    }

    public String getDeptId()
    {
        return deptId;
    }
    public void setBussnessUnit(String bussnessUnit)
    {
        this.bussnessUnit = bussnessUnit;
    }

    public String getBussnessUnit()
    {
        return bussnessUnit;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("areaCode", getAreaCode())
            .append("areaName", getAreaName())
            .append("areaLevel", getAreaLevel())
            .append("areaParentCode", getAreaParentCode())
            .append("areaParentName", getAreaParentName())
            .append("status", getStatus())
            .append("longitude", getLongitude())
            .append("latitude", getLatitude())
            .append("creator", getCreator())
            .append("createTime", getCreateTime())
            .append("modifier", getModifier())
            .append("updateTime", getUpdateTime())
            .append("delFlag", getDelFlag())
            .append("deptId", getDeptId())
            .append("bussnessUnit", getBussnessUnit())
            .toString();
    }
}
