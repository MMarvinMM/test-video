package com.chinatower.event.vo.zhongshi;

import com.chinatower.event.dto.zhongshi.FunctionValidationDTO;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 功能模块统计
 */
@Data
public class FunctionModuleStatsVO {

    /**
     * 验证功能点数量
     */
    private Long functionPointCount;

    /**
     * 首次通过率
     */
    private BigDecimal firstPassRate;

    /**
     * 最终通过率
     */
    private BigDecimal finalPassRate;

    /**
     * 功能模块明细
     */
    private List<FunctionValidationDTO> detailList;
}
