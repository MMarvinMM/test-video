package com.chinatower.event.dto;

import com.chinatower.common.core.dto.SendAlarmOrderDTO;
import com.chinatower.event.entity.dto.ModifyLableDTO;
import lombok.Data;

import java.util.List;

/**
 * @ClassName JudgeAlarmOrderDTO
 * @Deacription 告警研判入参实体
 * @Author lyy
 * @Date 2024/12/04 17:53
 * @Version v1.7.10
 **/
@Data
public class JudgeAlarmOrderDTO extends SendAlarmOrderDTO {

    /**
     * 标签集合
     */
    private List<ModifyLableDTO> lables;
}
