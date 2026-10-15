package com.chinatower.platform.project.noticeRule.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chinatower.platform.project.noticeRule.entity.NoticeRuleDO;
import com.chinatower.platform.project.noticeRule.entity.NoticeRuleVO;
import com.chinatower.system.api.model.NoticeRuleFilterVO;

import java.util.List;

/**
 * 消息通知规则 数据层
 *
 * @author chinatower
 */
public interface SysNoticeRuleMapper extends BaseMapper<NoticeRuleDO>
{
    /**
     * 更新是否通知消息开关
     *
     * @param noticeRuleVO
     */
    int updateIsNotice(NoticeRuleVO noticeRuleVO);

    /**
     * 批量更新
     * @param list
     * @return
     */
    int updateRuleBatch(List<NoticeRuleDO> list);

    List<NoticeRuleDO> selectRuleBatchList(List<NoticeRuleFilterVO> list);

}
