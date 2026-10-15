package com.chinatower.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chinatower.common.core.domain.OrderAlgorithmsHisDO;
import com.chinatower.order.vo.SceneAlarmRelVO;
import com.chinatower.order.vo.SceneConfigVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @ClassName OrderAlgorithmsHisMapper
 * @Deacription
 * @Author chenmf
 * @Date 2022/1/6 14:14
 * @Version 1.0
 **/
@Mapper
public interface OrderAlgorithmsHisMapper extends BaseMapper<OrderAlgorithmsHisDO> {

    List<String> selectOrderedModelAlgorithmCodeHis(@Param("tenantId") String tenantId, @Param("industryCode")String industryCode,
                                                 @Param("algorithmCodeList") List<String> algorithmCodeList);

    List<String> selectOrderedModelAlgorithmCode(@Param("tenantId") String tenantId, @Param("industryCode")String industryCode,
                                                 @Param("orderCode") String orderCode, @Param("algorithmCodeList") List<String> algorithmCodeList);

    List<String> selectAlreadyInitAlgorithmCode(@Param("tenantId") String tenantId, @Param("industryCode")String industryCode,
                                                    @Param("algorithmCodeList") List<String> algorithmCodeList);

    int initSceneConfigInfo(@Param("list") List<SceneConfigVO> list);

    int initSceneAlarmRelInfo(@Param("list") List<SceneAlarmRelVO> list);

    List<String> selectModelAlgorithmCodeList();
}
