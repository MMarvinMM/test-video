package com.chinatower.event.dto;

import java.io.Serializable;
import java.util.List;

import com.chinatower.event.vo.AlarmOrderRecordVO;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class AlarmOrderRecordDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 表名
     */
    private String tableName;
    /**
     * 告警环节信息
     */
    private List<AlarmOrderRecordVO> list;
}
