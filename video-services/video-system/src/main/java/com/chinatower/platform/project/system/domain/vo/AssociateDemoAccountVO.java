package com.chinatower.platform.project.system.domain.vo;

import com.chinatower.common.core.dto.PageDTO;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Name AssociateDemoAccountVO
 * @Description 关联演示账号VO
 */
@Data
public class AssociateDemoAccountVO extends PageDTO implements Serializable {

    private String id;

    private String tenantId;

    private String tenantName;

    private String creator;

    private String modifier;

    /**
     * 创建时间开始日期
     */
    private String createTimeStart;

    /**
     * 创建时间结束日期
     */
    private String createTimeEnd;

    /**
     * 修改时间开始日期
     */
    private String updateTimeStart;

    /**
     * 修改时间结束日期
     */
    private String updateTimeEnd;

    /**
     * 关联方式：1 按组织 2 按用户
     */
    private String associateMethod;

    private String deptId;

    private String userName;

    private String nickName;

    private String deptName;

    private List<String> deptIdList;

    private List<String> userIdList;

    private List<String> associateIdList;

    private String associateId;

    private String editId;

}