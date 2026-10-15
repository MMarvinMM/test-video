package com.chinatower.platform.project.system.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.chinatower.platform.framework.web.domain.SystemBaseEntity;

/**
 * 菜单权限表 sys_menu
 *
 * @author iwhalecloud
 */
public class SysMenu extends SystemBaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 菜单ID */
    private Long menuId;

    //app上传图片地址
    private String fileUrl;
    
    private String fileUrlDe;
    
    private String fileUrlObs;

    /** 菜单名称 */
    private String menuName;

    /** 父菜单名称 */
    private String parentName;

    /** 父菜单ID */
    private Long parentId;

    /** 显示顺序 */
    private String orderNum;

    /** 路由地址（访问路径） */
    private String path;

    /** 组件路径 */
    private String component;

    /** 是否为外链（0是 1否） */
    private String isFrame;

    /** 菜单类型（M目录 C菜单 F按钮） */
    private String menuType;

    /** 菜单状态:0显示,1隐藏 */
    private String visible;

    /** 菜单状态：1启用,0停用 */
    private String status;

    /**菜单归属（菜单权限）(0-铁塔菜单,1-租户菜单)*/
    private String menuClassify;

    /**菜单归属（菜单权限）(0-铁塔菜单,1-租户菜单)*/
    private String menuClassifyEq;

    private List<String> menuClassifyList;
    /**适用范围（平台标识）(0-通用菜单,1-PC端菜单,2-APP端菜单)*/
    private String menuApply;

    /** 权限字符串 */
    private String perms;
    //资源URL
    private String permUrl;

    /** 菜单图标 */
    private String icon;

    /** 备注 */
    private String remark;

    /** 行业编码 */
    private String industryCode;

    private String industryName;

    /** 子菜单 */
    private List<SysMenu> children = new ArrayList<SysMenu>();

    private Integer pageSize;

    private Integer pageNum;

    private String paramMenuClassify;

    private String paramMenuName;

    private String paramVisible;

    private String paramMenuApply;

    private List<Long> parentIds;

    private Integer notInFlag;

    // 排序规则
    private String sortRule;
    private String acceptNode;

    /** 菜单ID列表 */
    private List<Long> menuIdList;

    private String classifyName;

    private List<String> roleIdList;

    private List<String> idList;

    /** 用户ID */
    private String userId;

    private String id;

    private String pid;

    private String label;

    private String applyType;

    public Long getMenuId()
    {
        return menuId;
    }

    public void setMenuId(Long menuId)
    {
        this.menuId = menuId;
    }

    @NotBlank(message = "菜单名称不能为空")
    @Size(min = 0, max = 50, message = "菜单名称长度不能超过50个字符")
    public String getMenuName()
    {
        return menuName;
    }

    public void setMenuName(String menuName)
    {
        this.menuName = menuName;
    }

    public String getParentName()
    {
        return parentName;
    }

    public void setParentName(String parentName)
    {
        this.parentName = parentName;
    }

    public Long getParentId()
    {
        return parentId;
    }

    public void setParentId(Long parentId)
    {
        this.parentId = parentId;
    }

    public String getOrderNum()
    {
        return orderNum;
    }

    public void setOrderNum(String orderNum)
    {
        this.orderNum = orderNum;
    }

    @Size(min = 0, max = 200, message = "路由地址不能超过200个字符")
    public String getPath()
    {
        return path;
    }

    public void setPath(String path)
    {
        this.path = path;
    }

    @Size(min = 0, max = 200, message = "组件路径不能超过255个字符")
    public String getComponent()
    {
        return component;
    }

    public void setComponent(String component)
    {
        this.component = component;
    }

    public String getIsFrame()
    {
        return isFrame;
    }

    public void setIsFrame(String isFrame)
    {
        this.isFrame = isFrame;
    }

    @NotBlank(message = "菜单类型不能为空")
    public String getMenuType()
    {
        return menuType;
    }

    public void setMenuType(String menuType)
    {
        this.menuType = menuType;
    }

    public String getVisible()
    {
        return visible;
    }

    public void setVisible(String visible)
    {
        this.visible = visible;
    }

    @Size(min = 0, max = 100, message = "权限标识长度不能超过100个字符")
    public String getPerms()
    {
        return perms;
    }

    public void setPerms(String perms)
    {
        this.perms = perms;
    }

    public String getIcon()
    {
        return icon;
    }

    public void setIcon(String icon)
    {
        this.icon = icon;
    }

    public List<SysMenu> getChildren()
    {
        return children;
    }

    public void setChildren(List<SysMenu> children)
    {
        this.children = children;
    }

    public String getRemark()
    {
        return remark;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }


    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getMenuApply() {
        return menuApply;
    }

    public void setMenuApply(String menuApply) {
        this.menuApply = menuApply;
    }

    public String getMenuClassify() {
        return menuClassify;
    }

    public void setMenuClassify(String menuClassify) {
        this.menuClassify = menuClassify;
    }

    public String getPermUrl() {
        return permUrl;
    }

    public void setPermUrl(String permUrl) {
        this.permUrl = permUrl;
    }


    public String getSortRule() {
        return sortRule;
    }

    public void setSortRule(String sortRule) {
        this.sortRule = sortRule;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("menuId", getMenuId())
            .append("menuName", getMenuName())
            .append("parentId", getParentId())
            .append("orderNum", getOrderNum())
            .append("path", getPath())
            .append("component", getComponent())
            .append("isFrame", getIsFrame())
            .append("menuType", getMenuType())
            .append("visible", getVisible())
            .append("perms", getPerms())
            .append("icon", getIcon())
            .append("creator", getCreator())
            .append("createTime", getCreateTime())
            .append("modifier", getModifier())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }

    public String getIndustryCode() {
        return industryCode;
    }

    public void setIndustryCode(String industryCode) {
        this.industryCode = industryCode;
    }

    public List<String> getMenuClassifyList() {
        if(StringUtils.isNotBlank(menuClassify)){
            String[] split = menuClassify.split(",");
            menuClassifyList = Arrays.asList(split);
        }
        return menuClassifyList;
    }

    public String getMenuClassifyEq() {
        return menuClassifyEq;
    }

    public void setMenuClassifyEq(String menuClassifyEq) {
        this.menuClassifyEq = menuClassifyEq;
    }

    public void setMenuClassifyList(List<String> menuClassifyList) {
        this.menuClassifyList = menuClassifyList;
    }

    public String getParamMenuClassify() {
        return paramMenuClassify;
    }

    public void setParamMenuClassify(String paramMenuClassify) {
        this.paramMenuClassify = paramMenuClassify;
    }

    public String getParamMenuName() {
        return paramMenuName;
    }

    public void setParamMenuName(String paramMenuName) {
        this.paramMenuName = paramMenuName;
    }

    public String getParamVisible() {
        return paramVisible;
    }

    public void setParamVisible(String paramVisible) {
        this.paramVisible = paramVisible;
    }

    public String getParamMenuApply() {
        return paramMenuApply;
    }

    public void setParamMenuApply(String paramMenuApply) {
        this.paramMenuApply = paramMenuApply;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

	public String getFileUrlDe() {
		return fileUrlDe;
	}

	public void setFileUrlDe(String fileUrlDe) {
		this.fileUrlDe = fileUrlDe;
	}

	public String getFileUrlObs() {
		return fileUrlObs;
	}

	public void setFileUrlObs(String fileUrlObs) {
		this.fileUrlObs = fileUrlObs;
	}

    public String getIndustryName() {
        return industryName;
    }

    public void setIndustryName(String industryName) {
        this.industryName = industryName;
    }

    public Integer getNotInFlag() {
        return notInFlag;
    }

    public void setNotInFlag(Integer notInFlag) {
        this.notInFlag = notInFlag;
    }

    public List<Long> getParentIds() {
        return parentIds;
    }

    public void setParentIds(List<Long> parentIds) {
        this.parentIds = parentIds;
    }
    public String getAcceptNode() {
        return acceptNode;
    }

    public void setAcceptNode(String acceptNode) {
        this.acceptNode = acceptNode;
    }

    public List<Long> getMenuIdList() {
        return menuIdList;
    }

    public void setMenuIdList(List<Long> menuIdList) {
        this.menuIdList = menuIdList;
    }

    public String getClassifyName() {
        return classifyName;
    }

    public void setClassifyName(String classifyName) {
        this.classifyName = classifyName;
    }

    public List<String> getRoleIdList() {
        return roleIdList;
    }

    public void setRoleIdList(List<String> roleIdList) {
        this.roleIdList = roleIdList;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getApplyType() {
        return applyType;
    }

    public void setApplyType(String applyType) {
        this.applyType = applyType;
    }

    public List<String> getIdList() {
        return idList;
    }

    public void setIdList(List<String> idList) {
        this.idList = idList;
    }
}
