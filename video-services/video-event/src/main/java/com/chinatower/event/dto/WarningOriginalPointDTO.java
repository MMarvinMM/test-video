package com.chinatower.event.dto;

import com.chinatower.event.entity.vo.AlarmOriginalPointVO;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * @author tianhao
 */
@Data
@ToString
public class WarningOriginalPointDTO implements Serializable {

    private String tableName;

    private List<AlarmOriginalPointVO> list;
}
