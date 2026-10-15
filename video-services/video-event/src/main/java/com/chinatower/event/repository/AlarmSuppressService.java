package com.chinatower.event.repository;

import com.chinatower.common.entity.Result;
import com.chinatower.event.vo.SysLntlagConfigVO;
import com.chinatower.event.vo.entity.ChannelSuppressVO;
import com.chinatower.event.vo.entity.VmAlarmAreaSuppressVO;
import com.chinatower.event.vo.entity.VmAlarmSuppressStrategyVO;
import com.chinatower.platform.model.vo.DeviceModelVO;

import java.util.List;
import java.util.Map;

/**
 * 功能说明: 告警压制API
 * @author: li.xufu
 * @date: 2022/5/8
 * @requestID 需求单号及需求名称
 */
public interface AlarmSuppressService {

    //压制策略
    List<VmAlarmSuppressStrategyVO> getList(VmAlarmSuppressStrategyVO vmAlarmSuppressStrategyVo);

    List<VmAlarmSuppressStrategyVO> getListAll(VmAlarmSuppressStrategyVO vmAlarmSuppressStrategyVo);

    Result<VmAlarmSuppressStrategyVO> getStrategyInfo(VmAlarmSuppressStrategyVO vmAlarmSuppressStrategyVo);

    Result<List<ChannelSuppressVO>> queryChannelByDeviceCode(ChannelSuppressVO channelSuppressVo);

    Result<String> getAutoStrategyName(VmAlarmSuppressStrategyVO suppressStrategyVO);

    Result<Map<String, Object>> getAutoStrategyAndRegionName(VmAlarmSuppressStrategyVO suppressStrategyVO);

    Result tranformPT(VmAlarmAreaSuppressVO areaSuppressVo);

    Result add(VmAlarmSuppressStrategyVO vmAlarmSuppressStrategyVo);

    Result mod(VmAlarmSuppressStrategyVO vmAlarmSuppressStrategyVo);

    Result modStatus(VmAlarmSuppressStrategyVO vmAlarmSuppressStrategyVo);

    Result ptTransform(VmAlarmAreaSuppressVO areaSuppressVo);

    Result del(Integer strategyId);

    Result checkAlarmSuppressName(VmAlarmSuppressStrategyVO vmAlarmSuppressStrategyVo);

    Result<SysLntlagConfigVO> getLntlagConfig(VmAlarmAreaSuppressVO vo);

    Result queryPtzPositionPtz(VmAlarmAreaSuppressVO vo);

    Result getAreaSuppress(VmAlarmAreaSuppressVO vo);

    Result getAreaSuppressList(VmAlarmAreaSuppressVO vo);

    Result areaSuppressCheck(VmAlarmAreaSuppressVO vo);

}
