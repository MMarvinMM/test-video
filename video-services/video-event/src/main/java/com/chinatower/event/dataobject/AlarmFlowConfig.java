package com.chinatower.event.dataobject;

import java.io.Serializable;

import lombok.Data;

/**
 * 告警流程配置实体
 * @author chmod
 * @version 1.0
 * @date 2022/1/27 9:32
 */
@Data
public class AlarmFlowConfig extends AlarmFlowConfigDO implements Serializable {
    //组织名称
    private String orgName;
}
