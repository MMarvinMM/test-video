package com.chinatower.event.repository.impl;

import com.chinatower.common.core.exception.CustomException;
import com.chinatower.event.constants.Constants;
import com.chinatower.event.dto.AnalysisDTO;
import com.chinatower.event.dto.EventNumByAreaDTO;
import com.chinatower.event.dto.EventNumByHourDTO;
import com.chinatower.event.dto.EventNumNewDTO;
import com.chinatower.event.entity.vo.AreaEventVO;
import com.chinatower.event.mapper.AnalysisMapper;
import com.chinatower.event.repository.IStatisticsRepository;
import com.chinatower.platform.annotation.DataScope;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import com.chinatower.platform.entity.admin.SysArea;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Copyright: Copyright(c) 2019 iwhalecloud
 * <p>
 * 类说明：TODO
 * <p>
 * 类名称: StatisticsRepositoryImpl.java
 *
 * @author su.tingning
 * @version v1.0.0
 * @date 2022/3/14 11:12
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ------------------------------------------------------------
 */
@Slf4j
@Service
public class StatisticsRepositoryImpl implements IStatisticsRepository {
    @Autowired
    private AnalysisMapper analysisMapper;

    /**
     * 功能描述 获取事件统计数量
     * 根据角色权限获取大屏告警事件上方事件数据统计
     *
     * @return EventNumNewDTO
     * @author chenmf
     * @date 2022/6/7
     * @version
     */
    @Override
    @DataScope(dbType = EbikeDataBaseConstant.BASE,deviceColumn="dataTable.device_code",tenantColumn="dataTable.tenant_id",
            industryCode="dataTable.industry_code", alarmSourceColumn="dataTable.warning_source",
            alarmSourceVal="1,2,3,4,5,6,7,8,9,10",areaPro = "dataTable.province_id"
            ,areaCity = "dataTable.city_id",areaCode = "dataTable.county_id")
    public EventNumNewDTO getEventNumNew(AnalysisDTO analysisDTO) {

        // 获取当前登录人
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null || loginUser.getUser() == null) {
            // 无法获取当前登录人，返回错误信息
            throw new CustomException(Constants.NO_USER_INFO);
        }

        SysUser user = loginUser.getUser();
        Long[] roleIds = user.getRoleIds();

