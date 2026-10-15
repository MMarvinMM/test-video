package com.chinatower.event.dataobject;

import lombok.Data;

import java.io.Serializable;

/**
 * 流程环节配置实体
 * @author chmod
 * @version 1.0
 * @date 2022/1/25 15:03
 */
@Data
public class FlowLinkAttr extends FlowLinkAttrDO implements Serializable {

    /** 当前环节ID */
    private Long linkId;
}
