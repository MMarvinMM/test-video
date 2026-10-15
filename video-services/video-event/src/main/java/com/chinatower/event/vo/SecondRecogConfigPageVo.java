package com.chinatower.event.vo;

import com.chinatower.common.core.dto.PageDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SecondRecogConfigPageVo extends PageDTO {

    /**
     * 二次识别配置名称
     */
    private String secondRecogName;

    /**
     * 创建人
     */
    private String creator;

    /**
     * 创建开始时间
     */
    private String createStartTime;
    /**
     * 创建结束时间
     */
    private String createEndTime;

    /**
     * 修改人
     */
    private String modifier;

    /**
     * 修改开始时间
     */
    private String updateStartTime;

    /**
     * 修改结束时间
     */
    private String updateEndTime;

    /**
     * 客户名称/ID
     */
    private String tenantId;

    /**
     * 创建时间排序
     */
    private String createDateOrder;

    /**
     * 修改时间排序
     */
    private String updateDateOrder;
}
