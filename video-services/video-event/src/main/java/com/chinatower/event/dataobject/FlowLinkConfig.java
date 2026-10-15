package com.chinatower.event.dataobject;

import com.baomidou.mybatisplus.annotation.TableName;
import com.chinatower.common.core.entity.BaseIndustryEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 流程环节配置实体
 *
 * @author lyy
 * @version 1.0
 * @date 2022/08/01 16:00
 */
@Data
@TableName("vm_flow_link_config")
public class FlowLinkConfig extends BaseIndustryEntity implements Serializable {
    private static final long serialVersionUID = -7321303919574901500L;

    /** 主键 */
    private Long linkId;
    /**
     * 流程环节名称
     */
    private String linkName;
    /**
     * 状态：1生效 0失效
     */
    private String status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    /**
     * 流程模板ID
     */
    private Long templateId;
    /**
     * 环节来源
     */
    private String linkSource;
    /**
     * 环节类型
     */
    private Long realLinkType;
}
