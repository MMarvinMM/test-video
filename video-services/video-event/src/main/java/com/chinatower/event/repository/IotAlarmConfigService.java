package com.chinatower.event.repository;

import com.chinatower.common.entity.Result;
import com.chinatower.event.dataobject.AlarmTypeLevelDO;
import com.chinatower.event.entity.dto.IotAlarmConfigDTO;
import com.chinatower.event.vo.IotAlarmConfigRequest;
import com.chinatower.event.vo.IotAlarmConfigVo;
import com.chinatower.event.vo.IotDeviceInfoVo;

import java.util.List;
import java.util.Map;

/**
 * 物联告警配置相关接口
 * @author lishihao
 * @date 2022-7-20
 * @requestID 物联告警配置（铁塔&客户）
 */
public interface IotAlarmConfigService {

    /**
     * 分页查询物联网告警配置信息
     * @author lishihao
     * @param model
     * @return Result<Object>
     * @date 2022-7-20
     * @requestID 物联告警配置（铁塔&客户）
     */
    Result<Object> queryIotAlarmConfigList(IotAlarmConfigDTO model);


    /**
     * 新增物联告警配置
     * @author lishihao
     * @param iotAlarmConfigVo
     * @return Result<Object>
     * @date 2022-7-20
     * @requestID 物联告警配置（铁塔&客户）
     */
    Result<Object> insertIotAlarmConfig(IotAlarmConfigVo iotAlarmConfigVo);

    /**
     * 编辑物联告警配置
     * @author lishihao
     * @param iotAlarmConfigVo
     * @return Result<Object>
     * @date 2022-7-22
     * @requestID 物联告警配置（铁塔&客户）
     */
    Result<Object> updateIotAlarmConfig(IotAlarmConfigVo iotAlarmConfigVo);

    /**
     * 删除物联告警配置
     * @author lishihao
     * @param configId
     * @return Result<String>
     * @date 2022-7-22
     * @requestID 物联告警配置（铁塔&客户）
     */
    Result<String> deleteIotAlarmConfig(String configId);


    /**
     * 查询单个告警配置信息
     * @author lishihao
     * @param configId
     * @return Result<IotAlarmConfigVo>
     * @date 2022-7-22
     * @requestID 物联告警配置（铁塔&客户）
     */
    Result<IotAlarmConfigVo> getIotAlarmConfig(String configId);

    /**
     * 根据ID改变物联告警状态
     * @author lishihao
     * @param iotAlarmConfigVo
     * @return Result<Object>
     * @date 2022-7-22
     * @requestID 物联告警配置（铁塔&客户）
     */
    Result<Object> changeIotAlarmStatus(IotAlarmConfigVo iotAlarmConfigVo);

    /**
     * 获取当前客户下所有订购的有效的物联设备
     * @author lishihao
     * @param iotAlarmConfigVo
     * @return Result<Object>
     * @date 2022-7-24
     * @requestID 物联告警配置（铁塔&客户）
     */
    Result<Object> getIotDevice(IotAlarmConfigRequest iotAlarmConfigVo);

    /**
     * 获取物联设备对应的物联因子
     * @author lishihao
     * @param iotDeviceInfoVoList
     * @return Result<Object>
     * @date 2022-7-24
     * @requestID 物联告警配置（铁塔&客户）
     */
    Result<Object> getIotFactorByDeviceCode(List<IotDeviceInfoVo> iotDeviceInfoVoList);

    /**
     * 获取客户所有的物联因子
     * @author lishihao
     * @param iotAlarmConfigVo
     * @return Result<Object>
     * @date 2022-7-24
     * @requestID 物联告警配置（铁塔&客户）
     */
    Result<Object> getAllFactor(IotAlarmConfigRequest iotAlarmConfigVo);

    /**
     * 校验物联告警配置名称是否重复
     * @author lishihao
     * @param iotAlarmConfigVo
     * @return Result<Object>
     * @date 2022-8-8
     * @requestID 物联告警配置（铁塔&客户）
     */
    Result<Object> checkIotAlarmConfigName(IotAlarmConfigVo iotAlarmConfigVo);

    /**
     * 校验物联告警类型配置是否重复
     * @author cuisj
     * @param iotAlarmConfigVo
     * @return Result<Object>
     * @date 2023-11-02
     * @requestID 物联告警配置（组户）
     */
    Result<Object> checkIotAlarmType(IotAlarmConfigVo iotAlarmConfigVo);

    /**
     * 获取默认名称
     * @author liusq
     * @return Result<Object>
     * @date 2022-8-23
     * @requestID 物联告警配置（铁塔&客户）
     */
    Result<Object> getAutoConfigName();

    /**
     * 查询配置的物联设备信息
     * @param params
     * @return
     */
    Result<Object> queryIotDeviceAndFactorInfo(Map<String, Object> params);

    Result<Object> queryAlarmLevelByTypeId (AlarmTypeLevelDO alarmTypeLevelDO);

    Result<Object> queryIotDeviceType ();

}
