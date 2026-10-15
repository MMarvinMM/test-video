package com.chinatower.manage.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 行业大屏关联模型
 * @author chmod
 * @version 1.0
 * @date 2022/2/9 16:29
 */
@Data
public class SysIndustryViewRelModel extends BaseModel implements Serializable {
    private static final long serialVersionUID = 2881663621257511762L;
    /** 主键 */
    private Long industryViewId;
    /** 行业配置ID */
    private Long industryId;
    /** 视图大屏ID集合 */
    private String viewList;
    /** 版本号 */
    private String appVerCode;
    /** 应用版本描述 */
    private String appVerDesc;
    /** 应用图标地址 */
    private String imgUrl;
    /** 创建人 */
    private String creator;
    /** 修改人 */
    private String modifier;
    /** 创建时间 */
    private Date createDate;
    /** 修改时间 */
    private Date updateDate;
    /** 1上架 0下架 */
    private String status;

    /** 菜单ID */
    private String menuId;
    private String industryName;
    private String industryCode;
    /** 查询开启分页：true-开启，false-关闭 */
    private boolean enablePage;

    private String acceptNode;

    /**
     * bannerId
     */
    private Long bannerId;
    /**
     * banner信息
     */
    private IndustryViewBannerInfoDTO banner;

    /** 大屏关联菜单列表 */
    private List<MenuInfoDTO> menuList;
}
