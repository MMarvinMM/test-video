package com.chinatower.event.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chinatower.event.dataobject.*;
import com.chinatower.event.entity.dto.ReceiveIotAlarmConfigDto;
import com.chinatower.event.entity.dto.ReceiveIotAlarmDeviceDto;
import com.chinatower.event.entity.dto.ReceiveIotAlarmFactorDto;
import com.chinatower.event.entity.dto.ReceiveIotAlarmGroupsDto;
import com.chinatower.event.vo.AlarmLevelVO;
import com.chinatower.event.vo.IotAlarmConfigRequest;
import com.chinatower.event.vo.IotDeviceInfoVo;
import com.chinatower.event.vo.IotFactorVo;
import com.chinatower.platform.annotation.DataScope;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import com.chinatower.platform.entity.admin.SysUser;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 物联告警配置Mapper
 * @author lishihao
 * @date 2022-7-20
 * @requestID 物联告警配置（铁塔&客户）
 */
public interface IotAlarmConfigMapper extends BaseMapper<IotAlarmConfigDo> {

    /**
     * 根据客户ID获取客户订购的所有有效设备
     * @param iotAlarmConfigVo
     * @return List<IotDeviceInfoVo>
     * @author lishihao
     * @date 2022-8-05
     * @requestID 物联告警配置（铁塔&客户）
     */
    List<IotDeviceInfoVo> getIotDeviceInfoVoList(IotAlarmConfigRequest iotAlarmConfigVo);

    /**
     * 获取物联设备对应的因子信息
     * @param deviceCodeList
     * @return List<IotFactorVo>
     * @author lishihao
     * @date 2022-8-05
     * @requestID 物联告警配置（铁塔&客户）
     */
    List<IotFactorVo>  getFactorByDeviceCode(@Param("deviceCodeList")List<String> deviceCodeList,@Param("tenantId") String tenantId);

    /**
     * 获取客户下所有的物联因子信息
     * @param iotAlarmConfigVo
     * @return List<IotFactorVo>
     * @author lishihao
     * @date 2022-8-05
     * @requestID 物联告警配置（铁塔&客户）
     */
    List<IotFactorVo> getAllFactor(IotAlarmConfigRequest iotAlarmConfigVo);

    /**
     * 新增物联因子
     * @param iotFactorVoList
     * @return int
     * @author lishihao
     * @date 2022-8-04
     * @requestID 物联告警配置（铁塔&客户）
     */
    int insertFactor(@Param("iotFactorVoList") List<IotFactorVo> iotFactorVoList);

    /**
     * 新增物联设备
     * @param iotDeviceVOList
     * @return int
     * @author lishihao
     * @date 2022-8-04
     * @requestID 物联告警配置（铁塔&客户）
     */
    int insertDevice(@Param("iotDeviceVOList") List<IotAlarmDeviceDo> iotDeviceVOList);

    /**
     * 新增物联多组规则
     * @param iotConfigGroups
     * @return int
     * @author cuisj
     * @date 2024-08-06
     * @requestID 物联告警配置（铁塔&客户）
     */
    int insertConfigGroups(IotAlarmConfigGroupsDo iotConfigGroups);

    List<String> queryGroupIdsById(String id);

    List<IotAlarmConfigGroupsDo> queryConfigGroupsById(List<String> idList);

    List<IotAlarmDeviceDo> queryConfigDeviceById(List<String> idList);
    List<IotFactorVo> queryConfigFactorById(List<String> idList);


    void deleteConfigGroups(int id);

    int deleteConfigDevice(List<String> idList);
    int deleteConfigFactor(List<String> idList);

    void updateConfigGroups(int id);

    int updateConfigDevice(List<String> idList);
    int updateConfigFactor(List<String> idList);



    /**
     * 查询根据ID查询user_name
     * @param userIdList
     * @return int
     * @author List<SysUser>
     * @date 2022-8-15
     * @requestID 物联告警配置（铁塔&客户）
     */
    List<SysUser> queryUserInfoList(List<String> userIdList,String userName);

    /**
     * 获取默认名称
     * @param config
     * @return int
     * @author List<SysUser>
     * @date 2022-8-15
     * @requestID 物联告警配置（铁塔&客户）
     */
    String getAutoConfigName(IotAlarmConfigDo config);

    /**
     * 查询该租户行业下的物联因子信息
     * @param param
     * @return
     */
    List<IotFactorVo> selectFactorInfos(@Param("param") Map<String, Object> param);

    /**
     * 根据设备编码查询物联设备信息
     * @param param
     * @return
     */
    List<Map> selectDeviceInfos(@Param("param") Map<String, Object> param);

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
    List<ReceiveIotAlarmGroupsDto> selectAllIotGroupsInConfig (List<String> configIdList);

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

    /**
     * 查询所有物联设别下对应物联因子信息
     * @return
     */
    @DS(EbikeDataBaseConstant.BASE)
    List<IotFactorVo> getDeviceAllFactor();

    /**
     * 查询所有物联设别下对应物联因子信息
     * @return
     */
    @DS(EbikeDataBaseConstant.BASE)
    AlarmLevelVO queryAlarmLevelByTypeId(AlarmTypeLevelDO alarmTypeLevelDO);


    /**
     * 查询所有物联设别下对应物联因子信息
     * @return
     */
    @DS(EbikeDataBaseConstant.BASE)
    @DataScope(dbType = EbikeDataBaseConstant.BASE,tenantColumn="a.tenant_id", industryCode = "a.industry_code",deviceColumn="a.device_code")
    List<String> queryIotDeviceType(IotAlarmConfigRequest iotAlarmConfigRequest);
}
