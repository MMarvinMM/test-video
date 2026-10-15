package com.chinatower.system.api.model;

import lombok.Data;

import java.util.List;

/**
 * @author chuan hao
 * @version 1.0
 * @desription: 公告查询信息
 * @data 创建时间：2023/8/21 16:44
 */
@Data
public class NoticeRuleFilterResultVO extends NoticeRuleFilterVO {
    /**
     * 推送类型 0:消息类 1:通知类
     * @required false
     */
    private String msgType;

    /**
     * 扩展信息
     * @required false
     */
    private List<NoticeExtendVO> noticeExtendVO;
}
