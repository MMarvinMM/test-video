package com.chinatower.event.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * @ClassName: 大屏数据配置数据
 * @Author: wangyt
 * @CreateTime: 2025-01-07  10:34
 * @Description: TODO
 * @Version: 1.0
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ScreenDataConfigParticipationDTO {
    private String dataType;
    private String code;
    private String source;
}
