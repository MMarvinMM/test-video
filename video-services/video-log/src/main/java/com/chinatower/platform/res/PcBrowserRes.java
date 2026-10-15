package com.chinatower.platform.res;

import lombok.Data;

/**
 * @desription: PC浏览器统计
 * @data 创建时间：2023/3/29 9:45
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
public class PcBrowserRes {
    /**
     * 浏览器类型
     */
    private String browser;
    /**
     * 登录次数
     */
    private Long count;
}
