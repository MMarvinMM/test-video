package com.chinatower.common.core.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 视图模板实体
 * @author chmod
 * @version 1.0
 * @date 2022/1/13 17:38
 */
@Data
public class SysViewTemplateDTO implements Serializable {
    private static final long serialVersionUID = -1345675228071712577L;
    /** 主键 */
    private Long viewId;
    /** 视图名称 */
    private String viewName;
    /** 视图所属目录ID */
    private Long dirId;
    /** 视图样式JSON串 */
    private String viewJson;
    /** 组件ID集合，多个以,分隔 */
    private String compoList;
    /** 封面图片URL */
    private String coverPicUrl;
    /** 创建时间 */
    private Date createDate;
    /** 修改时间 */
    private Date updateDate;
    /** 状态：1生效 0失效 */
    private String status;
}
