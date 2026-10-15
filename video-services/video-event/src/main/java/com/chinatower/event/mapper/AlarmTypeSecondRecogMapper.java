package com.chinatower.event.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.event.entity.vo.AlarmTypeSecondRecogVO;
import com.chinatower.event.entity.vo.CombinationVo;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 二次识别配置告警类型
 */
@Mapper
@DS(EbikeDataBaseConstant.BASE)
public interface AlarmTypeSecondRecogMapper {


    /**
     * 批量插入
     * @param typeList
     */
    void insertBatch(List<AlarmTypeSecondRecogVO> typeList);

    /**
     * 二次识别配置告警类型逻辑删除
     * @param alarmSecondRecogId
     */
    void deleteAlarmSecondRecogId(@Param("alarmSecondRecogId") Integer alarmSecondRecogId,@Param("modifier") String modifier);


    /**
     * 根据id逻辑删除二次识别配置告警类型
     * @param list
     * @return
     */
    int deleteIds(List<CombinationVo> list);

    /**
     * 根据id获取二次识别配置告警类型集合
     * @param id
     * @return
     */
    List<AlarmTypeSecondRecogVO> findAllListByAlarmSecondRecogId(Integer id);
}
