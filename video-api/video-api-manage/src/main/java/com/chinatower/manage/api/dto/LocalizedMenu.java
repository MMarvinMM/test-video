package com.chinatower.manage.api.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 属地版本管理实体
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LocalizedMenu {

    private static final long serialVersionUID = 1L;
    /**
     * 新增或编辑时传add或modify,查看传detail
     */
    private String optType;

    /**
     * 前端传的选中菜单列表
     */
    private List<Long> checkedMenuIdList;


    /**
     * 是否选中
     */
    private String isChecked;

    /**
     * 属地版本号id
     */
    private String localizedVersionId;


    /** 菜单ID */
    private Long menuId;


    /** 真正的菜单ID */
    private Long generateMenuId;

    /** 真正的父菜单ID */
    private Long generateParentId;

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
    private List<LocalizedMenu> children = new ArrayList<LocalizedMenu>();

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



    private String id;

    private String pid;

    private String label;

    private String applyType;

    private String creator;
    private String createBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    private String updateBy;
    private String modifier;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    private String dataScope;

//    private Long userId;

    private String appVerCode;

    /** 开始时间 */
    private String beginTime;

    /** 结束时间 */
    private String endTime;

    /**
     * 比对结果 1：新增
     */
    private String compareResult;

    private String defaultPort;

    private String updatePort;



}
