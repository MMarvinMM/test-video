package com.chinatower.event.common;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;

/**
 * @Auther: wjd
 * @Date: 2022/2/8 12:32
 * @Description:
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseTableEntity extends BaseDataScopeEntity implements Serializable {
    protected String tableName;
}
