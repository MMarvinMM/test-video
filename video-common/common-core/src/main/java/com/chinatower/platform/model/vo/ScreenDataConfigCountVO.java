package com.chinatower.platform.model.vo;

import lombok.Data;

import java.util.List;

/**
 * @ClassName: ScreenDataConfigCountVO
 * @Author: wangyt
 * @CreateTime: 2025-01-08  15:57
 * @Description: TODO
 * @Version: 1.0
 */
@Data
public class ScreenDataConfigCountVO {
    private Long id;
    private String tenantId;
    private String industryCode;
    private String categoryCode;
    private List<String> alarmSources;
    private Boolean onlySatelliteFirePointFlag;
}
