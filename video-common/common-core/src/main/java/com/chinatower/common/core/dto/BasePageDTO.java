package com.chinatower.common.core.dto;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * Copyright: Copyright(c) 2019 iwhalecloud
 * <p>
 * 类说明：TODO
 * <p>
 * 类名称: BasePageDTO.java
 *
 * @author su.tingning
 * @version v1.0.0
 * @date 2022/1/27 10:03
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ------------------------------------------------------------
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@SuppressWarnings("all")
public class BasePageDTO extends BaseDataScopeEntity implements Serializable {

    protected Integer pageNum;
    protected Integer pageSize;
    protected Integer offset;
    protected Integer limit;
    protected Integer total;
    protected Object list;
    protected Date startTime;
    protected Date endTime;
}
