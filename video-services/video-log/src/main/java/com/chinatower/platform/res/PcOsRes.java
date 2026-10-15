package com.chinatower.platform.res;

import lombok.Data;

/**
 * @desription: pc系统统计
 * @data 创建时间：2023/3/29 9:34
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
public class PcOsRes {
    /**
     * 系统类型
     */
    private String os;
    /**
     * 登录次数
     */
    private Long count;
}
