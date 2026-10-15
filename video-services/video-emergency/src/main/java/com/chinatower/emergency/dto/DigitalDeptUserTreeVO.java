package com.chinatower.emergency.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DigitalDeptUserTreeVO implements Serializable {

    private String id;

    private String deptId;

    private String emergencyDeptName;

    private Integer deptLevel;

    private String ancestors;

    private String parentId;

    private List<DigitalDeptUserTreeVO> children;

    private List<String> userList;

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

    /**
     * 租户ID
     */
    private String tenantId;

    /**
     * 行业编码
     */
    private String industryCode;

    private String appVerCode;

    private String eventCode;
}
