package com.chinatower.event.repository;

import com.chinatower.common.entity.Result;
import com.chinatower.event.vo.entity.VmAlarmAreaSuppressVO;
import com.chinatower.event.vo.entity.VmAlarmSuppressStrategyVO;

/**
 * 功能说明: 告警压制策略 service
 * @author: li.xufu
 * @date: 2022/5/13
 * @requestID 需求单号及需求名称
 */
public interface StrategyService {

    /**
     * 功能说明: 告警策略新增
     * @author: li.xufu
     * @date: 2022/5/13
     * @param vmAlarmSuppressStrategyVo
     * @return: int
     */
    Result add(VmAlarmSuppressStrategyVO vmAlarmSuppressStrategyVo);

    /**
     * 功能说明: 告警策略修改
     * @author: li.xufu
     * @date: 2022/5/13
      * @param vmAlarmSuppressStrategyVo
     * @return: ProcessResult
     */
    Result mod(VmAlarmSuppressStrategyVO vmAlarmSuppressStrategyVo);

    /**
     * 功能说明: 告警策略删除
     * @author: li.xufu
     * @date: 2022/5/13
     * @param strategyVo
     * @return: ProcessResult
     */
    Result del(VmAlarmSuppressStrategyVO strategyVo);

    /**
     * 功能说明: PT转化为百分比坐标
     * @author: li.xufu
     * @date: 2022/5/17
     * @param areaSuppressVo
     * @return: ProcessResult
     */
    Result ptTransform(VmAlarmAreaSuppressVO areaSuppressVo);

    /**
     * 功能说明: 新增区域实时进行坐标转绘
     * @author: li.xufu
     * @date: 2022/5/19
     * @param areaSuppressVo
     * @return: ProcessResult
     * @version:
     */
    Result tranformPT(VmAlarmAreaSuppressVO areaSuppressVo);
}
