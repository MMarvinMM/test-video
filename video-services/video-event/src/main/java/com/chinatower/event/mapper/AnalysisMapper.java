package com.chinatower.event.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.event.dto.AnalysisDTO;
import com.chinatower.event.dto.EventNumByAreaDTO;
import com.chinatower.event.dto.EventNumByHourDTO;
import com.chinatower.event.dto.EventNumNewDTO;
import com.chinatower.event.entity.vo.AreaEventVO;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import com.chinatower.platform.entity.admin.SysArea;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;
import java.util.List;

/**
 * 功能描述: 自定义的 统计数据 DAO层
 *
 * @ClassName AnalysisMapper
 * @auther: chenshaowu
 * @date: 2022/2/16
 * @version: 1.0
 */
@Mapper
@DS(EbikeDataBaseConstant.STATISTIC)
public interface AnalysisMapper {

    /**
     * 告警事件统计-事件高发区域
     *
     * @return
     * @author chenmf
     * @date 2022/6/7
     * @version
     */
    List<EventNumByAreaDTO> getEventNumByArea(@Param("param") AnalysisDTO analysisDTO);

    /**
     * 获取事件数量-曲线图用
     *
     * @return
     * @author chenmf
     * @date 2022/6/7
     * @version
     */
    List<EventNumByHourDTO> getEventNumByHour(@Param("param") AnalysisDTO analysisDTO);

    /**
     * 获取事件统计数量
     *
     * @return
     * @author chenmf
     * @date 2022/6/7
     * @version
     */
    EventNumNewDTO getEventNumNew(@Param("param") AnalysisDTO analysisDTO);

    /**
     * 租户下用户查询本组织或本人的待处置数量
     *
     * @param
     * @return int
     */
    @DS(EbikeDataBaseConstant.PLATFORM)
    int getPeddingDisposal(@Param("userId")String userId, @Param("deptId") String deptId, @Param("tableName") String tableName);

    List<AreaEventVO> getEventNumListByArea(@Param("param") AnalysisDTO analysisDTO);

    @DS(EbikeDataBaseConstant.BASE)
    List<SysArea> getSysAreas(@Param("areaCodes") ArrayList<String> areaCodes, @Param("level") Integer level);

    @DS(EbikeDataBaseConstant.BASE)
    String getAlarmByCode(String areaCode);
}
