package com.chinatower.event.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 流程环节用户处理表
 * @author chmod
 * @version 1.0
 * @date 2022/2/14 11:18
 */
@Data
public class LinkUserDealModel implements Serializable {
    /** 主键 */
    private Long id;
    /** 告警ID */
    @NotEmpty(message = "告警ID不能为空")
    private String warningOrderId;
    /** 处理环节 */
    @NotNull(message = "处理环节不能为空")
    private Long linkId;
    /** 用户ID */
    @NotEmpty(message = "用户ID不能为空")
    private String userId;
    /** 1结束 0未结束 */
    private String status;
    /** 表名后缀*/
    @NotEmpty(message = "tableSuffix不能为空")
    private String tableSuffix;
}
