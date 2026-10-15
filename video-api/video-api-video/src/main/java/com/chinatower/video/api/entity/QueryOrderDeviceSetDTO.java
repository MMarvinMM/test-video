package com.chinatower.video.api.entity;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import lombok.Data;
import lombok.ToString;
import org.apache.ibatis.type.Alias;

import javax.validation.constraints.NotNull;
import java.util.List;


/**
 * @ClassName QueryOrderDeviceSetDTO
 * @Description 查询设备预置位/巡航/录像计划对象
 * @Author chenmf
 * @Date 2022/1/24
 **/
@Data
@ToString
@Alias("QueryOrderDeviceSetDTOVideo")
public class QueryOrderDeviceSetDTO extends BaseDataScopeEntity {

    /**
     * 客户编码
     * @required true
     */
    @NotNull(message = "客户编码不能为空")
    private String tenantId;

    /**
     * 订单编码
     * @required true
     */
    @NotNull(message = "订单编码不能为空")
    private String orderCode;

    /**
     * 套餐编码
     */
    private String packageCode;

    /**
     * 设备编码
     */
    private List<String> deviceCodeList;

    private String industryCode;

}
