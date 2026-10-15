package com.chinatower.event.entity.dto;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 附件表
 * </p>
 *
 * @author AutoGen
 * @since 2022-04-27
 */
@Getter
@Setter
@TableName("vm_order_file_t")
public class OrderFileDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 工单流程ID
     */
    private String orderFileId;

    /**
     * 告警单ID(yyyyMM+租户id+)
     */
    private String warningOrderId;

    /**
     * 工单流程ID
     */
    private String orderFlowId;

    /**
     * 处理单ID
     */
    private String processOrderId;

    /**
     * 环节ID
     */
    private String linkId;

    /**
     * 资源ID
     */
    private String resourceId;

    /**
     * 资源url
     */
    private String resourceUrl;

    /**
     * 1:图片 2:视频 3:文件
     */
    private String resourceType;

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 文件大小
     */
    private String fileSize;

    /**
     * 图片水印信息(json字符串)
     */
    private String fileWaterMark;

    /**
     * 删除标识 0正常 1删除
     */
    private String deleteFlag;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 创建时间
     */
    private Date createDate;

    /**
     * 修改人
     */
    private String modifierBy;

    /**
     * 最后更新时间
     */
    private Date modifierDate;

    /**
     * 备注
     */
    private String remark;

    /**
     * 处理人ID
     */
    private String processId;

    /**
     * 处理人名称
     */
    private String processName;
}
