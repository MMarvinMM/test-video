package com.chinatower.system.api.model;

import lombok.Data;

import java.util.List;

/**
 * @author chuan hao
 * @version 1.0
 * @desription: 查询扩展信息
 * @data 创建时间：2023/8/21 16:45
 */
@Data
public class NoticeExtendVO {
    /**
     * 星期 1、2、3、4、5、6、7
     * @required false
     */
    private List<String> dayNo;

    /**
     * 消息提示音
     * @required false
     */
    private String sound;

    /**
     * 是否稍后提醒 0:否 1:是
     * @required false
     */
    private String isDelay;

    /**
     * 间隔时间，单位：分钟
     * @required false
     */
    private String delayTime;

    /**
     * 生效时间列表
     * @required false
     */
    private List<NoticeExtendTimeVO> timeList;
}
