package com.chinatower.system.api.model;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * @desription: 用户推送
 * @data 创建时间：2023/8/22 9:35
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
@ToString
public class UserPushVO implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 用户编码
     */
    private String userCode;//用户编码
    /**
     * 姓名
     */
    private String userName;//姓名
    /**
     * 组织机构编码
     */
    private String orgCode;//组织机构编码
    /**
     * 创建时间
     */
    private String creatorDate;//创建时间
    /**
     * 1:新增；2:修改；3：删除
     */
    private String flag ;//1:新增；2:修改；3：删除；

    /**
     * 手机号
     */
    private String mobile;//手机号
}