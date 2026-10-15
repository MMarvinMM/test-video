package com.chinatower.platform.project.system.domain;

import com.chinatower.platform.framework.web.domain.SystemBaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 【请填写功能名称】对象 sys_login_config
 *
 * @author 王梦迪
 * @date 2021-11-15
 */
public class SysLoginConfig extends SystemBaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键自增id */
    private Long id;

    /** 标题 */
    private String title;

    /** 图片id */
    private String pictureUrl;

    /** 是否启用状态（0启用 1禁用） */
    private String status;

    /** 删除标志（0代表存在 2代表删除） */
    private String deleteFlag;

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId()
    {
        return id;
    }
    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getTitle()
    {
        return title;
    }
    public void setPictureUrl(String pictureUrl)
    {
        this.pictureUrl = pictureUrl;
    }

    public String getPictureUrl()
    {
        return pictureUrl;
    }
    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getStatus()
    {
        return status;
    }
    public void setDeleteFlag(String deleteFlag)
    {
        this.deleteFlag = deleteFlag;
    }

    public String getDeleteFlag()
    {
        return deleteFlag;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("title", getTitle())
            .append("pictureUrl", getPictureUrl())
            .append("status", getStatus())
            .append("deleteFlag", getDeleteFlag())
            .append("creator", getCreator())
            .append("createTime", getCreateTime())
            .append("modifier", getModifier())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
