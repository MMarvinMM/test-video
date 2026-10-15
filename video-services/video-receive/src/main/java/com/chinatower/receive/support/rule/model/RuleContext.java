package com.chinatower.receive.support.rule.model;

import com.alibaba.fastjson.JSONObject;
import com.chinatower.platform.entity.admin.SysUser;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RuleContext {

    private SysUser sysUser;

    private Long aiTaskId;

    private Boolean gtFiveFlag;

    private JSONObject extendInfo = new JSONObject();

    public void putExtendInfo(String key, Object value) {
        if(extendInfo == null){
            extendInfo = new JSONObject();
        }
    	extendInfo.put(key, value);
    }

}
