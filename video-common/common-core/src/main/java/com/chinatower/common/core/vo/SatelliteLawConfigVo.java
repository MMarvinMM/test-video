package com.chinatower.common.core.vo;

import com.chinatower.common.core.dto.PageDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SatelliteLawConfigVo extends PageDTO {
    /**
     * 配置id
     */
    private String id;

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 自定义文件名称
     */
    private String customName;

    /**
     * 告警类型ID
     */
    private String alarmTypeId;

    /**
     * 告警类型名称
     */
    private String alarmTypeName;

    /**
     * 导入文件路径
     */
    private String fileUrl;

    /**
     * 图层模版ID
     */
    private String layerTemplateId;

    /**
     * 图层模版ID集合
     */
    private List<String> layerTemplateIdList;

    /**
     * 图层模版名称
     */
    private String layerTemplateName;

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
     * 创建时间排序
     */
    private String createDateOrder;

    /**
     * 修改时间排序
     */
    private String updateDateOrder;

    /** 租户ID */
    private String tenantId;

    /** 租户名称 */
    private String tenantName;

    /**
     * 行业编码
     */
    private String industryCode;

    /**
     * 行业名称
     */
    private String industryName;

    /**
     * 行业版本编码
     */
    private String appVerCode;

    private String acceptNode;

    /**
     * 是否生产告警
     */
    private String isBuildAlarm;

}
