package com.chinatower.receive.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.common.entity.Result;
import com.chinatower.event.entity.dto.*;
import com.chinatower.event.entity.vo.OrderInfoVO;
import com.chinatower.event.entity.vo.ReceiveAlarmVO;
import com.chinatower.event.entity.vo.SysUserVO;
import com.chinatower.event.entity.vo.TenantDeviceEventRelVO;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import com.chinatower.platform.entity.admin.SysDept;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.entity.order.AlarmOrderDTO;
import com.chinatower.platform.model.vo.DeviceRefPointVO;
import com.chinatower.receive.vo.dto.AlarmTypeInfoDTO;
import com.chinatower.receive.vo.dto.UavFlyInfoVo;
import com.chinatower.receive.vo.entity.*;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import com.chinatower.receive.mapper.IDeviceMapper;
import com.chinatower.receive.mapper.ISysMapper;
import com.chinatower.receive.service.DeviceRepository;
import com.chinatower.receive.vo.dto.OrderCustomerVO;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Copyright: Copyright(c) 2019 iwhalecloud
 * <p>
 * 类说明：TODO
 * <p>
 * 类名称: DeviceInfoRepositoryImpl.java
 *
 * @author su.tingning
 * @version v1.0.0
 * @date 2022/2/28 09:34
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ------------------------------------------------------------
 */
@Slf4j
@Service
@DS(EbikeDataBaseConstant.BASE)
public class DeviceRepositoryImpl implements DeviceRepository {

    @Autowired
    private IDeviceMapper IDeviceMapper;

    @Autowired
    private ISysMapper ISysMapper;

    @Autowired
    private SystemFeignClient systemFeignClient;

    /**
     * 通过设备编码获取租户
     *
     * @param deviceCode
     * @return
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<OrderCustomerVO> getCustomersByDevice(String deviceCode, String applicationCode, ReceiveAlarmVO receiveAlarmVO) {
        return IDeviceMapper.getCustomersByDevice(deviceCode, applicationCode, receiveAlarmVO);
    }

    /**
     * 通过设备编码和算法编码获取租户
     *
     * @param deviceCode
     * @param algorithmCode
     * @param taskId
     * @return
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<OrderCustomerVO> getCustomersList(String deviceCode, String algorithmCode, String taskId, String alarmSource, String localNode) {
        return IDeviceMapper.getCustomersList(deviceCode, algorithmCode, taskId, alarmSource, localNode);
    }

    /**
     * 根据航线ID获取租户
     *
     * @param routeId
     * @return
     */
    @Override
    public List<OrderCustomerVO> getCustomersByRouteId(String routeId) {
        return IDeviceMapper.getCustomersByRouteId(routeId);
    }

    /**
     * 获取符合条件的铁塔运营研判人员
     *
     * @param list
     * @return
     */
    @Override
    public List<String> getUserForOperation(List<String> list) {
        return IDeviceMapper.getUserForOperation(list);
    }

