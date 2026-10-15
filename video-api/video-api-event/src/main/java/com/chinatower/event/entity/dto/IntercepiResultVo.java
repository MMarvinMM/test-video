package com.chinatower.event.entity.dto;

import com.chinatower.event.entity.vo.InterceptVO;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class IntercepiResultVo {

    private int total;
    private List<InterceptVO> resultList;
    private List<Map<String,Object>> resList;
    /**
     * 1：mongo来源
     */
    private String flag;
}
