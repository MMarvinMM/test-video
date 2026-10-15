package com.chinatower.system.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;
/**
 * @desription: 部门区域
 * @data 创建时间：2023/8/22 9:47
 * @author chuan hao
 * @version 1.0
 *
 */
@Data
public class SysDeptAreaVO {
    /**
     * 数据ID
     */
    private String id;
    /**
     * 标签
     */
    private String label;
    private String pid;
    /**
     * 区域编码
     */
    private String areaName;
    /**
     * 区域等级
     */
    private String areaLevel;
    /**
     * 下级区域
     */
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<SysDeptAreaVO> children;
    /**
     * 租户ID
     */
    private String tenantId;
    /**
     * 部门等级
     */
    private Integer deptLevel;

    public SysDeptAreaVO(){

    }

    public SysDeptAreaVO(String id, String areaName, String pid) {
        this.id = id;
        this.pid = pid;
        this.areaName = areaName;
    }

    public SysDeptAreaVO(String id, String areaName,String areaLevel, String pid,List<SysDeptAreaVO> children) {
        this.id = id;
        this.pid = pid;
        this.areaName = areaName;
        this.children = children;
        this.areaLevel = areaLevel;
    }

}
