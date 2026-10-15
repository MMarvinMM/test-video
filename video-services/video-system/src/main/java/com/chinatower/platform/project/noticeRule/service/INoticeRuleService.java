package com.chinatower.platform.project.noticeRule.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chinatower.common.entity.Result;
import com.chinatower.platform.project.noticeRule.entity.*;
import com.chinatower.system.api.model.NoticeRuleFilterResultVO;
import com.chinatower.system.api.model.NoticeRuleFilterVO;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 消息提醒规则表 服务类
 * </p>
 *
 * @author
 * @since 2023-02-01
 */
public interface INoticeRuleService extends IService<NoticeRuleDO> {

    Result getListByUserId(NoticeRuleVO req);

    int updateIsNotice(NoticeRuleVO noticeRuleVO);

    Result add(NoticeRuleAddVO noticeRuleAddVO);

    Result edit(NoticeRuleAddVO noticeRuleAddVO);

    List<NoticeRuleFilterResultVO> filterUserList(List<NoticeRuleFilterVO> noticeRuleFilterVOList);

    Result getAlarmType();

    Result getMaxRuleName();
}
