package com.chinatower.system.api.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

/**
 * @desription: 部门
 * @data 创建时间：2023/8/22 10:03
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
public class DeptVO implements Serializable {
    /**
     * 部门ID
     */
    private String deptId;

    /**
     * 上级部门
     */
    private String parentdeptId;

    /**
     * 部门名称
     */
    private String deptlName;

    public DeptVO(String deptId,String parentdeptId,String deptlName){
        this.deptId = deptId;
        this.parentdeptId = parentdeptId;
        this.deptlName = deptlName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeptVO d = (DeptVO) o;
        return d.getDeptId().equalsIgnoreCase(deptId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(deptId);
    }
}
