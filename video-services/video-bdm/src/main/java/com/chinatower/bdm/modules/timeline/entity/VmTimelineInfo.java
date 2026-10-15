package com.chinatower.bdm.modules.timeline.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 时间轴基础表(VmTimelineInfo)实体类
 *
 * @author makejava
 * @since 2024-05-27 16:57:52
 */
@Data
@Accessors(chain = true)
@TableName("vm_timeline_info")
@ApiModel(value = "VmTimelineInfo", description = "")
public class VmTimelineInfo implements Serializable {
    private static final long serialVersionUID = -65979973107173814L;
    /**
     * 时间轴信息ID
     */
    private Long timelineId;
    /**
     * 时间轴名称
     */
    private String timelineName;
    /**
     * 行业信息
     */
    private String industryCode;
    /**
     * 行业应用版本编码
     */
    private String appVerCode;
    /**
     * 租户ID
     */
    private String tenantId;
    /**
     * 租户名称
     */
    private String tenantName;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新时间
     */
    private Date updateTime;

    private String creator;

    private String modifier;
    /**
     * 0正常 1删除
     */
    private Integer deleteFlag;
    /**
     * 默认时间轴 0非默认 1默认
     */
    private Integer defaultFlag;
    /**
     * 排列顺序
     */
    private Integer sortNum;

}
