package com.chinatower.event.mapper;

import com.chinatower.common.core.vo.AlarmClassParamVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 告警类型阈值参数mapper
 * @author chmod
 * @version 1.0
 * @date 2022/3/11 14:15
 */
@Mapper
public interface AlarmClassParamMapper {
    /**
     * 新增
     * @param alarmClassParamList
     * @return
     */
    int save(@Param("list") List<AlarmClassParamVO> alarmClassParamList);

    /**
     * 删除
     * @param alarmTypeClassId
     * @return
     */
    int delete(String alarmTypeClassId);

    /**
     * 获取告警类型阈值
     * @param alarmClassParamVO
     * @return
     */
    AlarmClassParamVO getInfo(AlarmClassParamVO alarmClassParamVO);
}
