package com.chinatower.event.mapper;




import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.event.entity.dto.AlarmAreaUpdateReq;
import com.chinatower.event.vo.entity.VmAlarmAreaSuppressVO;
import com.chinatower.event.vo.entity.VmAlarmSuppressStrategyVO;
import com.chinatower.event.vo.entity.VmAlarmUavAreaSuppressVO;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import com.chinatower.platform.model.vo.DeviceModelVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 功能说明: 告警区域压制dao
 * @author: li.xufu
 * @date: 2022/5/8
 * @requestID 需求单号及需求名称
 */
@Mapper
@DS(EbikeDataBaseConstant.BASE)
public interface VmAlarmAreaSuppressMapper {

    /**
     * @author:
     * @date:
     * @param suppressVO
     * 功能说明: 查询区域压制信息
     * @return List<VmAlarmAreaSuppressVO>
     * @tableName: vm_alarm_area_suppress
     * @operatorType: select
     * @requestID 需求单号及需求名称
     */
    List<VmAlarmAreaSuppressVO> selectAll(VmAlarmAreaSuppressVO suppressVO);

    /**
     * @author:
     * @date:
     * @param suppressVO
     * 功能说明: 查询区域压制信息
     * @return List<VmAlarmAreaSuppressVO>
     * @tableName: vm_alarm_area_suppress
     * @operatorType: select
     * @requestID 需求单号及需求名称
     */
    List<VmAlarmAreaSuppressVO> selectList(VmAlarmSuppressStrategyVO suppressVO);

    /**
     * @author:
     * @date:
     * @param suppressVO
     * 功能说明: 查询无人机区域压制信息
     * @return List<VmAlarmAreaSuppressVO>
     * @tableName: vm_alarm_area_suppress
     * @operatorType: select
     * @requestID 需求单号及需求名称
     */
    List<VmAlarmUavAreaSuppressVO> selectUavList(VmAlarmSuppressStrategyVO suppressVO);

    List<VmAlarmAreaSuppressVO> selectAreaSuppress (VmAlarmAreaSuppressVO suppressVO);


    VmAlarmAreaSuppressVO selectByPrimaryKey(Integer areaId);

    int insert(VmAlarmAreaSuppressVO suppressVO);

    int update(VmAlarmAreaSuppressVO suppressVO);

    int batchUpdate(@Param("list") List<AlarmAreaUpdateReq> list);

    int deleteByPrimaryKey(Integer areaId);

    /**
     * @author:
     * @date:
     * @param strategyVo
     * 功能说明: 根据告警压制策略ID删除区域压制信息
     * @return int
     * @tableName: vm_alarm_area_suppress
     * @operatorType: delete
     * @requestID 需求单号及需求名称
     */
    int deleteByStrategyId(VmAlarmSuppressStrategyVO strategyVo);

    /**
     * @author:
     * @date:
     * @param strategyVo
     * 功能说明: 根据告警压制策略ID删除无人机区域压制信息
     * @return int
     * @tableName: vm_alarm_area_suppress
     * @operatorType: delete
     * @requestID 需求单号及需求名称
     */
    int deleteUavByStrategyId(VmAlarmSuppressStrategyVO strategyVo);

    /**
     * @author:
     * @date:
     * @param areaList
     * 功能说明: 批量新增区域压制信息
     * @return int
     * @tableName: vm_alarm_area_suppress
     * @operatorType: insert
     * @requestID 需求单号及需求名称
     */
    int batchInsert(List<VmAlarmAreaSuppressVO> areaList);

    /**
     * @author:
     * @date:
     * @param areaList
     * 功能说明: 批量无人机新增区域压制信息
     * @return int
     * @tableName: vm_alarm_uav_area_suppress
     * @operatorType: insert
     * @requestID 需求单号及需求名称
     */
    int batchUavInsert(@Param("list") List<VmAlarmUavAreaSuppressVO> areaList);

    /**
     * @author:
     * @date:
     * @param deviceCode
     * 功能说明: 查询设备信息
     * @return List<DeviceModelVO>
     * @tableName: vm_device_info_t
     * @tableName: vm_device_model_t
     * @operatorType: select
     * @requestID 需求单号及需求名称
     */
    List<DeviceModelVO> selectDeviceInfo(String deviceCode);

    List<DeviceModelVO> selectDeviceModel(String deviceCode);

    List<VmAlarmAreaSuppressVO> selectAreaSuppressList (VmAlarmAreaSuppressVO suppressVO);

    List<VmAlarmAreaSuppressVO> areaSuppressCheck (VmAlarmAreaSuppressVO suppressVO);
}
