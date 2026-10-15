package com.chinatower.device.entity.dto;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author willem
 * @date 2024/10/9
 * @description 喇叭内容管理配置
 */
@Data
public class VmHornContentConfRequestListDTO extends BaseDataScopeEntity {
    /**
     * 主键ID
     */
    private Long id;
    /**
     * 内容标题
     */
    private String contentTitle;
    /**
     * 内容类型  0-音频 1-文本
     */
    private Integer contentType;
    /**
     * 审核状态 0-未审核 1-审核通过 2-审核不通过
     */
    private String auditStatus;
    /**
     * 审核时间开始
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date auditTimeStart;
    /**
     * 审核时间结束
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date auditTimeEnd;
    /**
     * 修改时间开始
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date updateTimeStart;
    /**
     * 修改时间结束
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date updateTimeEnd;
    /**
     * 创建时间开始
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date createTimeStart;
    /**
     * 创建时间结束
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date createTimeEnd;
    /**
     * 审核人
     */
    private String auditor;
    /**
     * 修改人
     */
    private String modifier;
    /**
     * 创建人
     */
    private String creator;
    /**
     * 行业编码
     */
    private String industryCode;
    /**
     * 喇叭平台上传后的资源id
     */
    private String resourceId;
    /**
     * 租户编码
     */
    private String tenantId;
    /**
     * 页码
     */
    private Integer pageNum;

    /**
     * 每页数量
     */
    private Integer pageSize;

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
     * id列表
     */
    private List<Long> idList;

    /**
     * 适配无人机pcm广播 文件类型 0-其他格式 1-pcm格式
     */
    private String fileType;

    /**
     * queryType 传 uav 就是查询无人机需要的
     */
    private String queryType;
}
