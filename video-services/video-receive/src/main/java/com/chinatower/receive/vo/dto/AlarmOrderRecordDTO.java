package com.chinatower.receive.vo.dto;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

@Data
@ToString
public class AlarmOrderRecordDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String tableName;
    private List<AlarmOrderRecordVO> list;
}
