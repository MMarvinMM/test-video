package com.chinatower.platform.res;

import lombok.Data;

/**
 * @desription: 登录端统计
 * @data 创建时间：2023/3/28 16:06
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
public class ClientTypeRes {
    //日期
    private String date;
    //登录端
    private String clientType;
    //登录端描述
    private String clientTypeRemark;
    //数量
    private Long count;
}
