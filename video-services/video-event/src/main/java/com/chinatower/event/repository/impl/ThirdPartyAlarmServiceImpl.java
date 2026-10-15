package com.chinatower.event.repository.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.event.constants.Constants;
import com.chinatower.event.entity.vo.AlarmTVo;
import com.chinatower.event.entity.vo.ThirdAlarmVo;
import com.chinatower.event.entity.vo.ThirdPartyAlarmVo;
import com.chinatower.event.mapper.ThirdPartyAlarmMapper;
import com.chinatower.event.repository.IWarningOrderRepository;
import com.chinatower.event.repository.ThirdPartyAlarmService;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import com.chinatower.system.api.request.alarmType.ThirdPartyAlarmTypeVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 第三方告警配置ServiceImpl
 */
@Slf4j
@Service
public class ThirdPartyAlarmServiceImpl implements ThirdPartyAlarmService {

    @Autowired
    private ThirdPartyAlarmMapper thirdPartyAlarmMapper;

    @Autowired
    private IWarningOrderRepository warningOrderRepository;

    @Autowired
    private SystemFeignClient systemFeignClient;

    /**
     * 批量插入
     * @param list
     */
    @Override
    public Integer insertBatch(List<ThirdPartyAlarmVo> list) {

        //获取当前登录人
        LoginUser loginUser = SecurityUtils.getLoginUser();
        //获取当前用户名
        String userName = loginUser.getUser().getUserName();
        //获取客户id
        String tenantId = loginUser.getUser().getTenantId();
        //获取行业应用
        String industryCode = loginUser.getUser().getIndustryCode();
        //获取行业应用版本
        String appVerCode = loginUser.getUser().getAppVerCode();

        //判断list集合是否为空
        if (CollectionUtil.isEmpty(list)){
            return 0;
        }
        // 1.7.20 同步第三方告警类型新增/删除数据
        try {
            syncThirdPartyAlarmType(tenantId, industryCode, appVerCode, list);
        } catch (Exception e) {
            log.info("调用第三方告警类型同步新增/删除异常：{}", e);
        }
        
        //根据tenantId和industryCode逻辑删除
        thirdPartyAlarmMapper.updateByTenantId(tenantId, industryCode);

        for (ThirdPartyAlarmVo thirdPartyAlarmVo : list) {
            thirdPartyAlarmVo.setCreator(userName);
            thirdPartyAlarmVo.setTenantId(tenantId);
            thirdPartyAlarmVo.setIndustryCode(industryCode);
            thirdPartyAlarmVo.setAppVerCode(appVerCode);
        }
        //往vm_third_party_alarm表中插入数据
        return thirdPartyAlarmMapper.insertBatch(list);
    }

    /**
     * 同步第三方告警类型新增/删除数据
     *
     * @param tenantId     租户ID
     * @param industryCode 行业应用编码
     * @param appVerCode   行业应用版本
     * @param list      新增的告警列表
     */
    private void syncThirdPartyAlarmType(String tenantId, String industryCode, String appVerCode, List<ThirdPartyAlarmVo> list) {
        ThirdPartyAlarmTypeVO alarmTypeVO = new ThirdPartyAlarmTypeVO();
        alarmTypeVO.setTenantId(tenantId);
        alarmTypeVO.setIndustryCode(industryCode);
        alarmTypeVO.setAppVerCode(appVerCode);

        // 查询当前数据库中已有的第三方告警类型
        List<ThirdPartyAlarmVo> thirdPartyAlarmList = thirdPartyAlarmMapper.details(tenantId, industryCode);
        // 提取已有告警类型，用于删除
        List<String> delAlarmType = thirdPartyAlarmList.stream()
                .map(ThirdPartyAlarmVo::getAlarmType)
                .collect(Collectors.toList());
        alarmTypeVO.setDeleteAlarmTypeList(delAlarmType);

        // 提取新增告警类型
        List<String> addAlarmType = list.stream()
                .map(ThirdPartyAlarmVo::getAlarmType)
                .collect(Collectors.toList());
        alarmTypeVO.setAddAlarmTypeList(addAlarmType);

        systemFeignClient.thirdPartyAlarmSync(alarmTypeVO);
        log.info("同步第三方告警类型成功，tenantId: {}, industryCode: {}, 删除类型: {}, 新增类型: {}", tenantId, industryCode, delAlarmType, addAlarmType);
    }

    /**
     * 根据 租户ID、行业编码、删除标识查询详情
     * @param tenantId 租户ID
     * @param industryCode 行业编码
     * @return 查询详情
     */
    @Override
    public List<ThirdAlarmVo> details(String tenantId, String industryCode) {
        //根据 租户ID、行业编码、删除标识查询详情
        List<ThirdPartyAlarmVo> list = thirdPartyAlarmMapper.details(tenantId, industryCode);

        List<ThirdAlarmVo> thirdAlarmVoList = new ArrayList<>();
        //判断list不为空
        if (StringUtils.isNotNull(list)) {
            //获取所有的alramType
            List<Map> alramTypeList = warningOrderRepository.getAlarmTypeList();
            for (ThirdPartyAlarmVo vo : list) {
                ThirdAlarmVo thirdAlarmVo = new ThirdAlarmVo();
                AlarmTVo alarmTVo = new AlarmTVo();
                thirdAlarmVo.setWarningSourceName(vo.getWarningSourceName());
                alarmTVo.setId(vo.getAlarmType());
                alramTypeList.forEach(item -> {
                    //根据alarmType获取对应的alarmName
                    if (item.get(Constants.PARAM_TYPE_VALUE_).toString().equals(vo.getAlarmType())) {
                        alarmTVo.setName(String.valueOf(item.get(Constants.PARAM_TYPENAME)));
                    }

                });
                thirdAlarmVo.setAlarmTVo(alarmTVo);
                thirdAlarmVoList.add(thirdAlarmVo);
            }
        }
        return thirdAlarmVoList;
    }


    /**
     * 根据租户id逻辑删除
     * @param tenantId 租户id
     * @return
     */
    @Override
    public Integer updateByTenantId(String tenantId,String industryCode) {
        return thirdPartyAlarmMapper.updateByTenantId(tenantId,industryCode);
    }


    /**
     * 判断该租户是否开启第三方告警
     * @param tenantId 租户ID
     * @param industryCode 行业编码
     * @return
     */
    @Override
    public boolean isOpenThirdPartyAlarm(String tenantId, String industryCode) {
        Integer integer = thirdPartyAlarmMapper.isOpenThirdPartyAlarm(tenantId, industryCode);
        return (StringUtils.isNull(integer) || integer == 0) ? false : true;
    }

    /**
     * 判断第三方告警配置表中 该租户是否有第三方告警数据
     * @param tenantId 租户ID
     * @param industryCode 行业编码
     * @return
     */
    @Override
    public boolean tenantThirdPartyAlarm(String tenantId, String industryCode) {
        Integer integer = thirdPartyAlarmMapper.tenantThirdPartyAlarm(tenantId, industryCode);
        return (integer != 0) ? true : false;
    }

    /**
     * 根据告警类型、租户ID和行业编码在流程模板关联告警类型表中查找对应的数据
     * @param alarmType 告警类型
     * @param tenantId 租户ID
     * @param industryCode 行业编码
     * @return
     */
    @Override
    public boolean isThirdAlarmType(String alarmType, String tenantId, String industryCode) {
        Integer integer = thirdPartyAlarmMapper.isThirdAlarmType(alarmType, tenantId, industryCode);
        //true就代表没数据 false就代表有数据
        return (StringUtils.isNull(integer) || integer == 0) ? true : false;
    }
}