    /**
     * 根据角色ID查询角色用户
     *
     * @param roleIdList
     * @param tenantId
     * @return
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<SysUser> getUserInfoByRoleIds(List<String> roleIdList, String tenantId) {

        return IDeviceMapper.getUserInfoByRoleIds(roleIdList, tenantId);
    }

    /**
     * 根据角色ID查询角色用户
     *
     * @param roleIdList
     * @param tenantId
     * @return
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<SysUser> getUserInfoByLongRoleIds(List<Long> roleIdList, String tenantId) {

        return IDeviceMapper.getUserInfoByLongRoleIds(roleIdList, tenantId);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<SysUser> getUserInfoByLongRoleIdsAndIndustryCode(List<Long> roleIdList, String tenantId, String industryCode) {
        return IDeviceMapper.getUserInfoByLongRoleIdsAndIndustryCode(roleIdList, tenantId, industryCode);
    }

    /**
     * 查询告警类型
     *
     * @param warningTypeId
     * @return
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public AlarmTypeInfoDTO getAlarmTypeInfoById(String warningTypeId) {
        return IDeviceMapper.getAlarmTypeInfoById(warningTypeId);
    }

    /**
     * 查询客户管理员信息
     *
     * @param tenantId
     * @return
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<SysUserVO> getTenantAdminUser(String tenantId) {
        return IDeviceMapper.getTenantAdminUser(tenantId);
    }


    /**
     * 根据用户ID查询用户信息
     *
     * @param userIdList
     * @return
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<SysUserVO> getUserInfoByUserIds(List<String> userIdList) {
        if (!CollectionUtils.isEmpty(userIdList)) {
            return IDeviceMapper.getUserInfoByUserIds(userIdList);
        }
        return new ArrayList<>();
    }

    /**
     * 根据用户ID查询用户信息
     *
     * @param userIdList,industryCode
     * @return
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<SysUserVO> getUserInfoByUserIdsAndIndustryCode(List<String> userIdList, String industryCode) {
        if (!CollectionUtils.isEmpty(userIdList)) {
            return IDeviceMapper.getUserInfoByUserIdsAndIndustryCode(userIdList, industryCode);
        }
        return new ArrayList<>();
    }

    /**
     * 查询设备别名
     *
     * @param tenantDeviceRelDTOList
     * @return
     */
    @Override
    public List<TenantDeviceEventRelVO> getDeviceRel(List<TenantDeviceRelDTO> tenantDeviceRelDTOList) {
        TenantDeviceRelDTO dto = new TenantDeviceRelDTO();
        dto.setDeviceCodes(
                tenantDeviceRelDTOList.stream().map(TenantDeviceRelDTO::getDeviceCode).collect(Collectors.toList())
        );
        dto.setChannelCodes(
                tenantDeviceRelDTOList.stream().map(TenantDeviceRelDTO::getChannelCode).collect(Collectors.toList())
        );
        if (!StringUtils.isEmpty(tenantDeviceRelDTOList) && tenantDeviceRelDTOList.size() > 0) {
            dto.setTenantId(tenantDeviceRelDTOList.get(0).getTenantId());
        }
        List<TenantDeviceEventRelVO> list = IDeviceMapper.getDeviceRel(dto);

//        for (TenantDeviceRelDTO tenantDeviceRelDTO : tenantDeviceRelDTOList) {
//            tenantDeviceRelVOList.addAll(deviceMapper.getDeviceRel(tenantDeviceRelDTO));
//        }
        return list;
    }

    /**
     * 通过网格id获取网格员
     *
     * @param gridId
     * @return
     */
    @Override
    public String getGridKeeperByGridId(String gridId) {
        return IDeviceMapper.getGridKeeperByGridId(gridId);
    }

