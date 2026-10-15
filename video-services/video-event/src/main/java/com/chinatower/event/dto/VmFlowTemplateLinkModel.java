package com.chinatower.event.dto;

import com.chinatower.common.core.entity.BasePlatformEntity;
import lombok.Data;

import java.io.Serializable;

/**
 * @Description 告警流程环节实体对象
 * @Author guyuefei
 * @Date 2022/7/22
 * @Version release_v1.5
 **/
@Data
public class VmFlowTemplateLinkModel extends BasePlatformEntity implements Serializable {
    private static final long serialVersionUID = -4074803938124426158L;
    /** 主键 */
    private Long flowId;
    /** 模板ID */
    private Long templateId;
    /** 当前环节Id */
    private Integer curLink;
    /** 环节属性，1开始环节，2中间环节，3结束环节 */
    private String linkType;
    /** 上一个环节 */
    private String preLink;
    /** 下一个环节 */
    private String nextLink;
    /** 环节类型 */
    private Long realLinkType;
}
