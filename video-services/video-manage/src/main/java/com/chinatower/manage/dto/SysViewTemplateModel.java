package com.chinatower.manage.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author chmod
 * @version 1.0
 * @date 2022/1/14 9:15
 */
@Data
public class SysViewTemplateModel extends BaseModel implements Serializable {
    private static final long serialVersionUID = -7602199822832048189L;

    private Long viewId;
    private Long[] viewIdArr;
    private String viewName;
    private Long dirId;
    private String viewJson;
    private String compoList;
    private String coverPicUrl;
    private String status;
    private String tenantId;
    /** 查询开启分页：true-开启，false-关闭 */
    private boolean enablePage;
}
