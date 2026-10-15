package com.chinatower.analysis.api.entity;

import com.chinatower.common.core.dto.PageDTO;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class CustomVO extends PageDTO {
    private static final long serialVersionUID = 1L;

    /**
     * 租户ID
     * @required false
     */
    private String tenantId;

    /**
     * 开始时间
     * @required true
     */
    private String queryStartTime;

    /**
     * 结束时间
     * @required true
     */
    private String queryEndTime;

    /**
     * 组织ID集合
     * @required false
     */
    private List<String> orgIdList;

    /**
     * 组织ID
     * @required false
     */
    private String deptId;

    /**节点编码*/
    private String localNode;

    private List<String> userIdList;

    private Integer pageNums;
    private Integer pageSizes;

    /**类型  0:三端总合 1:pc端 2:app 3:小程序*/
    private Integer endType;

    private List<String> systemTypeList;
}
