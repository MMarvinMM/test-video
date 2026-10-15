package com.chinatower.event.vo;

import com.chinatower.common.core.dto.BasePageDTO;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * 重复报警信息查询入参
 * @author chmod
 * @version 1.0
 * @date 2022/3/11 09:22
 */
@Data
@ToString
@ApiModel(description = "重复报警信息查询入参")
public class SearchOrderVO extends BasePageDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String provinceId;

    private String cityId;

    private String countyId;

    private String latitude;

    private String longitude;

    private String tenantId;

    private String isQuery;

    private List<String> countyIdList;

    private String tableName;

    private String cityDate;

    /**
     * 查询几天内的数据
     */
    private String dayNum;

    /**
     * 自定义时间段：开始时间 年月日
     */
    private String webStartDate;
    /**
     * 自定义时间段：结束时间 年月日
     */
    private String webEndDate;
}
