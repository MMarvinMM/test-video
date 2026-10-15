package com.chinatower.platform.modules.alarm.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @className: DeptDataScopeVO
 * @description: 组织数据权限标识
 * @author: yongqi.han
 * @date: 2022/10/18
 * @requestID 需求单号及需求名称
 */
@Data
public class DeptDataScopeVO  implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 用户唯一id
     */
    private String userId;
    /**
     * 是否是所有数据权限 1所有数据权限
     */
    private String dataScope;
    /**
     * 摄像机列表拼接串
     */
    private String deviceCodeStr;
    /**
     * 区域列表拼接串
     */
    private String regionCodeStr;


}
