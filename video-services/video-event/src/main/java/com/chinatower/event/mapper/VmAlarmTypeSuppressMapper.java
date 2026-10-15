package com.chinatower.event.mapper;


import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.event.vo.entity.VmAlarmSuppressStrategyVO;
import com.chinatower.event.vo.entity.VmAlarmTypeSuppressVO;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 功能说明: 告警类型dao
 * @author: li.xufu
 * @date: 2022/5/8
 * @requestID 需求单号及需求名称
 */
@Mapper
@DS(EbikeDataBaseConstant.BASE)
public interface VmAlarmTypeSuppressMapper {

    /**
     * @author:
     * @date:
     * @param suppressVO
     * 功能说明: 查询告警类型信息
     * @return List<VmAlarmTypeSuppressVO>
     * @tableName: vm_alarm_type_suppress
     * @operatorType: select
     * @requestID 需求单号及需求名称
     */
    List<VmAlarmTypeSuppressVO> selectAll(VmAlarmTypeSuppressVO suppressVO);


    /**
     * @author:
     * @date:
     * @param suppressVO
     * 功能说明: 查询告警类型信息
     * @return List<VmAlarmTypeSuppressVO>
     * @tableName: vm_alarm_type_suppress
     * @operatorType: select
     * @requestID 需求单号及需求名称
     */
    List<VmAlarmTypeSuppressVO> selectList(VmAlarmSuppressStrategyVO suppressVO);

    VmAlarmTypeSuppressVO selectByPrimaryKey(Integer typeId);

    int insert(VmAlarmTypeSuppressVO suppressVO);

    int update(VmAlarmTypeSuppressVO suppressVO);

    int deleteByPrimaryKey(Integer typeId);

    /**
     * @author:
     * @date:
     * @param strategyVo
     * 功能说明: 根据告警压制策略ID删除告警类型信息
     * @return int
     * @tableName: vm_alarm_type_suppress
     * @operatorType: delete
     * @requestID 需求单号及需求名称
     */
    int deleteByStrategyId(VmAlarmSuppressStrategyVO strategyVo);

    /**
     * @author:
     * @date:
     * @param typeList
     * 功能说明: 批量新增告警类型信息
     * @return int
     * @tableName: vm_alarm_type_suppress
     * @operatorType: insert
     * @requestID 需求单号及需求名称
     */
    int batchInsert(List<VmAlarmTypeSuppressVO> typeList);


}
