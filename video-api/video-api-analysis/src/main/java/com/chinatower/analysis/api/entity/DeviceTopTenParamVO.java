package com.chinatower.analysis.api.entity;

import com.chinatower.common.core.entity.BaseDataScopeEntity;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class DeviceTopTenParamVO extends BaseDataScopeEntity {

    /**
     * 区域类型:1-全国、2-省、3-市、4-区县
     * @required true
     */
    @NotBlank(message = "区域类型不能为空")
    private String areaType;

    /**
     * 区域编码
     * @required true
     */
    @NotBlank(message = "区域编码不能为空")
    private String areaCode;

    /**
     * 开始时间 YYYY-MM-DD
     * @required true
     */
    @NotBlank(message = "开始时间不能为空")
    private String startDate;

    /**
     * 结束时间 YYYY-MM-DD
     * @required true
     */
    @NotBlank(message = "结束时间不能为空")
    private String endDate;

    /**
     * 排序字段
     * */
    private String sort;

    /**
     * 排序方式
     * */
    private String sortOrder;
}
