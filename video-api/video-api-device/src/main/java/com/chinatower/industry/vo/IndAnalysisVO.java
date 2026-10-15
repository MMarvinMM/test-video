package com.chinatower.industry.vo;

import com.chinatower.platform.model.dto.CommonFeignEntity;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * @Name IndAnalysisVO
 * @Description
 * @Author trk
 * @Date 2022/04/2022/4/7 20 : 36
 */
@Data
@ToString
@ApiModel(description = "重复报警信息查询入参")
public class IndAnalysisVO extends CommonFeignEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    private String dimensionFlag;
    private String queryStartTime;
    private String queryEndTime;
    private String provinceId;
    private String cityId;
    private String countyId;
}
