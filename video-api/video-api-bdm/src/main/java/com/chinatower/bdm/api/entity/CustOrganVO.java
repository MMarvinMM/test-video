package com.chinatower.bdm.api.entity;

import lombok.Data;
import lombok.ToString;
import org.apache.ibatis.type.Alias;

import java.io.Serializable;

@Data
@ToString
@Alias("CustOrganVOBdm")
public class CustOrganVO implements Serializable {
    /**
     * 组织机构信息表
     */
    private static final long serialVersionUID = 1L;
    private String deptId;
    private String deptName;
    private String parentId;

}
