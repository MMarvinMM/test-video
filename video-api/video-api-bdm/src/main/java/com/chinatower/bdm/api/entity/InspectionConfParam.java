package com.chinatower.bdm.api.entity;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * 查询参数
 * @author zl
 */
@Data
public class InspectionConfParam extends BaseDataScopeEntity {

    /**
     * 计划名称
     */
    private String cnofName;

    private String gridId;
    /**
     * 网格员名称
     */
    private String keeperName;
    /**
     * 网格员编码
     */
    private String keeperId;

    /**
     * 状态
     */
    private String confStatus;

    //创建人
    private String creator;

    //修改人
    private String modifier;

    //创建开始时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private String createStartDate;
    //创建结束时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private String createEndDate;
    //修改开始时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private String modifyStartDate;
    //修改结束时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private String modifyEndDate;

    //排序：desc降序，asc升序
    private String sort = "desc";

    //排序标识：1创建时间；2更新时间
    private String sortFlag = "1";

    /**
     * 当前页
     */
    private int pageNum;
    /**
     * 条数限制
     */
    private int pageSize;
}
