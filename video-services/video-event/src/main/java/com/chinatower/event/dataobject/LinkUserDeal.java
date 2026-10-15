package com.chinatower.event.dataobject;

import lombok.Data;

import java.io.Serializable;

/**
 * 流程环节用户处理表
 * @author chmod
 * @version 1.0
 * @date 2022/2/14 11:18
 */
@Data
public class LinkUserDeal implements Serializable {
    /** 主键 */
    private Long id;
    /** 告警ID */
    private String warningOrderId;
    /** 处理环节 */
    private Long linkId;
    /** 用户ID */
    private String userId;
    /** 1结束 0未结束 */
    private String status;
}
