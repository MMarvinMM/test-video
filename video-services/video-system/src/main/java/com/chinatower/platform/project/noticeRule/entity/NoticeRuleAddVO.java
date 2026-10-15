package com.chinatower.platform.project.noticeRule.entity;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import com.chinatower.system.api.model.NoticeExtendVO;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
public class NoticeRuleAddVO extends BaseDataScopeEntity {

    /**
     * 唯一标识
     */
    private Long id;

    /**
     * 规则名称
     */
    @NotBlank(message = "规则名称不能为空")
    private String ruleName;

    /**
     * 是否开启消息通知 0:关闭 1:开启
     */
    private String isNotice;

    /**
     * 告警类型
     */
    @NotEmpty(message = "告警来源/告警类型不能为空")
    private List<NoticeAlarmSourceVO> alarmInfo;

    /**
     * 扩展信息
     */
    @NotEmpty(message = "失效时间不能为空")
    private List<NoticeExtendVO> extendInfo;

}
