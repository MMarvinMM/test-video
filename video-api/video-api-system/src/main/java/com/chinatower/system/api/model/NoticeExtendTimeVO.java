package com.chinatower.system.api.model;

import lombok.Data;

/**
 * @desription: 公告扩展时间
 * @data 创建时间：2023/8/22 9:55
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
public class NoticeExtendTimeVO {
    /**
     * 开始时间
     * @required false
     */
    private String startTime;

    /**
     * 结束时间
     * @required false
     */
    private String endTime;
}
