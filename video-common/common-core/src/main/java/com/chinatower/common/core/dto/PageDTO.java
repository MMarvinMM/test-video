package com.chinatower.common.core.dto;

import com.chinatower.common.utils.StringUtils;
import com.chinatower.platform.model.dto.CommonFeignEntity;
import lombok.Data;

import javax.validation.constraints.Pattern;

@Data
public class PageDTO extends CommonFeignEntity {
    /**
     * 页码
     */
    private Integer pageNum;
    /**
     * 页长
     */
    private Integer pageSize;
    /**
     * 排序字段
     */
    private String orderByColumn;

    /**
     * 排序规则
     */
    @Pattern(regexp = "asc|desc",message = "排序规则不合法")
    private String isAsc;

    public String getOrderBy() {
        return StringUtils.isEmpty(this.orderByColumn) ? "" : "CONVERT("+StringUtils.toUnderScoreCase(this.orderByColumn)+" USING GBK) " + this.isAsc;
    }
}
