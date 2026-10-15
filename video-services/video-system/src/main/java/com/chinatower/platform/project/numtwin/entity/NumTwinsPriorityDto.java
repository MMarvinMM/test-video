package com.chinatower.platform.project.numtwin.entity;

import lombok.Data;

import java.util.Date;

/**
 * @Author: songxinfeng
 * @Date: 2025/9/12 10:30
 */

@Data
public class NumTwinsPriorityDto {

    /**
     * 主键id
     */
    private Long id;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 优先级
     */
    private String level;

    /**
     * 数字孪生配置id
     */
    private Long configId;

    /**
     * 创建人
     */
    private String createUser;

    /**
     * 修改人
     */
    private String updateUser;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;

    /**
     * 删除标记位
     */
    private String deleteFlag;

    private String userName;

    private String nickName;
}
