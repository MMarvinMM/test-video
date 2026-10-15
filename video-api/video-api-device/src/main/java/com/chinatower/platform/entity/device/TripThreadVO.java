package com.chinatower.platform.entity.device;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TripThreadVO {

    //绊线唯一标识
    private String threadId;

    //警戒线
    private List<ThreadLineVO> threadLine;

    //告警方向
    private List<DirectionVO> direction;

    /**
     * 场景编码 逗号分隔
     */
    private String sceneCode;

    /**
     * 是否回显 0:否
     */
    private String isShow;
}
