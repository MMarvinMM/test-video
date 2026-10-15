package com.chinatower.emergency.dto;

import com.chinatower.platform.entity.admin.SysUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DigitalTask implements Serializable {

    private String taskId;

    private String digitalId;

    private String planId;

    private String stageId;

    private String taskName;

    /**
     * 内容
     */
    private String content;

    /**
     * 负责人
     */
    private String userIds;

    private List<SysUser> users;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 创建人
     */
    private String creator;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 修改人
     */
    private String modifier;

    private String tenantId;

    private String industryCode;

    /**
     * 删除标记，0：正常，1：删除
     */
    private Integer deleteFlag;

    private List<String> resourceList;

}
