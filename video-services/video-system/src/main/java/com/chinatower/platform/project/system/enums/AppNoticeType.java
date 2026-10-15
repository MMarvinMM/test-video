package com.chinatower.platform.project.system.enums;

import lombok.Getter;

/**
 * @desription: 公告类型
 * @data 创建时间：2023/5/5 16:01
 * @author chuan hao
 * @version 1.0
 *
 */
@Getter
public enum AppNoticeType {
    WEB_100("WEB_100","通知公告","3"),
    WEB_101("WEB_101","行业资讯","2");
    /**
     * 数据库数据
     */
    private String noticeType;
    /**
     * 描述
     */
    private String desc;
    private String type;

    AppNoticeType(String noticeType,String desc,String type){
        this.noticeType = noticeType;
        this.desc = desc;
        this.type = type;
    }

    public static AppNoticeType get(String noticeType){
        for (AppNoticeType enums : AppNoticeType.values()){
            if (enums.getNoticeType().equals(noticeType)){
                return enums;
            }
        }
        return null;
    }
}
