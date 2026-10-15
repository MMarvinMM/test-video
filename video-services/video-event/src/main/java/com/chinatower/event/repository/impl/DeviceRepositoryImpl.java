package com.chinatower.event.repository.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.event.dto.AlarmOrderDTO;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.event.constants.Constants;
import com.chinatower.event.dto.*;
import com.chinatower.event.dto.leadertree.LeaderByTenantIdTreeReq;
import com.chinatower.event.entity.dto.*;
import com.chinatower.event.entity.vo.OrderInfoVO;
import com.chinatower.event.entity.vo.ReceiveAlarmVO;
import com.chinatower.event.entity.vo.SysUserVO;
import com.chinatower.event.entity.vo.TenantDeviceEventRelVO;
import com.chinatower.event.mapper.DeviceMapper;
import com.chinatower.event.mapper.FlowLinkAttrMapper;
import com.chinatower.event.mapper.SysMapper;
import com.chinatower.event.repository.ICommonRepository;
import com.chinatower.event.repository.IDeviceRepository;
import com.chinatower.event.repository.IStatisticsRepository;
import com.chinatower.event.vo.*;
import com.chinatower.event.vo.entity.DeviceFeatureVO;
import com.chinatower.platform.annotation.DataScope;
import com.chinatower.platform.common.utils.TreeUtils;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import com.chinatower.platform.entity.admin.SysDept;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.entity.devicemgmt.vo.DeviceInfoVO;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.model.vo.DeviceRefPointVO;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import com.chinatower.system.api.resp.SysDictDataResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
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
public class DeviceRepositoryImpl implements IDeviceRepository {

    @Autowired
    private DeviceMapper deviceMapper;

    @Autowired
    private IStatisticsRepository statisticsRepository;

    @Autowired
    private SysMapper sysMapper;
    @Autowired
    private FlowLinkAttrMapper flowLinkAttrMapper;

    @Autowired
    private SystemFeignClient systemFeignClient;
    @Autowired
    private ICommonRepository iCommonRepository;

