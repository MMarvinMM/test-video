package com.chinatower.system.api.model;


import com.chinatower.common.core.entity.BaseDataScopeEntity;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * 角色表 sys_role_screen_ref
 *
 * @author liuxt
 */
@Data
@ToString
public class SysRoleScreenVO extends BaseDataScopeEntity
{
    private static final long serialVersionUID = 1L;

    /**
     * 角色id
     */
    private Long roleId;

    /**
     * 大屏id
     */
    private String industryViewId;


    /**
     * 大屏类型 0-应用大屏 1-第三方大屏
     */
    private Integer industryViewType;

    /**
     * 应用大屏类型 1-旧大屏 2-新大屏
     */
    private Integer viewType;

    /**
     * 新增修改标识(1.新增 2.修改)
     */
    private String flag;


    private String userName;


    /**
     * 角色id
     */
    private List<Long> roleIdList;

    /**
     * 角色id
     */
    private List<SysRoleScreenVO> industryViewList;


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

    /**
     * 个性化大屏菜单配置列表
     */
    private List<ScreenMenuConfVO> screenMenuConfVOList;

    /**
     * 是否个性化配置标识
     */
    private String personalFlag;


    /**
     * 1代表只有自定义角色
     */
    private String roleScreenFlag;


}
