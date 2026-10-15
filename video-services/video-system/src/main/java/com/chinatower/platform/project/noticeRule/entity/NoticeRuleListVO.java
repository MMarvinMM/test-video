package com.chinatower.platform.project.noticeRule.entity;

import lombok.Data;

import java.util.List;

@Data
public class NoticeRuleListVO {

    private List<NoticeRuleVO> list;


    private String isNotice;

}
