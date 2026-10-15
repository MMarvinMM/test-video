package com.chinatower.bdm.api.entity;

import com.chinatower.common.core.dto.PageDTO;
import com.chinatower.common.core.entity.BaseDataScopeEntity;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class InspectStatisticsVo extends PageDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 所属网格id
     */
    private String gridId;

    /**
     * 开始时间(格式：yyyy-MM-dd HH:mm:ss）
     */
    private String queryStartTime;
    /**
     * 结束时间(格式：yyyy-MM-dd HH:mm:ss）
     */
    private String queryEndTime;
    /**
     * 网格员id
     */
    private String keeperId;

    /**
     * 反馈人网格员id
     */
    private String feedbackKeeperId;

    /**
     *  网格id集合
     */
    private List<String> gridList;

    /**
     *  网格id集合
     */
    private List<String> gridIdList;

    /**
     * 1 网格长  2.普通网格员
     */
    private String keeperType;

    /**
     * 排序字段
     * */
    private String sort;

    /**
     * 排序方式
     * */
    private String sortOrder;

}
