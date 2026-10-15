package com.chinatower.platform.entity.device;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 巡航计划
 * Created by zheng.yan on 2022/1/22.
 */
@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BatchSttPatrolReq extends DeviceSouthCommonDTO {
    
    @NotEmpty(message ="巡航计划列表不能为空")
    private List<SttPatrolSimpleReq> patrols;
}