    /**
     * 通过设备编码获取租户
     *
     * @param deviceCode
     * @return
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<OrderCustomerVO> getCustomersByDevice(String deviceCode, String applicationCode, ReceiveAlarmVO receiveAlarmVO) {
        return deviceMapper.getCustomersByDevice(deviceCode, applicationCode,receiveAlarmVO);
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
    public List<OrderCustomerVO> getCustomersList(String deviceCode, String algorithmCode, String taskId,String alarmSource,String localNode) {
        return deviceMapper.getCustomersList(deviceCode, algorithmCode, taskId,alarmSource,localNode);
    }

    /**
     * 获取符合条件的铁塔运营研判人员
     *
     * @param list
     * @return
     */
    @Override
    public List<String> getUserForOperation(List<String> list) {
        return deviceMapper.getUserForOperation(list);
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

        return deviceMapper.getUserInfoByRoleIds(roleIdList, tenantId);
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

        return deviceMapper.getUserInfoByLongRoleIds(roleIdList, tenantId);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<SysUser> getUserInfoByLongRoleIdsAndIndustryCode(List<Long> roleIdList, String tenantId, String industryCode) {
        return deviceMapper.getUserInfoByLongRoleIdsAndIndustryCode(roleIdList,tenantId,industryCode);
    }

    /**
     * 查询客户管理员信息
     * @param tenantId
     * @return
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<SysUserVO> getTenantAdminUser(String tenantId) {
        return deviceMapper.getTenantAdminUser(tenantId);
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
            return deviceMapper.getUserInfoByUserIds(userIdList);
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
    public List<SysUserVO> getUserInfoByUserIdsAndIndustryCode(List<String> userIdList , String industryCode) {
        if (!CollectionUtils.isEmpty(userIdList)) {
            return deviceMapper.getUserInfoByUserIdsAndIndustryCode(userIdList,industryCode);
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
        if (!StringUtils.isEmpty(tenantDeviceRelDTOList) && tenantDeviceRelDTOList.size()>0){
            dto.setTenantId(tenantDeviceRelDTOList.get(0).getTenantId());
        }
        List<TenantDeviceEventRelVO> list = deviceMapper.getDeviceRel(dto);

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
        return deviceMapper.getGridKeeperByGridId(gridId);
    }

    /**
     * 通过网格id获取网格员
     *
     * @param gridId
     * @return
     */
    @Override
    public List<Map> getGridKeeperByGridIds(String gridId) {
        return deviceMapper.getGridKeeperByGridIds(gridId);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public String getGridUserByGridId(String gridId) {
        return deviceMapper.getGridUserByGridId(gridId);
    }

    /**
     * 查询摄像机信息
     *
     * @param deviceCode
     * @return
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public DeviceSiteVO queryDevice(String deviceCode, String channelCode) {
        return deviceMapper.queryDevice(deviceCode,channelCode);
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
    public DeviceSiteVO queryIotDevice(String deviceCode) {
        return deviceMapper.queryIotDevice(deviceCode);
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
    public DeviceSiteVO queryRadarDevice(String tenantId, String industryCode, String deviceCode) {
        return deviceMapper.queryRadarDevice(tenantId,industryCode,deviceCode);
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
    public DeviceSiteVO querySensorDevice(String deviceCode, int categoryCode) {
        return deviceMapper.querySensorDevice(deviceCode, categoryCode);
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
        return deviceMapper.getAlarmTypeInfoById(warningTypeId);
    }

    /**
     * 根据部门ID查询部门信息
     *
     * @param deptIdList
     * @return
     */
    @Override
    public List<SysDept> getSysDeptByDeptIds(List<String> deptIdList) {
        return deviceMapper.getSysDeptByDeptIds(deptIdList);
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
            areaInfoList = deviceMapper.getAreaInfo();
            RedisUtil.set("areaInfoList", JSON.toJSONString(areaInfoList), RedisDBType.PRIMARY);
        }
        return areaInfoList;
    }

    @Override
    public Map<String, String> getAreaNameCaffeineMap(List<String> areaCodes) {
        if(areaCodes == null){
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
        return deviceMapper.getAlarmType();
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
        return deviceMapper.getUserInfoById(userId);
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
        return deviceMapper.getDeviceRelList(list, tenantId);
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
        return deviceMapper.getChannelRel(list, tenantId);
    }

    /**
     * 查询设备相关信息
     *
     * @return
     * @author chenmf
     * @date 2022/3/10
     * @version
     */
    @Override
    public List<AlarmOrderByDeviceDTO> queryAlarmOrderByDevice(AlarmOrderDTO dto) {

        if (!StringUtils.isEmpty(dto.getDeviceLabelId()) || !CollectionUtils.isEmpty(dto.getDeviceLabelIdList())
//                || !StringUtils.isEmpty(dto.getOrgId()) || !CollectionUtils.isEmpty(dto.getOrgIdList())
                || !StringUtils.isEmpty(dto.getModelId()) || !StringUtils.isEmpty(dto.getDeviceName()) ||
                !CollectionUtils.isEmpty(dto.getModelIds())) {
            return deviceMapper.queryAlarmOrderByDevice(dto);
        } else {
            return null;
        }
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
            return deviceMapper.getAlarmOrderByIotDevice(dto);
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
        return deviceMapper.getLeaderByTenantId(dto);
    }

    /**
     * 查询领导批示数据（根据组织节点展示树状数据）
     *
     * @param dto
     * @return
     */
    @Override
    @DataScope(dbType = EbikeDataBaseConstant.BASE,tenantColumn="tenant_id",industryCode="industry_code")
    public List<SysInfo> getLeaderByTenantIdShowTree(LeaderByTenantIdTreeReq dto) {
        SysUserDTO userDto = new SysUserDTO();
        BeanUtils.copyProperties(dto,userDto);
        if (StringUtils.isEmpty(dto.getTenantId())) {
            return null;
        }
        List<String> parentList = sysMapper.queryDeptId(dto.getTenantId());
        if(parentList != null && parentList.size()>0){
            FlowLinkAttrModel flowLinkAttrModel = new FlowLinkAttrModel();
            flowLinkAttrModel.setDataScope(dto.getDataScope());
            LoginUser loginUser = SecurityUtils.getLoginUser();
            SysUser currentUser = loginUser.getUser();
            flowLinkAttrModel.setAppVerCode(currentUser.getAppVerCode());
            flowLinkAttrModel.setIndustryCode(currentUser.getIndustryCode());
            List<SysInfo> deptList = flowLinkAttrMapper.queryDeptsNew(parentList,flowLinkAttrModel);
            for (SysInfo dept : deptList) {
                dept.setChildren(new ArrayList<>());
            }
            List<SysInfo> userList = deviceMapper.getLeaderDeptByTenantId(userDto);

            Result<List<SysUser>> reponse =null;
            //最终封装用户集
            List<SysInfo> newUserList = new ArrayList<>();
            //封装符合要求的调用用户是否有摄像机权限的用户list
            List<SysUser> sysUserList =new ArrayList<>();
            //查询数据字典是否调用华苏接口获取用户权限，默认1调用
            String flag =Constants.ONE;
            try{
                Result<List<SysDictDataResponse>> suppressDictData = systemFeignClient.dictDataType(Constants.SELECT_USER_POWER_FLAG);
                if (suppressDictData != null && suppressDictData.getData() != null && !suppressDictData.getData().isEmpty()) {
                    flag= suppressDictData.getData().get(0).getDictValue();
                }
                //如果是卫片告警，不过滤数据权限
                if(org.apache.commons.lang3.StringUtils.isNotEmpty(dto.getWarningSource()) && Constants.NINE.equals(dto.getWarningSource())
                        && Arrays.asList(Constants.REGION_ALARM_LIST).contains(dto.getWarningTypeId())){
                    flag = Constants.ZERO;
                }
                if(Constants.ONE.equals(flag) && org.apache.commons.lang3.StringUtils.isNotEmpty(dto.getWarningSource())){
                    //调用接口获取有摄像机权限的客户
                    List<String> list = userList.stream().map(user -> user.getUserId()).collect(Collectors.toList());
                    reponse =iCommonRepository.getUserByDeptAndDeviceScope(list,dto.getTenantId(),dto.getDeviceCode(),
                            flowLinkAttrModel.getIndustryCode(),flowLinkAttrModel.getAppVerCode(),dto.getWarningSource(),
                            dto.getCityId(),dto.getCountyId(),dto.getProvinceId(), dto.getWarningTypeId());
                    if(reponse !=null && ResultCode.SUCCESS.getCode() == reponse.getCode()&&reponse.getData()!=null){
                        JSONArray arrayList = JSONArray.parseArray(JSON.toJSONString(reponse.getData()));
                        for (Object object:arrayList) {
                            sysUserList.add(JSONObject.parseObject(JSON.toJSONString(object),SysUser.class));
                        }
                    }
                }else{
                    newUserList = userList;
                }
            }catch (Exception e){
                log.error("FlowLinkAttrServiceImpl-getFlowByIdUserTreeNew-根据设备编号获取有权限的用户列表error,返回原有用户,错误信息:{}",e);
                newUserList = userList;
            }
            //判断是否有摄像机code,有的话取摄像机有权限的用户返回，没有按照所查用户返回
            if (org.apache.commons.lang3.StringUtils.isNotEmpty(dto.getWarningSource()) && Constants.FILTER_WARNING_SOURCE.contains(dto.getWarningSource())) {
                for (SysInfo sysInfo : userList) {
                    for (SysUser sysUser : sysUserList) {
                        if (sysInfo.getUserId() != null && sysInfo.getUserId().equals(sysUser.getUserId())
                                && sysInfo.getUserId() != null && sysInfo.getDeptId().equals(sysUser.getDeptId())) {
                            newUserList.add(sysInfo);
                        }
                    }
                }
            }else{
                newUserList = userList;
            }

            List<SysInfo> finalNewUserList = newUserList;
            deptList.forEach(dept -> {
                finalNewUserList.forEach(user -> {
                    String userDeptId = user.getDeptId();
                    if(userDeptId!=null && !(userDeptId.indexOf(",")>0) ){
                        if (userDeptId.equals(dept.getDeptId())) {
                            dept.getChildren().add(user);
                        }
                    }else{
                        List<String> deptIdTemps = Arrays.asList(userDeptId.split(Constants.COMMA));
                        for(String deptIdTemp:deptIdTemps){
                            if (deptIdTemp.equals(dept.getDeptId())) {
                                dept.getChildren().add(user);
                                continue;
                            }
                        }
                    }
                });
            });
           List<SysInfo> deptListNew = new ArrayList<>();
            for(int i=0; i<deptList.size();i++){
                if(deptList.get(i).getChildren() != null && deptList.get(i).getChildren().size() >0){
                    deptListNew.add(deptList.get(i));
                }
            }
            deptListNew = deptListNew.stream().distinct().collect(Collectors.toList());
            JSONArray treeAry = TreeUtils.listToTree(deptListNew, "deptId", "parentId");
            List<SysInfo> list = JSON.parseArray(JSON.toJSONString(treeAry), SysInfo.class);
            SysInfo root = new SysInfo(Constants.TOWER_ROOT_AREA, Constants.TOWER_ROOT_AREA_NAME);
            List<SysInfo> rootChild = new LinkedList<>();
            //去重
            HashSet h = new HashSet(list);
            rootChild.clear();
            rootChild.addAll(h);
            root.setChildren(list);
            return list;
        }else{
            return null;
        }
    }

    public void delChildren(List<SysInfo> list){
        Iterator<SysInfo> it = list.iterator();
        while (it.hasNext()){
            SysInfo info =it.next();
            if((null == info.getChildren() || info.getChildren().size()<1) && org.apache.commons.lang3.StringUtils.isEmpty(info.getUserId())){
                it.remove();
            }else{
                if(null != info.getChildren() && info.getChildren().size() > 0){
                    delChildren(info.getChildren());
                }
            }
        }
    }

    @Override
    public List<SiteRequestVO> getDeviceList(SiteRequestVO siteRequestVO) {
        return deviceMapper.getDeviceList(siteRequestVO);
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
        return deviceMapper.getLntlagConfig(deviceCode);
    }

    /**
     * 功能描述 通过 厂商 查询摄像机
     *
     * @param deviceInfoReq
     * @return List<DeviceInfoVO>
     * @author Chensw
     * @date 2022/4/26 15:58
     */
    @Override
    public List<DeviceInfoVO> getDeviceInfoList(DeviceInfoReq deviceInfoReq) {
        return deviceMapper.getDeviceInfoList(deviceInfoReq);
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
    public List<OrderCustomerVO> getCustomersBySensorDevice(String[] deviceCode, String applicationCode, int categoryCode,String taskId ,String alarmSource,String localNode) {
        return deviceMapper.getCustomersBySensorDevice(deviceCode, applicationCode, categoryCode,taskId,alarmSource,localNode);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public int getRadarTemplateCount(String taskId) {
        return deviceMapper.getRadarTemplateCount(taskId);
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
        return deviceMapper.getCustomersBySensorDeviceList(deviceCodeList);
    }

    /**
     *  通过设备编码获取有效参考点
     *
     * @return
     * @author chenmf
     * @date 2022/5/23
     * @version
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<DeviceRefPointVO> getDevicePointsValid(String deviceCode) {
        return deviceMapper.getDevicePointsValid(deviceCode);
    }

    /**
     *  通过设备编码获取有效参考点
     *
     * @return
     * @author chenmf
     * @date 2022/5/23
     * @version
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<DeviceRefPointVO> getDevicePointsValidBatch(List<String> deviceCodeList) {
        return deviceMapper.getDevicePointsValidBatch(deviceCodeList);
    }


    /**
     * 查询摄像机信息
     *
     * @param deviceCodeList
     * @return
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<DeviceSiteVO> queryDeviceList(List<String> deviceCodeList) {
        return deviceMapper.queryDeviceList(deviceCodeList);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public TranslnglatVO handlerFeaturePT(TranslnglatVO translnglatRep,String deviceCode ,String alarmCode ) {
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
            List<DeviceFeatureVO> vos = deviceMapper.getFeatureListByDevice(featureVO);
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
                    if (btp.compareTo(bmp) == -1 || btp.compareTo(bmp) == 0 ) {
                        minIndex = i;
                    }
                }
                DeviceFeatureVO deviceFeatureVO = vos.get(minIndex);
                log.info("告警编码:{},匹配到的特征点为:{}",alarmCode,deviceFeatureVO);
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
        return deviceMapper.getDeviceCodeCollectByDeviceCodeAndObjType(collObjCode, collObjType, userId);
    }

    /**
     * 通过物联设备编码集合批量查询物联设备信息
     * @param deviceCodeList
     * @return
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<DeviceSiteVO> queryIotDeviceList(List<String> deviceCodeList) {
        return deviceMapper.queryIotDeviceList(deviceCodeList);
    }

    /**
     * 根据任务id查询预置位名称
     * @param taskId
     * @return
     */
    @Override
    public List<String> getPresetNameByTaskId(String taskId) {
        return deviceMapper.getPresetNameByTaskId(taskId);
    }

    @Override
    public List<AuthRegisterPush> getAuthRegister(List<String> customerCodeList) {
        return deviceMapper.getAuthRegister(customerCodeList);
    }

    @Override
    public List<OrderCustomerVO> queryRealNowVersion(List<OrderCustomerVO> customers) {
        return deviceMapper.queryRealNowVersion(customers);
    }

    @Override
    public String getPlatformNameByPlatformCode(String platformCode) {
        return deviceMapper.getPlatformNameByPlatformCode(platformCode);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<String> getAllDownPlatformCode() {
        return deviceMapper.getAllDownPlatformCode();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public SysInfo queryAdminUser(String tenantId) {
        return flowLinkAttrMapper.queryAdminUser(tenantId);
    }

    @Override
    public int insertLogs(Map map) {
        return deviceMapper.insertLogs(map);
    }

    @Override
    public List<DeviceInfoByPush> getDevInfo(List<String> deviceList) {
        return deviceMapper.getDevInfo(deviceList);
    }

    @Override
    public List<String> getValidDev(String customerCode) {
        return deviceMapper.getValidDev(customerCode);
    }

    @Override
    public List<String> getValidChn(String customerCode) {
        return deviceMapper.getValidChn(customerCode);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<String> getAlarmOperateByRole(String provinceId, String cityId) {
        return deviceMapper.getAlarmOperateByRole(provinceId,cityId);
    }
}
