package com.chinatower.receive.vo.entity;

import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WakeUpTowerDTO {
    /**
     * 是否唤起小塔
     */
    private Boolean wakeUpResult;
    /**
     * 扩展字段
     */
    private JSONObject extendInfo;


}
