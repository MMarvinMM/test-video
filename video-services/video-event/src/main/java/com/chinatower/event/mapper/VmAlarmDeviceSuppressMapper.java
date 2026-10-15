package com.chinatower.event.mapper;


import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.event.vo.SysLntlagConfigVO;
import com.chinatower.event.vo.entity.VmAlarmDeviceSuppressVO;
import com.chinatower.event.vo.entity.VmAlarmSuppressStrategyVO;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 功能说明: 告警设备压制dao
 * @author: li.xufu
 * @date: 2022/5/8
 * @requestID 需求单号及需求名称
 */
@Mapper
@DS(EbikeDataBaseConstant.BASE)
public interface VmAlarmDeviceSuppressMapper {

    /**
     * @author:
     * @date:
     * @param suppressVO
     * 功能说明: 查询设备信息
     * @return List<VmAlarmDeviceSuppressVO>
     * @tableName: vm_alarm_device_suppress
     * @operatorType: select
     * @requestID 需求单号及需求名称
     */
    List<VmAlarmDeviceSuppressVO> selectAll(VmAlarmDeviceSuppressVO suppressVO);

    /**
     * @author:
     * @date:
     * @param suppressVO
     * 功能说明: 查询设备信息
     * @return List<VmAlarmDeviceSuppressVO>
     * @tableName: vm_alarm_device_suppress
     * @operatorType: select
     * @requestID 需求单号及需求名称
     */
    List<VmAlarmDeviceSuppressVO> selectList(VmAlarmSuppressStrategyVO suppressVO);

    VmAlarmDeviceSuppressVO selectByPrimaryKey(Integer deviceId);

    int insert(VmAlarmDeviceSuppressVO suppressVO);

    int update(VmAlarmDeviceSuppressVO suppressVO);

    int deleteByPrimaryKey(Integer deviceId);

    /**
     * @author:
     * @date:
     * @param strategyVo
     * 功能说明: 根据告警压制策略ID删除设备信息
     * @return int
     * @tableName: vm_alarm_device_suppress
     * @operatorType: delete
     * @requestID 需求单号及需求名称
     */
    int deleteByStrategyId(VmAlarmSuppressStrategyVO strategyVo);

    /**
     * @author:
     * @date:
     * @param deviceList
     * 功能说明: 批量新增设备信息
     * @return int
     * @tableName: vm_alarm_device_suppress
     * @operatorType: insert
     * @requestID 需求单号及需求名称
     */
    int batchInsert(List<VmAlarmDeviceSuppressVO> deviceList);

    /**
     * 功能说明: 查询设备参数信息
     * @param deviceCode
     * @return SysLntlagConfigVO
     * @author:
     * @date:
     * @tableName: vm_device_info_t
     * @tableName: vm_device_model_t
     * @operatorType: select
     * @requestID 需求单号及需求名称
     */
    SysLntlagConfigVO getLntlagConfig(String deviceCode);
}
