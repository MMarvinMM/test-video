package com.chinatower.bdm.api.entity;

import com.chinatower.common.entity.TreeSelect;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.util.List;

public class TreeSelectByBdm implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * id
     * @required true
     */
    private String id;
    /**
     * 名称
     * @required true
     */
    private String label;
    /**
     * 父级id
     * @required true
     */
    private String pid;
    /**
     * 子级
     * @required true
     */
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<TreeSelectByBdm> children;
    /**
     * 电话号码
     * @required true
     */
    private String tel;
    /**
     * 是否在线
     * @required true
     */
    private String isLine;
    /**
     * 网格员总数
     * @required true
     */
    private String allTotal;
    /**
     * 网格员在线数
     * @required true
     */
    private String lineTotal;

    private String icon;
    /**
     * 0 网格  1 网格长 2 网格员
     * @required true
     */
    private String type;
    /**
     * 中心点坐标
     * @required true
     */
    private String center;

    private String gridLevelCode;
    private String color;
    private String userId;
    private String userName;
    private String locationFlag;

    /**
     * 资源属性
     */
    private String resourceProperty;

    private Integer isMonitor;

    /**
     * 是否是应急资源 1：是 0：否
     */
    private String isEmergency;

    public Integer getIsMonitor() {
        return isMonitor;
    }

    public void setIsMonitor(Integer isMonitor) {
        this.isMonitor = isMonitor;
    }

    public TreeSelectByBdm(String id, String label, String pid, List<TreeSelectByBdm> children, String tel, String isLine, String allTotal, String lineTotal, String icon, String type, String center, String gridLevelCode, String color, String userId, String userName, String locationFlag, String resourceProperty, Integer isMonitor, String isEmergency) {
        this.id = id;
        this.label = label;
        this.pid = pid;
        this.children = children;
        this.tel = tel;
        this.isLine = isLine;
        this.allTotal = allTotal;
        this.lineTotal = lineTotal;
        this.icon = icon;
        this.type = type;
        this.center = center;
        this.gridLevelCode = gridLevelCode;
        this.color = color;
        this.userId = userId;
        this.userName = userName;
        this.locationFlag = locationFlag;
        this.resourceProperty = resourceProperty;
        this.isMonitor = isMonitor;
        this.isEmergency = isEmergency;
    }

    public TreeSelectByBdm() {

    }
    public TreeSelectByBdm(ResourceConfTree vo) {
        this.id = vo.getId();
        this.label = vo.getLabel();
        this.pid = vo.getPid();
        this.type = vo.getType();
        this.icon = vo.getIcon();
        this.resourceProperty = vo.getResourceProperty();
        this.isMonitor = vo.getIsMonitor();
        this.isEmergency = vo.getIsEmergency();
    }

    public TreeSelectByBdm(GridKeeperTree vo) {
        this.id = vo.getId();
        this.label = vo.getLabel();
        this.pid = vo.getPid();
        this.type = vo.getType();
        this.allTotal = vo.getAllTotal();
        this.lineTotal = vo.getLineTotal();
        this.isLine = vo.getIsLine();
        this.center = vo.getCenter();
        this.tel = vo.getTel();
        this.color=vo.getColor();
        this.userId=vo.getUserId();
        this.userName=vo.getUserName();
        this.locationFlag=vo.getLocationFlag();
    }


    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLabel() {
        return this.label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getPid() {
        return this.pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public List<TreeSelectByBdm> getChildren() {
        return this.children;
    }

    public void setChildren(List<TreeSelectByBdm> children) {
        this.children = children;
    }



    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getIsLine() {
        return isLine;
    }

    public void setIsLine(String isLine) {
        this.isLine = isLine;
    }



    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCenter() {
        return center;
    }

    public void setCenter(String center) {
        this.center = center;
    }

    public String getAllTotal() {
        return allTotal;
    }

    public void setAllTotal(String allTotal) {
        this.allTotal = allTotal;
    }

    public String getLineTotal() {
        return lineTotal;
    }

    public void setLineTotal(String lineTotal) {
        this.lineTotal = lineTotal;
    }

    public String getGridLevelCode() {
        return gridLevelCode;
    }

    public void setGridLevelCode(String gridLevelCode) {
        this.gridLevelCode = gridLevelCode;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getLocationFlag() {
        return locationFlag;
    }

    public void setLocationFlag(String locationFlag) {
        this.locationFlag = locationFlag;
    }

    public String getResourceProperty() {
        return resourceProperty;
    }

    public void setResourceProperty(String resourceProperty) {
        this.resourceProperty = resourceProperty;
    }

    public String getIsEmergency() {
        return isEmergency;
    }

    public void setIsEmergency(String isEmergency) {
        this.isEmergency = isEmergency;
    }
}
