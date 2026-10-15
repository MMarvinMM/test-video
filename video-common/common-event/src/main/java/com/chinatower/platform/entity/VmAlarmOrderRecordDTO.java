package com.chinatower.platform.entity;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

@Data
@ToString
public class VmAlarmOrderRecordDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String tableName;
    private List<VmAlarmIOrderRecordVO> list;
}
