package com.chinatower.common.core.dto;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author chmod
 * @version 1.0
 * @date 2022/1/14 14:44
 */
@Data
public class BaseModel extends BaseDataScopeEntity implements Serializable {
    private static final long serialVersionUID = -5550768447758103891L;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    protected Integer pageNum;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    protected Integer pageSize;
    protected Integer offset;
    protected Integer limit;
    protected Integer total;
    protected Date startTime;
    protected Date endTime;
}
