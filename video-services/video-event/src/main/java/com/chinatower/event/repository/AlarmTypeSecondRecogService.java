package com.chinatower.event.repository;

import com.chinatower.event.entity.vo.AlarmTypeSecondRecogVO;

import java.util.List;

/**
 * 二次识别配置告警类型相关接口
 */
public interface AlarmTypeSecondRecogService {

    /**
     * 批量插入
     * @param typeList
     */
    void insertBatch(List<AlarmTypeSecondRecogVO> typeList);

}
