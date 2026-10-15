package com.chinatower.bdm.api.entity;


import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Treeselect树结构实体类
 *
 * @author iwhalecloud
 */
public class ResourceTreeSelect implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 节点ID */
    private String label;

    /** 节点ID */
    private String id;

    /** 节点ID */
    private String resourceTypeId;

    /** 节点名称 */
    private String resourceTypeName;

    /** 父节点ID */
    private String pid;

    /** 归属部门id */
    private String dept;

    /** 图片地址 */
    private String icon;

    /** 资源属性 */
    private String resourceProperty;

    /** 子节点 */
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<ResourceTreeSelect> children;

    public ResourceTreeSelect() {

    }
    public ResourceTreeSelect(String id, String label, String pid) {
        this.resourceTypeId = id;
        this.id = id;
        this.label = label;
        this.resourceTypeName = label;
        this.pid = pid;
    }

    public ResourceTreeSelect(String id, String label, String pid, String dept) {
        this.resourceTypeId=id;
        this.resourceTypeName = label;
        this.id = id;
        this.label = label;
        this.pid = pid;
        this.dept = dept;
    }

    public ResourceTreeSelect(ResourceType resourceType) {
        this.resourceTypeId = resourceType.getResourceTypeId();
        this.resourceTypeName = resourceType.getResourceTypeName();
        this.id = resourceType.getResourceTypeId();
        this.label = resourceType.getResourceTypeName();
        this.pid = resourceType.getParentId();
        this.icon=resourceType.getIcon();
        this.resourceProperty = resourceType.getResourceProperty();//资源管理优化需求 add by liuxt
        this.children = resourceType.getChildren().stream().map(ResourceTreeSelect::new).collect(Collectors.toList());
    }



    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public List<ResourceTreeSelect> getChildren()
    {
        return children;
    }

    public void setChildren(List<ResourceTreeSelect> children)
    {
        this.children = children;
    }

    public String getDept() {
        return dept;
    }

    public void setDept(String dept) {
        this.dept = dept;
    }

    public String getResourceTypeId() {
        return resourceTypeId;
    }

    public void setResourceTypeId(String resourceTypeId) {
        this.resourceTypeId = resourceTypeId;
    }

    public String getResourceTypeName() {
        return resourceTypeName;
    }

    public void setResourceTypeName(String resourceTypeName) {
        this.resourceTypeName = resourceTypeName;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getResourceProperty() {
        return resourceProperty;
    }

    public void setResourceProperty(String resourceProperty) {
        this.resourceProperty = resourceProperty;
    }

}
