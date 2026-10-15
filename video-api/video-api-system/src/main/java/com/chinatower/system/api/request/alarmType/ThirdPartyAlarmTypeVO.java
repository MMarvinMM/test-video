package com.chinatower.system.api.request.alarmType;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * @author liuxt
 * @date 2024/01/06 15:30
 * @description 第三方告警类型同步实体
 */
@Data
@ToString
public class ThirdPartyAlarmTypeVO {

        @NotBlank(message = "客户id不能为空")
        private String tenantId;
        @NotBlank(message = "行业编码不能为空")
        private String industryCode;
        @NotBlank(message = "行业版本编码不能为空")
        private String appVerCode;

        private List<String> addAlarmTypeList;

        private List<String> deleteAlarmTypeList;

}
