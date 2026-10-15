package com.chinatower.event.dto;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import lombok.Data;

import java.io.Serializable;

/**
 * @className: AppOrderUpdateDto
 * @description:
 * @author: yongqi.han
 * @date: 2022/8/18
 * @requestID 需求单号及需求名称
 */
@Data
public class AppOrderUpdateDto extends BaseDataScopeEntity implements Serializable {

    private String warningOrderId;
    private String warningTypeId;
    private String address;
    private String longitude;
    private String latitude;
    private String importanceLevel;

    private String tableName;

}
