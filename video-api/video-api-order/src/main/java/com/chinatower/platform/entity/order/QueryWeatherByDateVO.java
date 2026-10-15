package com.chinatower.platform.entity.order;

import com.chinatower.common.core.entity.BaseIndustryEntity;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @ClassName QueryWeatherByDateVO
 * @Deacription 查询天气
 * @Author chenmf
 * @Date 2022/4/22 15:57
 * @Version 1.0
 **/
@Data
public class QueryWeatherByDateVO extends BaseIndustryEntity {

    /**
     * 开始日期
     * @required true
     */
    @NotNull(message = "开始日期不能为空")
    private String startDate;

    /**
     * 结束日志
     * @required true
     */
    @NotNull(message = "结束日期不能为空")
    private String endDate;

    /**
     * 行政区域编码列表
     */
    private List<String> adCodeList;

    private String collectionName;
}
