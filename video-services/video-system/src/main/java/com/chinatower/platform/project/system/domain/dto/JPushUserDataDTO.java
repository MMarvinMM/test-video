package com.chinatower.platform.project.system.domain.dto;

import lombok.Data;

/**
 * @desription: 推送用户查询开关
 * @data 创建时间：2023/5/6 9:26
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
public class JPushUserDataDTO {
    /**
     * 用户ID
     */
    private String userId;
    /**
     * 行业
     */
    private String industryCode;
    /**
     * 消息类型
     */
    private String msgType;
}
