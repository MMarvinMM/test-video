package com.chinatower.event.dto;

import com.chinatower.common.core.entity.BaseIndustryEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 环节表对象
 * @author chmod
 * @version 1.0
 * @date 2022/1/25 11:03
 */
@Data
public class FlowLinkConfigModel extends BaseIndustryEntity implements Serializable {
    private static final long serialVersionUID = -3336416254161903768L;
    private Long linkId;
    /**
     * 环节名称
     */
    private String linkName;
    /**
     * 环节状态 1生效 0失效
     */
    private String status;
    /** 查询开启分页：true-开启，false-关闭 */
    private boolean enablePage;
    private Integer pageNum;
    private Integer pageSize;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date createStartTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date createEndTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date updateStartTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateEndTime;

    /**
     * 排序方式
     * 排序字段
     * @return
     */
    private String sortName;

    /**
     * 排序规则
     */
    private String sort;
    /**
     * 租户ID
     */
    private String tenantId;
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
