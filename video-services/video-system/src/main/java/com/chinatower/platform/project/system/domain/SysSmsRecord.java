package com.chinatower.platform.project.system.domain;

import com.chinatower.common.utils.StringUtils;
import com.chinatower.platform.entity.msg.BsSmsRecord;
import lombok.Data;

@Data
public class SysSmsRecord extends BsSmsRecord {

    private String startTime;

    private String endTime;

    private String orderByColumn;
    
    private String isAsc;

    public String getOrderBy() {
        return StringUtils.isEmpty(this.orderByColumn) ? "" : StringUtils.toUnderScoreCase(this.orderByColumn) + " " + this.isAsc;
    }
}
