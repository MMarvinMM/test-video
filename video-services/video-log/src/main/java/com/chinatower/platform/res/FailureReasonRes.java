package com.chinatower.platform.res;

import lombok.Data;

/**
 * @desription: 失败原因统计
 * @data 创建时间：2023/3/29 13:58
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
public class FailureReasonRes {
    /**
     * 失败原因
     */
    private String msg;
    /**
     * 次数
     */
    private Long count;
}