        analysisDTO.setTenantId(user.getTenantId());
        EventNumNewDTO data = analysisMapper.getEventNumNew(analysisDTO);
        //根据角色控制工作台及大屏的数量展示
//        if (!Constants.ONE.equals(user.getIsTenantAdmin())) {
//            //待研判
//            if (!CommonUtils.ifJudgeRole(roleIds)) {
//                data.setPenddingJudged(0);
//            }
//            //待调度
//            if (!CommonUtils.ifDispatchRole(roleIds)) {
//                data.setPendingDispatched(0);
//            }
//            //待处置，租户下用户查询本组织或本人的待处置数量
//            if (CommonUtils.ifHandleRole(roleIds)) {
//
//                //全量统计
//                //获取当前月的表名
//                // 日期转化为字符串，只有年月
//                String dateStr = DateUtil.dateToStringYM(new Date());
//                boolean dateFlag = true;
//                int disposalSum = 0;
//                while (dateFlag) {
//                    DateUtils.parseDate(dateStr);
//                    String tableName = TableUtil.getTableName(DateUtil.stringToDateYM(dateStr), TableConstant.WARN_ORDER_TEBLE);
//                    disposalSum += analysisMapper.getPeddingDisposal(user.getUserId(), user.getDeptId(), tableName);
//                    if (TableUtil.getMinTableDate().equals(dateStr)) {
//                        dateFlag = false;
//                    } else {
//                        dateStr = CommonUtils.getlastMonth(dateStr);
//                    }
//                }
//                data.setPeddingDisposal(disposalSum);
//            } else {
//                data.setPeddingDisposal(0);
//            }
//            //待核实
//            if (!CommonUtils.ifCheckRole(roleIds)) {
//                data.setPendingApprove(0);
//            }
//        }
        return data;
    }

    /**
     * 告警事件统计-事件高发区域
     *
     * @return List<EventNumByAreaDTO>
     * @author chenmf
     * @date 2022/6/7
     * @version
     */
    @Override
    @DataScope(dbType = EbikeDataBaseConstant.BASE,deviceColumn="dataTable.device_code",tenantColumn="dataTable.tenant_id",
            industryCode="dataTable.industry_code", alarmSourceColumn="dataTable.warning_source",
            alarmSourceVal="1,2,3,4,5,6,7,8,9,10",areaPro = "dataTable.province_id"
            ,areaCity = "dataTable.city_id",areaCode = "dataTable.county_id")
    public List<EventNumByAreaDTO> getEventNumByArea(AnalysisDTO analysisDTO) {
        return analysisMapper.getEventNumByArea(analysisDTO);
    }

    /**
     * 获取事件数量-曲线图用
     *
     * @return List<EventNumByHourDTO>
     * @author chenmf
     * @date 2022/6/7
     * @version
     */
    @Override
    @DataScope(dbType = EbikeDataBaseConstant.BASE,deviceColumn="dataTable.device_code",tenantColumn="dataTable.tenant_id",
            industryCode="dataTable.industry_code", alarmSourceColumn="dataTable.warning_source",
            alarmSourceVal="1,2,3,4,5,6,7,8,9,10",areaPro = "dataTable.province_id"
            ,areaCity = "dataTable.city_id",areaCode = "dataTable.county_id")
    public List<EventNumByHourDTO> getEventNumByHour(AnalysisDTO analysisDTO) {
        return analysisMapper.getEventNumByHour(analysisDTO);
    }

    /**
     * 告警事件统计-事件高发区域全景TOP5
     *
     * @return List<AreaEventVO>
     * @author chenmf
     * @date 2022/6/7
     * @version
     */
    @Override
    @DataScope(dbType = EbikeDataBaseConstant.BASE,deviceColumn="dataTable.device_code",tenantColumn="dataTable.tenant_id",
            industryCode="dataTable.industry_code", alarmSourceColumn="dataTable.warning_source",
            alarmSourceVal="1,2,3,4,5,6,7,8,9,10",areaPro = "dataTable.province_id"
            ,areaCity = "dataTable.city_id",areaCode = "dataTable.county_id")
    public List<AreaEventVO> getEventNumListByArea(AnalysisDTO analysisDTO) {

        if (analysisDTO.getAreaBoxNum() == null || (analysisDTO.getAreaBoxNum() != Constants.ZERO_NUM
                && analysisDTO.getAreaBoxNum() != Constants.ONE_NUM && analysisDTO.getAreaBoxNum() != Constants.TWO_NUM && analysisDTO.getAreaBoxNum() != Constants.THREE_NUM)) {
            throw new CustomException("当前数据仅支持选择到区县维度");
        }
        // 查询排名前5的告警区域
        List<AreaEventVO> eventVOList = analysisMapper.getEventNumListByArea(analysisDTO);
        if (CollectionUtils.isEmpty(eventVOList)) {
            return new ArrayList<>();
        }
        ArrayList<String> areaCodes = new ArrayList<>(5);
        eventVOList.forEach(vo -> {
            areaCodes.add(vo.getProvinceId());
            areaCodes.add(vo.getCityId());
            areaCodes.add(vo.getCountyId());
            areaCodes.add(vo.getStreetId());
        });
        // 查询用到的省市区乡镇信息， 后面直接组装
        List<SysArea> areas = analysisMapper.getSysAreas(areaCodes, 0);
        Map<String, String> resultMap = areas.stream().collect(Collectors.toMap(SysArea::getAreaCode, SysArea::getAreaName));

        // 替换掉原有的省市县名字
        StringBuilder regionName;
        for (AreaEventVO eventVO : eventVOList) {
            regionName = new StringBuilder();
            regionName.append(MapUtils.getString(resultMap, eventVO.getProvinceId(), ""))
                    .append(MapUtils.getString(resultMap, eventVO.getCityId(), ""))
                    .append(MapUtils.getString(resultMap, eventVO.getCountyId(), ""))
                    .append(MapUtils.getString(resultMap, eventVO.getStreetId(), ""));
            eventVO.setRegionName(regionName.toString());
        }
        return eventVOList;
    }
}
