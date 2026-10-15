package com.chinatower.emergency.dto;

import com.chinatower.platform.model.dto.CommonFeignEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

/**
 * @program: video-platform
 * @ClassName: EmergencyEventAttachVO
 * @description: 事件附件表
 * @author: fengty
 * @create: 2024-10-28 14:29
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmergencyAttachVO extends CommonFeignEntity {
    public EmergencyAttachVO() {
    }
    public EmergencyAttachVO(String businessCode, Integer businessType,List<String> businessCodeList) {
        this.businessCode = businessCode;
        this.businessType = businessType;
        this.businessCodeList = businessCodeList;
    }

    /**
     * 主键ID
     */
    private Integer id;

    /**
     * 事件编码
     */
    private String businessCode;

    /**
     * 业务类型 1：应急事件 2：任务 3：反馈
     */
    private Integer businessType;

    /**
     * 文件路径
     */
    private String originUrl;

    /**
     * 文件ID
     */
    private String fileId;

    /**
     * 扩展名
     */
    private String fileSuffix;

    /**
     * 附件类型 1是图片 2是视频 3是文件 4是音频
     */
    private Integer recordType;

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 文件大小
     */
    private String fileSize;

    /**
     * 事件编码集合
     */
    private List<String> businessCodeList;
}
