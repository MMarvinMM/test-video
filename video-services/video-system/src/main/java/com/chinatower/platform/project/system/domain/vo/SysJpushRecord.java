package com.chinatower.platform.project.system.domain.vo;

import com.chinatower.platform.entity.msg.BsJpushRecord;
import lombok.Data;

@Data
public class SysJpushRecord extends BsJpushRecord {

    private Integer pageNum;

    private Integer pageSize;

    /**
     * 1-告警
     * 2-通知、行业
     * 3-其他
     */
    private String type;
}
