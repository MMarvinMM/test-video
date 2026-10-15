package com.chinatower.platform.common.utils;


import com.chinatower.platform.project.system.domain.SysMenu;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Treeselect树结构实体类
 *
 * @author iwhalecloud
 */
public class SysTreeSelect implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 节点ID */
    private String id;

    /** 节点名称 */
    private String label;

    /** 父节点ID */
    private String pid;

    /** 归属部门id */
    private String dept;

    /**适用范围（平台标识）(0-通用菜单,1-PC端菜单,2-APP端菜单)*/
    private String applyType;

    /** 子节点 */
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<SysTreeSelect> children;

    public SysTreeSelect() {

    }
    public SysTreeSelect(String id, String label, String pid,String applyType) {
        this.id = id;
        this.label = label;
        this.pid = pid;
        this.applyType = applyType;
    }

    public SysTreeSelect(String id, String label, String pid, String dept,String applyType) {
        this.id = id;
        this.label = label;
        this.pid = pid;
        this.dept = dept;
        this.applyType = applyType;
    }

    public SysTreeSelect(SysMenu menu) {
        this.applyType = menu.getMenuApply();
        this.id = menu.getMenuId().toString();
        this.label = menu.getMenuName();
        this.pid = menu.getParentId().toString();
        this.children = menu.getChildren().stream().map(SysTreeSelect::new).collect(Collectors.toList());
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getLabel()
    {
        return label;
    }

    public void setLabel(String label)
    {
        this.label = label;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public List<SysTreeSelect> getChildren()
    {
        return children;
    }

    public void setChildren(List<SysTreeSelect> children)
    {
        this.children = children;
    }

    public String getDept() {
        return dept;
    }

    public void setDept(String dept) {
        this.dept = dept;
    }

    public String getApplyType() {
        return applyType;
    }

    public void setApplyType(String applyType) {
        this.applyType = applyType;
    }
}
