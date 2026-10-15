package com.chinatower.manage.api.dto;

import lombok.Data;

import java.util.Date;

@Data
public class ResourceTypeIntEdition {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 资源类型编码
     */
    private String resourceTypeId;

    /**
     * 资源类型名称
     */
    private String resourceTypeName;

    /**
     * 上级资源类型编码
     */
    private String parentId;

    /**
     * 上级资源类型名称
     */
    private String parentName;

    /**
     * 显示顺序
     */
    private Long orderNum;

    /**
     * 类型状态（0正常 1停用）
     */
    private String status;

    /**
     * 属性类型 1-点资源 2-线资源 3-面资源
     */
    private Integer attrType;

    /**
     * 备注
     */
    private String remark;

    /**
     * 图标
     */
    private String icon;

    /**
     * 默认状态（0是 1否）
     */
    private String approve;

    /**
     * 资源类型层级路径
     */
    private String typeLevelUrl;

    //行业编码
    private String industryCode;

    //行业版本编码
    private String appVerCode;

    /** 创建人 */
    private String creator;
    /** 修改人 */
    private String modifier;
    /** 创建时间 */
    private Date createTime;
    /** 修改时间 */
    private Date updateTime;

    //删除标志位
    private String deleteFlag;

    /**
     * 属地版本号id
     */
    private Long localizedVersionId;
}
