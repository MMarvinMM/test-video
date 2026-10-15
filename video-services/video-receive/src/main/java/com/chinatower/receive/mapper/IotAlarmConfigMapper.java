package com.chinatower.receive.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chinatower.event.entity.dto.ReceiveIotAlarmConfigDto;
import com.chinatower.event.entity.dto.ReceiveIotAlarmDeviceDto;
import com.chinatower.event.entity.dto.ReceiveIotAlarmFactorDto;
import com.chinatower.event.entity.dto.ReceiveIotAlarmGroupsDto;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import com.chinatower.receive.vo.dto.IotAlarmConfigDo;
import com.chinatower.receive.vo.dto.IotFactorVo;

import java.util.List;

/**
 * 物联告警配置Mapper
 * @author lishihao
 * @date 2022-7-20
 * @requestID 物联告警配置（铁塔&客户）
 */
public interface IotAlarmConfigMapper extends BaseMapper<IotAlarmConfigDo> {

    /**
     * 查询所有物联配置信息
     * @return
     */
    @DS(EbikeDataBaseConstant.BASE)
    List<ReceiveIotAlarmConfigDto> selectAllIotConfig();

    /**
     * 查询所有物联配置下物联规则信息
     * @return
     */
    @DS(EbikeDataBaseConstant.BASE)
    List<ReceiveIotAlarmGroupsDto> selectAllIotGroupsInConfig(List<String> configIdList);

    /**
     * 查询所有物联配置下物联设备信息
     * @return
     */
    @DS(EbikeDataBaseConstant.BASE)
    List<ReceiveIotAlarmDeviceDto> selectAllIotDeviceInConfig(List<String> groupIdList);

    /**
     * 查询所有物联配置下物联因子信息
     * @return
     */
    @DS(EbikeDataBaseConstant.BASE)
    List<ReceiveIotAlarmFactorDto> selectAllIotFactorInConfig(List<String> groupIdList);

    /**废弃
     * 根据物联因子查询因子详细信息
     * @return
     */
    @DS(EbikeDataBaseConstant.BASE)
    List<IotFactorVo> getMonitorInfo(List<String> factoryCodeList);

}
