package com.chinatower.event.mapper;


import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.event.vo.OrderCustomerVO;
import com.chinatower.event.entity.vo.ReceiveAlarmVO;
import com.chinatower.event.vo.entity.ChannelSuppressVO;
import com.chinatower.event.vo.entity.VmAlarmSuppressStrategyVO;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 功能说明: 告警策略
 * @author: li.xufu
 * @date: 2022/5/8
 * @requestID 需求单号及需求名称
 */
@Mapper
@DS(EbikeDataBaseConstant.BASE)
public interface VmAlarmSuppressStrategyMapper {

    /**
     * @author:
     * @date:
     * @param suppressVO
     * 功能说明: 查询全部策略
     * @return List<VmAlarmSuppressStrategyVO>
     * @tableName: vm_alarm_suppress_strategy
     * @operatorType: select
     * @requestID 需求单号及需求名称
     */
    List<VmAlarmSuppressStrategyVO> selectAll(VmAlarmSuppressStrategyVO suppressVO);

    /**
     * 根据告警压制策略名称查询
     * @param suppressVO
     * @return
     */
    List<VmAlarmSuppressStrategyVO> selectAllByName(VmAlarmSuppressStrategyVO suppressVO);

    List<VmAlarmSuppressStrategyVO> selectList(VmAlarmSuppressStrategyVO suppressVO);

    /**
     * @author: li.xufu
     * @date: 2022/5/22
     * @param suppressVO
     * 功能说明: 查询全部策略，包括失效的
     * @return List<VmAlarmSuppressStrategyVO>
     * @tableName: vm_alarm_suppress_strategy
     * @operatorType: select
     * @requestID 需求单号及需求名称
     */
    List<VmAlarmSuppressStrategyVO> selectAllCount(VmAlarmSuppressStrategyVO suppressVO);

    /**
     * @author: li.xufu
     * @date: 2022/5/22
     * @param strategyId
     * 功能说明: 策略主键ID查询策略
     * @return VmAlarmSuppressStrategyVO
     * @tableName: vm_alarm_suppress_strategy
     * @operatorType: select
     * @requestID 需求单号及需求名称
     */
    VmAlarmSuppressStrategyVO selectByPrimaryKey(Integer strategyId);

    /**
     * @author: li.xufu
     * @date: 2022/5/22
     * @param suppressVO
     * 功能说明: 新增告警压制策略
     * @return int
     * @tableName: vm_alarm_suppress_strategy
     * @operatorType: insert
     * @requestID 需求单号及需求名称
     */
    int insert(VmAlarmSuppressStrategyVO suppressVO);
    /**
     * @author: li.xufu
     * @date: 2022/5/22
     * @param suppressVO
     * 功能说明: 修改告警压制策略
     * @return int
     * @tableName: vm_alarm_suppress_strategy
     * @operatorType: update
     * @requestID 需求单号及需求名称
     */
    int update(VmAlarmSuppressStrategyVO suppressVO);

    /**
     * @author: li.xufu
     * @date: 2022/5/22
     * @param suppressVO
     * 功能说明: 修改告警压制策略状态
     * @return int
     * @tableName: vm_alarm_suppress_strategy
     * @operatorType: update
     * @requestID 需求单号及需求名称
     */
    int updateStatus(VmAlarmSuppressStrategyVO suppressVO);

    /**
     * @author: li.xufu
     * @date: 2022/5/22
     * @param strategyId
     * @description: 根据策略ID删除告警压制策略
     * 弃用，不做物理删除
     * @return
     */
    int deleteByPrimaryKey(Integer strategyId);

    /**
     * @author: li.xufu
     * @date: 2022/5/22
     * @param suppressVO
     * 功能说明: 根据ID删除告警压制策略
     * @return int
     * @tableName: vm_alarm_suppress_strategy
     * @operatorType: delete
     * @requestID 需求单号及需求名称
     */
    int deleteById(VmAlarmSuppressStrategyVO suppressVO);

    /**
     * @author: li.xufu
     * @date: 2022/5/22
     * @param receiveAlarmVO
     * @description: 查询告警压制策略ID
     * @return
     */
    List<Integer> selectSuppressStrategy(@Param("req") ReceiveAlarmVO receiveAlarmVO,@Param("customers") List<OrderCustomerVO> customers);
    /**
     * @author: li.xufu
     * @date: 2022/5/22
     * @param channelSuppressVo
     * 功能说明: 查询设备通道信息
     * @return: List<ChannelSuppressVO>
     * @tableName: vm_channel_info_t
     * @tableName: vm_order_devices
     * @operatorType: select
     * @requestID 需求单号及需求名称
     */
    List<ChannelSuppressVO> queryChannelByDeviceCode(ChannelSuppressVO channelSuppressVo);

    /**
     * @author zhen.yan
     * @date 2022/6/15 11:20
     * @param tenantId
     * 功能说明: 获取当前租户下所有告警压制配置策略的数量
     * @return: int
     * @tableName: vm_alarm_area_suppress
     * @operatorType: select
     * @requestID 需求单号及需求名称
     */
    int getTenantMaxAreaNum(String tenantId);

    /**
     * 获取默认名称（告警压制策略名称：告警压制策略+数字）
     * @date 2022/12/22 14:20
     * @param suppressStrategyVO
     * @return String
     */
    String getAutoStrategyName(VmAlarmSuppressStrategyVO suppressStrategyVO);

    /**
     * 获取默认名称（区域名称：摄像机名称+屏蔽区域+数字）
     * @date 2024/06/04 18:48
     * @version v1.6.70
     * @param suppressStrategyVO
     * @return String
     */
    String getAutoRegionName(VmAlarmSuppressStrategyVO suppressStrategyVO);
}
