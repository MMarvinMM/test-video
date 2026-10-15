package com.chinatower.platform.project.noticeRule.entity;

import com.chinatower.platform.framework.web.domain.SystemBaseEntity;

import com.chinatower.system.api.model.NoticeExtendVO;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @program:
 * @description: TODO
 * @author:
 * @create:
 */
@ToString
@Data
public class NoticeRuleVO extends SystemBaseEntity {

    private Long id;
    private String ruleName;
    private String ruleType;
    private String userId;
    private String industryCode;
    private String isNotice;
    private String alarmTypeText;
    private String extendInfoText;
    private String enableTimeText;
    private String deleteFlag;
    private List<NoticeExtendVO>  extendInfoList;
    private List<NoticeAlarmSourceVO> alarmInfoList;

}