    /**
     * 通过网格id获取网格员
     *
     * @param gridId
     * @return
     */
    @Override
    public List<Map> getGridKeeperByGridIds(String gridId) {
        return IDeviceMapper.getGridKeeperByGridIds(gridId);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public String getGridUserByGridId(String gridId) {
        return IDeviceMapper.getGridUserByGridId(gridId);
    }

    /**
     * 查询摄像机信息
     *
     * @param deviceCode
     * @return
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public DeviceSiteReceiveVO queryDevice(String deviceCode, String channelCode) {
        return IDeviceMapper.queryDevice(deviceCode, channelCode);
    }

    /**
     * 查询无人机飞行任务时间
     *
     * @param taskRecordId
     * @return
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public UavFlyInfoVo queryUavFlyInfo(String taskRecordId) {
        return IDeviceMapper.queryUavFlyInfo(taskRecordId);
    }

    /**
     * 查询物联网设备信息
     *
     * @return
     * @author chenmf
     * @date 2022/5/19
     * @version
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public DeviceSiteReceiveVO queryIotDevice(String deviceCode) {
        return IDeviceMapper.queryIotDevice(deviceCode);
    }

    /**
     * 查询雷达设备信息
     *
     * @return
     * @author daiyong
     * @date 2022/9/15
     * @version
     */
    @Override
    public DeviceSiteReceiveVO queryRadarDevice(String tenantId, String industryCode, String deviceCode) {
        return IDeviceMapper.queryRadarDevice(tenantId, industryCode, deviceCode);
    }

    /**
     * 查询物联网设备/雷达设备信息
     *
     * @return
     * @author luo.zhenghong
     * @date 2022/8/10
     * @version
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public DeviceSiteReceiveVO querySensorDevice(String deviceCode, int categoryCode) {
        return IDeviceMapper.querySensorDevice(deviceCode, categoryCode);
    }

    /**
     * 根据部门ID查询部门信息
     *
     * @param deptIdList
     * @return
     */
    @Override
    public List<SysDept> getSysDeptByDeptIds(List<String> deptIdList) {
        return IDeviceMapper.getSysDeptByDeptIds(deptIdList);
    }

    /**
     * 获取行政区划列表
     *
     * @return
     */
    @Override
    @Deprecated
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<Map> getAreaInfoList() {
        //从redis获取行政区划列表
        List<Map> areaInfoList = JSON.parseArray(RedisUtil.get("areaInfoList", RedisDBType.PRIMARY), Map.class);
        if (CollectionUtils.isEmpty(areaInfoList)) {
            areaInfoList = IDeviceMapper.getAreaInfo();
            RedisUtil.set("areaInfoList", JSON.toJSONString(areaInfoList), RedisDBType.PRIMARY);
        }
        return areaInfoList;
    }

    @Override
    public Map<String, String> getAreaNameCaffeineMap(List<String> areaCodes) {
        if (areaCodes == null) {
            areaCodes = new ArrayList<>();
        }
        Result<Map<String, String>> areaRedisMap = systemFeignClient.getAreaNameCaffeineMap(areaCodes);
        return areaRedisMap.getData();
    }

    /**
     * 获取告警类型
     *
     * @return
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<Map> getAlarmType() {
        return IDeviceMapper.getAlarmType();
    }

    /**
     * 通过用户id获取用户相关信息
     *
     * @param userId
     * @return
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Map<String, String> getUserInfoById(String userId) {
        return IDeviceMapper.getUserInfoById(userId);
    }

    /**
     * 获取设备别名
     *
     * @param list
     * @param tenantId
     * @return
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<Map> getDeviceRel(List<OrderInfoVO> list, String tenantId) {
        return IDeviceMapper.getDeviceRelList(list, tenantId);
    }

    /**
     * 获取通道别名
     *
     * @param list
     * @param tenantId
     * @return
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<Map> getChannelRel(List<OrderInfoVO> list, String tenantId) {
        return IDeviceMapper.getChannelRel(list, tenantId);
    }


    /**
     * 查询物联设备相关信息
     *
     * @return
     * @author lyy
     * @date 2024/3/10
     * @version
     */
    @Override
    public List<AlarmOrderByDeviceDTO> getAlarmOrderByIotDevice(AlarmOrderDTO dto) {

        if (!StringUtils.isEmpty(dto.getIotDeviceCodeOrName())) {
            return IDeviceMapper.getAlarmOrderByIotDevice(dto);
        } else {
            return null;
        }
    }

    /**
     * 查询领导批示数据
     *
     * @param dto
     * @return
     */
    @Override
    public List<SysUserVO> getLeaderByTenantId(SysUserDTO dto) {
        if (StringUtils.isEmpty(dto.getTenantId())) {
            return null;
        }
        return IDeviceMapper.getLeaderByTenantId(dto);
    }


    /**
     * 查询设备参数信息
     *
     * @param deviceCode
     * @return
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public SysLntlagConfigVO getLntlagConfig(String deviceCode) {
        return IDeviceMapper.getLntlagConfig(deviceCode);
    }


    /**
     * 通过传感器设备编码获取租户
     *
     * @return
     * @author chenmf
     * @date 2022/5/10
     * @version
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<OrderCustomerVO> getCustomersBySensorDevice(String[] deviceCode, String applicationCode, int categoryCode, String taskId, String alarmSource, String localNode) {
        return IDeviceMapper.getCustomersBySensorDevice(deviceCode, applicationCode, categoryCode, taskId, alarmSource, localNode);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public int getRadarTemplateCount(String taskId) {
        return IDeviceMapper.getRadarTemplateCount(taskId);
    }

    /**
     * 通过传感器设备编码获取租户
     *
     * @return
     * @author luo.zhenghong
     * @date 2022/8/4
     * @version
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<OrderCustomerVO> getCustomersBySensorDeviceList(List<String> deviceCodeList) {
        return IDeviceMapper.getCustomersBySensorDeviceList(deviceCodeList);
    }

    /**
     * 通过设备编码获取有效参考点
     *
     * @return
     * @author chenmf
     * @date 2022/5/23
     * @version
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<DeviceRefPointVO> getDevicePointsValid(String deviceCode) {
        return IDeviceMapper.getDevicePointsValid(deviceCode);
    }

    /**
     * 通过设备编码获取有效参考点
     *
     * @return
     * @author chenmf
     * @date 2022/5/23
     * @version
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<DeviceRefPointVO> getDevicePointsValidBatch(List<String> deviceCodeList) {
        return IDeviceMapper.getDevicePointsValidBatch(deviceCodeList);
    }


    /**
     * 查询摄像机信息
     *
     * @param deviceCodeList
     * @return
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<DeviceSiteReceiveVO> queryDeviceList(List<String> deviceCodeList) {
        return IDeviceMapper.queryDeviceList(deviceCodeList);
    }

    /**
     * 查询无人机设备信息
     *
     * @param deviceCode
     * @return
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public DeviceSiteReceiveVO queryUavDeviceList(String deviceCode) {
        return IDeviceMapper.queryUavDeviceList(deviceCode);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public TranslnglatVO handlerFeaturePT(TranslnglatVO translnglatRep, String deviceCode, String alarmCode) {
        //特征点：如果告警有框图，则是告警框图下边沿中心点的P值和T值，如果告警没有框图，则是告警图片中心点的P值和T值。
        if (!StringUtils.isEmpty(translnglatRep.getAlarmP()) && !StringUtils.isEmpty(translnglatRep.getAlarmT())) {
            String pan = translnglatRep.getAlarmP();
            String titl = translnglatRep.getAlarmT();
            //特征点表devic_code取值对比pt范围
            DeviceFeatureVO featureVO = new DeviceFeatureVO();
            featureVO.setDeviceCode(deviceCode);
            featureVO.setPtzP(Float.parseFloat(pan));
            featureVO.setPtzT(Float.parseFloat(titl));
            //计算特征点，如果有多条特征记录时，取库里的特征点，与当前的点计算距离，距离最近的作为最终特征点
            List<DeviceFeatureVO> vos = IDeviceMapper.getFeatureListByDevice(featureVO);
            //告警点的P、T值在设定的范围内，都定位到对应特征点的真实经纬度上。
            if (!CollectionUtils.isEmpty(vos)) {
                int minIndex = 0;
                for (int i = 0; i < vos.size(); i++) {
                    float mpClose = Math.abs(vos.get(minIndex).getPtzP() - Float.parseFloat(pan));
                    float mtClose = Math.abs(vos.get(minIndex).getPtzT() - Float.parseFloat(titl));
                    float pClose = Math.abs(vos.get(i).getPtzP() - Float.parseFloat(pan));
                    float tClose = Math.abs(vos.get(i).getPtzT() - Float.parseFloat(titl));
                    BigDecimal bmp = new BigDecimal(mpClose).add(new BigDecimal(mtClose));
                    BigDecimal btp = new BigDecimal(pClose).add(new BigDecimal(tClose));
                    if (btp.compareTo(bmp) == -1 || btp.compareTo(bmp) == 0) {
                        minIndex = i;
                    }
                }
                DeviceFeatureVO deviceFeatureVO = vos.get(minIndex);
                log.info("告警编码:{},匹配到的特征点为:{}", alarmCode, deviceFeatureVO);
                translnglatRep.setLongitude(String.valueOf(deviceFeatureVO.getLongitude()));
                translnglatRep.setLatitude(String.valueOf(deviceFeatureVO.getLatitude()));
                translnglatRep.setFlatA(String.valueOf(deviceFeatureVO.getHoriAzimuthAngle()));
            }
        }
        return translnglatRep;
    }


    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public String getDeviceCodeCollectByDeviceCodeAndObjType(String collObjCode, String collObjType, String userId) {
        return IDeviceMapper.getDeviceCodeCollectByDeviceCodeAndObjType(collObjCode, collObjType, userId);
    }

    /**
     * 通过物联设备编码集合批量查询物联设备信息
     *
     * @param deviceCodeList
     * @return
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<DeviceSiteReceiveVO> queryIotDeviceList(List<String> deviceCodeList) {
        return IDeviceMapper.queryIotDeviceList(deviceCodeList);
    }

    /**
     * 根据任务id查询预置位名称
     *
     * @param taskId
     * @return
     */
    @Override
    public List<String> getPresetNameByTaskId(String taskId) {
        return IDeviceMapper.getPresetNameByTaskId(taskId);
    }

    @Override
    public List<AuthRegisterPush> getAuthRegister(List<String> customerCodeList) {
        return IDeviceMapper.getAuthRegister(customerCodeList);
    }

    @Override
    public List<OrderCustomerVO> queryRealNowVersion(List<OrderCustomerVO> customers) {
        return IDeviceMapper.queryRealNowVersion(customers);
    }

    @Override
    public String getPlatformNameByPlatformCode(String platformCode) {
        return IDeviceMapper.getPlatformNameByPlatformCode(platformCode);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<String> getAllDownPlatformCode() {
        return IDeviceMapper.getAllDownPlatformCode();
    }

    @Override
    public int insertLogs(Map map) {
        return IDeviceMapper.insertLogs(map);
    }

    @Override
    public List<String> getValidDev(String customerCode) {
        return IDeviceMapper.getValidDev(customerCode);
    }

    @Override
    public List<String> getValidChn(String customerCode) {
        return IDeviceMapper.getValidChn(customerCode);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<String> getAlarmOperateByRole(String provinceId, String cityId) {
        return IDeviceMapper.getAlarmOperateByRole(provinceId, cityId);
    }

    @Override
    public List<SysUser> getUserAlarmOperateByRole(String provinceId, String cityId) {
        return IDeviceMapper.getUserAlarmOperateByRole(provinceId, cityId);
    }
}
