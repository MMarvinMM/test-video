package com.chinatower.manage.dataobject;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 视图模板实体
 * @author chmod
 * @version 1.0
 * @date 2022/1/13 17:38
 */
@Data
public class SysIndustryView implements Serializable {
    private static final long serialVersionUID = -1345675228071712577L;
    /** 主键 */
    private Long industryViewId;
    private Long viewId;
    /**
     * 大屏类型 0-应用大屏 1-第三方大屏
     */
    private Integer viewType;

    /**
     * 大屏类型 1-旧大屏 2-新大屏
     */
    private Integer industryViewType;

    /**
     * 新大屏组件类型 1-iframe 2-通用内部
     */
    private Integer screenCompoType;

    /**
     * 大屏排序
     */
    private Integer sort;

    /**
     * 第三方大屏地址
     */
    private String screenUrl;
    /**
     * 第三方大屏类型 1-通用大屏 2-数字孪生大屏
     */
    private Integer screenType;

    /**
     * 大屏打开方式：0-在原页面打开 1-在新页面打开
     */
    private Integer displayType;
    /**
     * 单点登录标识 0-非单点登录 1-单点登录
     */
    private Integer ssoFlag;
    /** 视图名称 */
    private String viewName;
    /** 视图所属目录ID */
    private Long pageId;
    private Long dirId;
    /** 视图样式JSON串 */
    private String viewJson;
    /** 组件ID集合，多个以,分隔 */
    private String compoList;
    /** 组件ID集合 */
    private List<String> compoLists;
    /** 封面图片URL */
    private String imgUrl;
    /** 创建时间 */
    private Date createTime;
    /** 修改时间 */
    private Date updateTime;
    /** 状态：1生效 0失效 */
    private String status;
    /** 逻辑删除 0正常1删除 */
    private String deleteFlag;
    /** 创建人 */
    private String creator;
    /** 修改人 */
    private String modifier;
    /** 设置底图JSON*/
    private String baseMapJson;

    /**
     * 分组id(前端生成)
     */
    private String groupId;


    /**
     * 大屏属性：1.菜单 2.分组
     */
    private String attr;

    /**
     * tab名称
     */
    private String tabName;
}
