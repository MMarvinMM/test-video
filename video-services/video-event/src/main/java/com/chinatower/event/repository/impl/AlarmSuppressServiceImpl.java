package com.chinatower.event.repository.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.event.constants.Constants;
import com.chinatower.event.mapper.*;
import com.chinatower.event.repository.AlarmSuppressService;
import com.chinatower.event.repository.StrategyService;
import com.chinatower.event.vo.SysLntlagConfigVO;
import com.chinatower.event.vo.entity.*;
import com.chinatower.platform.annotation.DataScope;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import com.chinatower.platform.constants.SourceEnum;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.model.vo.DeviceModelVO;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.video.api.VideoQueryAPI;
import com.chinatower.video.api.entity.PresetData;
import com.chinatower.video.api.entity.VideoBaseReq;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static com.chinatower.event.constants.Constants.TYPE_T;
import static com.chinatower.event.constants.Constants.TYPE_Z;

/**
 * 功能说明: 告警压制策略 service
 * @author: li.xufu
 * @date: 2022/5/8
 * @requestID 需求单号及需求名称
 */
@Slf4j
@Service
@DS(EbikeDataBaseConstant.BASE)
public class AlarmSuppressServiceImpl implements AlarmSuppressService {

    @Autowired
    private StrategyService strategyService;
    @Autowired
    private VmAlarmAreaSuppressMapper areaSuppressDao;
    @Autowired
    private VmAlarmDeviceSuppressMapper deviceSuppressDao;
    @Autowired
    private VmAlarmTypeSuppressMapper typeSuppressDao;
    @Autowired
    private VmAlarmSuppressStrategyMapper strategyDao;
    @Autowired
    private DeviceMapper deviceMapper;
    @Autowired
    private VideoQueryAPI videoQueryAPI;
    @Autowired
    private SysMapper sysMapper;

    private static final String NO_USER_INFO = "无法获取当前登录用户信息，请重新登陆";
    private static final String NO_DEVICE = "设备编码或者通道不能为空！";
    private static final String MESS = "该摄像机没有配置摄像机型号，请联系铁塔管理员配置摄像机型号";

    /**
     * 功能说明 查询策略列表
     *
     * @param strategyVo
     * @return TableDataInfo
     * @author zhen.yan
     * @date 2022/6/15 11:17
     * @version:
     */
    @Override
    public List<VmAlarmSuppressStrategyVO>  getList(VmAlarmSuppressStrategyVO strategyVo) {
        log.info("AlarmSuppressServiceImpl-getList-req:{}",JSON.toJSONString(strategyVo));
        SysUser user = SecurityUtils.getLoginUser().getUser();
        if (SourceEnum.TT.getSource().equals(user.getUserType())) {
            //铁塔视角下查看客户压制策略
            if(strategyVo.getTenantId() != null && !strategyVo.getTenantId().isEmpty()) {
                strategyVo.setTenantId(strategyVo.getTenantId());
            }
        } else {
            //租户视角查看压制策略关系
            strategyVo.setTenantId(user.getTenantId());
        }

        // 默认按照ID排序
        /*String orderSql = genDateOrderSql(strategyVo.getCreateDateOrder(), strategyVo.getUpdateDateOrder());
        strategyVo.setOrderSql(orderSql);*/
        //PageHelper.startPage(strategyVo.getPageNum(), strategyVo.getPageSize());
        strategyVo.setLocalNode(user.getPlatformCode());
        List<VmAlarmSuppressStrategyVO> result = strategyDao.selectAll(strategyVo);
        if(result==null || result.isEmpty()){
            return result;
        }
        List<Integer> strategyIds = result.stream().map(VmAlarmSuppressStrategyVO::getId).collect(Collectors.toList());
        //添加关联设备、告警类型和区域压制信息
        VmAlarmDeviceSuppressVO deviceVo =  new VmAlarmDeviceSuppressVO();
        deviceVo.setStrategyIdList(strategyIds);
        List<VmAlarmDeviceSuppressVO> deviceList = deviceSuppressDao.selectAll(deviceVo);
        Map<Integer,List<VmAlarmDeviceSuppressVO>> deviceMap = new HashMap<>();
        if(deviceList!=null&&!deviceList.isEmpty()){
            deviceMap = deviceList.stream().collect(Collectors.groupingBy(VmAlarmDeviceSuppressVO::getAlarmSuppressId));
        }
        VmAlarmTypeSuppressVO typeVo =  new VmAlarmTypeSuppressVO();
        typeVo.setStrategyIdList(strategyIds);
        List<VmAlarmTypeSuppressVO> typeList = typeSuppressDao.selectAll(typeVo);
        Map<Integer,List<VmAlarmTypeSuppressVO>> typeMap = new HashMap<>();
        if(typeList!=null&&!typeList.isEmpty()){
            typeMap = typeList.stream().collect(Collectors.groupingBy(VmAlarmTypeSuppressVO::getAlarmSuppressId));
        }
        VmAlarmAreaSuppressVO areaVo =  new VmAlarmAreaSuppressVO();
        areaVo.setStrategyIdList(strategyIds);
        List<VmAlarmAreaSuppressVO> areaList = areaSuppressDao.selectAll(areaVo);
        Map<Integer,List<VmAlarmAreaSuppressVO>> areaMap = new HashMap<>();
        if(areaList!=null&&!areaList.isEmpty()){
            areaMap = areaList.stream().collect(Collectors.groupingBy(VmAlarmAreaSuppressVO::getAlarmSuppressId));
        }

        for(VmAlarmSuppressStrategyVO item:result){
            if(deviceMap!=null && !deviceMap.isEmpty()){
                item.setDeviceSuppressVoList(deviceMap.get(item.getId()));
            }
            if(typeMap!=null && !typeMap.isEmpty()){
                item.setTypeSuppressVoList(typeMap.get(item.getId()));
            }
            if(areaMap!=null && !areaMap.isEmpty()){
                List<VmAlarmAreaSuppressVO> vmAlarmAreaSuppressVos = areaMap.get(item.getId());
                if(vmAlarmAreaSuppressVos!= null){
                    for (VmAlarmAreaSuppressVO suppressVo : vmAlarmAreaSuppressVos) {
                        if(!StringUtils.isEmpty(suppressVo.getPoints())){
                            suppressVo.setSuppressPointList(JSON.parseArray(suppressVo.getPoints(),SuppressPointVO.class));
                        }
                    }
                }
                item.setAreaSuppressVoList(vmAlarmAreaSuppressVos);
            }
        }
        log.info("AlarmSuppressServiceImpl-getList-resp:{}",JSON.toJSONString(result));
        return result;
    }

    /**
     * 功能说明 查询所有策略列表
     *
     * @param strategyVo
     * @return TableDataInfo
     * @author zhen.yan
     * @date 2022/6/15 11:17
     * @version:
     */
    @Override
    @DataScope(dbType = EbikeDataBaseConstant.BASE, tenantColumn="tenant_id", industryCode="industry_code")
    public List<VmAlarmSuppressStrategyVO> getListAll(VmAlarmSuppressStrategyVO strategyVo) {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        //SysUser user = getsysUser();
        if (SourceEnum.TT_CUSTOMERS.getSource().equals(strategyVo.getSource())) {
            //铁塔视角下查看客户压制策略
        } else {
            //租户视角查看压制策略关系
            strategyVo.setTenantId(user.getTenantId());
        }
        return strategyDao.selectAllCount(strategyVo);
    }

    /**
     * 功能说明 查询策略信息
     *
     * @param vmAlarmSuppressStrategyVo
     * @return Result
     * @author zhen.yan
     * @date 2022/6/15 11:17
     * @version:
     */
    @Override
    public Result<VmAlarmSuppressStrategyVO> getStrategyInfo(VmAlarmSuppressStrategyVO vmAlarmSuppressStrategyVo) {
        try{
            Result resultVm;
            VmAlarmSuppressStrategyVO result = strategyDao.selectByPrimaryKey(vmAlarmSuppressStrategyVo.getId());
            if(result == null){
                return Result.fail("没有查询到告警压制策略");
            }
            //添加关联设备、告警类型和区域压制信息
            List<VmAlarmDeviceSuppressVO> deviceList = deviceSuppressDao.selectList(result);
            List<VmAlarmTypeSuppressVO> typeList = typeSuppressDao.selectList(result);
            List<VmAlarmAreaSuppressVO> areaList = areaSuppressDao.selectList(result);
            for(VmAlarmAreaSuppressVO areaTemp:areaList){
                if(areaTemp.getPoints()!=null){
                    areaTemp.setSuppressPointList(JSON.parseArray(areaTemp.getPoints(), SuppressPointVO.class));
                }
            }
            List<VmAlarmUavAreaSuppressVO> uavAreaList = areaSuppressDao.selectUavList(result);
            result.setDeviceSuppressVoList(deviceList);
            result.setTypeSuppressVoList(typeList);
            result.setAreaSuppressVoList(areaList);
            result.setUavAreaSuppressVoList(uavAreaList);
            resultVm = Result.success("查询策略信息成功");
            resultVm.setData(result);
            return resultVm;
        }catch(Exception e){
            log.error("查询策略信息异常，{}",e);
            return Result.fail("查询策略信息失败");
        }

    }

    @Override
    public Result<List<ChannelSuppressVO>> queryChannelByDeviceCode(ChannelSuppressVO channelSuppressVo) {
        log.info("查询通道信息:{}",channelSuppressVo.toString());
        try{
            SysUser user = SecurityUtils.getLoginUser().getUser();
            //SysUser user = getsysUser();
            String tenantId = user.getTenantId();
            List<DeviceModelVO> modelVoList = areaSuppressDao.selectDeviceModel(channelSuppressVo.getDeviceCode());
            if(CollectionUtils.isEmpty(modelVoList)){
                return Result.fail("无法查询到设备关联设备型号信息，请先关联型号信息");
            }
            if(!StringUtils.isEmpty(tenantId)){
                channelSuppressVo.setTenantId(tenantId);
            }
            List<ChannelSuppressVO> list = strategyDao.queryChannelByDeviceCode(channelSuppressVo);
            if(CollectionUtils.isEmpty(list)){
                return Result.fail("设备:"+channelSuppressVo.getDeviceCode()+"没有关联有效的通道信息");
            }
            //选择通道名称时，查询设备PTZ
            if ("1".equals(channelSuppressVo.getNameType())) {
                ChannelSuppressVO suppressVo = list.get(Constants.ZERO_NUM);
                VideoBaseReq videoBaseReq = new VideoBaseReq();
                videoBaseReq.setDeviceCode(suppressVo.getDeviceCode());
                videoBaseReq.setChannelCode(suppressVo.getChannelCode());
                videoBaseReq.setTenantId(tenantId);
                videoBaseReq.setUserId(user.getUserId());
                videoBaseReq.setOrderCode(suppressVo.getOrderCode());
                log.info("=======查询设备PTZ值入参========={}", JSON.toJSONString(videoBaseReq));
                Result<PresetData>  results = videoQueryAPI.queryPtzPosition(videoBaseReq);
                log.info("=======查询设备PTZ值返回结果========={}", JSON.toJSONString(results));
                if (ResultCode.SUCCESS.getCode() != results.getCode()) {
                    return  Result.fail(results.getMsg());
                }
                PresetData resultPtz = results.getData();
                SysLntlagConfigVO config = deviceMapper.getLntlagConfig(channelSuppressVo.getDeviceCode());
                String pan = String.valueOf(resultPtz.getPan());
                String tile = String.valueOf(resultPtz.getTilt());
                String zoom = String.valueOf(resultPtz.getZoom());
                suppressVo.setPointsP(pan);
                suppressVo.setPointsT(tile);
                suppressVo.setPointsZ(zoom);
                return Result.data(list);
            }
            return Result.data(list);
        }catch(Exception e){
            log.error("查询设备通道信息异常，{}",e);
            return Result.fail("查询设备通道信息异常");
        }
    }

    /**
     * 获取默认名称
     * @param suppressStrategyVO
     * @return
     */
    @Override
    public Result<String> getAutoStrategyName(VmAlarmSuppressStrategyVO suppressStrategyVO) {
        return Result.data(strategyDao.getAutoStrategyName(suppressStrategyVO));
    }

    /**
     * 获取默认名称：告警压制策略名称、屏蔽区域名称
     * @param suppressStrategyVO
     * @return
     */
    @Override
    public Result<Map<String, Object>> getAutoStrategyAndRegionName(VmAlarmSuppressStrategyVO suppressStrategyVO) {
        if (StringUtils.isEmpty(suppressStrategyVO.getDeviceCode())) {
            return Result.fail("设备编号不能为空！");
        }
        if (StringUtils.isEmpty(suppressStrategyVO.getTenantId())) {
            SysUser user = SecurityUtils.getLoginUser().getUser();
            if (user == null  || StringUtils.isEmpty(user.getUserId())) {
                return Result.fail(NO_USER_INFO);
            }
            suppressStrategyVO.setTenantId(user.getTenantId());
            suppressStrategyVO.setIndustryCode(user.getIndustryCode());
            suppressStrategyVO.setAppVerCode(user.getAppVerCode());
        }
        Map<String, Object> resData = new HashMap<>();
        resData.put("strategyName", strategyDao.getAutoStrategyName(suppressStrategyVO));
        resData.put("regionName", strategyDao.getAutoRegionName(suppressStrategyVO));
        return Result.data(resData);
    }

    /**
     * 功能说明 区域实时进行坐标转绘
     *
     * @param areaSuppressVo
     * @return Result
     * @author zhen.yan
     * @date 2022/6/15 11:18
     * @version:
     */
    @Override
    public Result tranformPT(VmAlarmAreaSuppressVO areaSuppressVo) {
        try{
            Result processResult = this.queryPTZ(areaSuppressVo);
            if(ResultCode.SUCCESS.getCode() != processResult.getCode()){
                return processResult;
            }
           /* VmAlarmAreaSuppressVO areaVo = new VmAlarmAreaSuppressVO();
            areaVo.setAreaName(areaSuppressVo.getAreaName());
            areaVo.setTenantId(areaSuppressVo.getTenantId());
            areaVo.setDeleteFlag(Constants.ONE);
            List<VmAlarmAreaSuppressVO> areaList = areaSuppressDao.selectAll(areaVo);
            if(areaList != null && !areaList.isEmpty() && !areaList.get(0).getAreaId().equals(areaSuppressVo.getAreaId())){
                return Result.fail("该压制区域名称:'"+areaSuppressVo.getAreaName()+"'已存在");
            }*/
            return strategyService.tranformPT(areaSuppressVo);
        }catch(Exception e){
            log.error("查询设备通道信息异常，{}",e);
            return Result.fail("查询设备通道信息异常");
        }
    }

    /**
     * 功能说明: 查询PTZ坐标
     *
     * @param areaSuppressVo
     * @return Result
     * @author
     * @date
     * @version:
     */
    private Result queryPTZ(VmAlarmAreaSuppressVO areaSuppressVo){
        log.info("查询PTZ坐标：{}",areaSuppressVo.toString());
        SysUser user = SecurityUtils.getLoginUser().getUser();
        String userId = user.getUserId();
        String tenantId = user.getTenantId();
        ChannelSuppressVO channelSuppressVo = new ChannelSuppressVO();
        channelSuppressVo.setTenantId(tenantId);
        channelSuppressVo.setDeviceCode(areaSuppressVo.getDeviceCode());
        List<ChannelSuppressVO> list = strategyDao.queryChannelByDeviceCode(channelSuppressVo);
        if(list==null || list.isEmpty()){
            return Result.fail("设备:"+channelSuppressVo.getDeviceCode()+"没有关联有效的通道信息");
        }
        VideoBaseReq ptzvo = new VideoBaseReq();
        ptzvo.setDeviceCode(areaSuppressVo.getDeviceCode());
        ptzvo.setChannelCode(areaSuppressVo.getChannelCode());
        ptzvo.setTenantId(tenantId);
        ptzvo.setUserId(userId);
        //ptzvo.setOrderCode(areaSuppressVo.getOrderCode());
        try{
            Result<PresetData> eventResult = videoQueryAPI.queryPtzPosition(ptzvo);
            log.info("获取PTZ接口返回信息：{}",JSON.toJSONString(eventResult));
            if(ResultCode.SUCCESS.getCode() != eventResult.getCode()){
                return eventResult;
            }else{
                PresetData resultPtz = eventResult.getData();
                areaSuppressVo.setPointsP(String.valueOf(resultPtz.getPan()));
                areaSuppressVo.setPointsT(String.valueOf(resultPtz.getTilt()));
                areaSuppressVo.setPointsZ(String.valueOf(resultPtz.getZoom()));
            }
        }catch (Exception e){
            log.error("调用获取PTZ信息接口异常：{}", e);
            return Result.fail("获取PTZ信息接口异常");
        }
        return Result.success("");
    }


    /**
     * 功能说明 新增告警压制
     *
     * @param vmAlarmSuppressStrategyVo
     * @return Result
     * @author zhen.yan
     * @date 2022/6/15 11:18
     * @version:
     */
    @Override
    public Result add(VmAlarmSuppressStrategyVO vmAlarmSuppressStrategyVo) {
        try{
            return strategyService.add(vmAlarmSuppressStrategyVo);
        }catch(Exception e){
            log.error("告警策略新增异常：{}",e);
            return Result.fail("告警策略新增失败");
        }
    }

    /**
     * 功能说明 修改告警压制
     *
     * @param vmAlarmSuppressStrategyVo
     * @return Result
     * @author zhen.yan
     * @date 2022/6/15 11:18
     * @version:
     */
    @Override
    public Result mod(VmAlarmSuppressStrategyVO vmAlarmSuppressStrategyVo) {
        try{
            return strategyService.mod(vmAlarmSuppressStrategyVo);
        }catch(Exception e){
            log.error("告警策略修改异常：{}",e);
            return Result.fail("告警策略修改失败");
        }
    }

    /**
     * 功能说明 修改告警压制状态
     *
     * @param strategyVo
     * @return Result
     * @author zhen.yan
     * @date 2022/6/15 11:19
     * @version:
     */
    @Override
    public Result modStatus(VmAlarmSuppressStrategyVO strategyVo) {
        try{
            SysUser currentUser = SecurityUtils.getLoginUser().getUser();
            SysUser user = sysMapper.selectUserOnlyByUserId(currentUser.getUserId());
            strategyVo.setUpdateBy(user.getUserName());
            strategyVo.setUpdateTime(new Date());
            strategyDao.updateStatus(strategyVo);
            return Result.success("告警策略状态修改成功");
        }catch(Exception e){
            log.error("告警策略修改异常：{}",e);
            return Result.fail("告警策略状态修改失败");
        }
    }

    /**
     * 功能说明 PT转换
     *
     * @param areaSuppressVo
     * @return Result
     * @author zhen.yan
     * @date 2022/6/15 11:19
     * @version:
     */
    @Override
    public Result ptTransform(VmAlarmAreaSuppressVO areaSuppressVo) {
        try{
            return strategyService.ptTransform(areaSuppressVo);
        }catch(Exception e){
            log.error("PT转换正常异常：{}",e);
            return Result.fail("PT转换失败");
        }
    }

    /**
     * 功能说明 删除告警配置
     *
     * @param strategyId
     * @return Result
     * @author zhen.yan
     * @date 2022/6/15 11:19
     * @version:
     */
    @Override
    public Result del(Integer strategyId) {
        if (StringUtils.isEmpty(strategyId)) {
            return Result.fail("压制策略ID不能为空，操作失败！");
        }
        try{
            SysUser user = SecurityUtils.getLoginUser().getUser();
            //SysUser user = getsysUser();
            VmAlarmSuppressStrategyVO strategyVo  = new VmAlarmSuppressStrategyVO();
            strategyVo.setId(strategyId);
            strategyVo.setUpdateBy(user.getUserId());
            return strategyService.del(strategyVo);
        }catch(Exception e){
            log.error("告警策略删除异常：{}",e);
            return Result.fail("告警策略删除失败");
        }

    }

    /**
     * 功能说明 租户视角查看压制策略关系
     *
     * @param vmAlarmSuppressStrategyVo
     * @return Result
     * @author zhen.yan
     * @date 2022/6/15 11:19
     * @version:
     */
    @Override
    public Result checkAlarmSuppressName(VmAlarmSuppressStrategyVO vmAlarmSuppressStrategyVo) {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        if (user == null  || StringUtils.isEmpty(user.getUserId())) {
            // 无法获取当前登录人，返回错误信息
            return Result.fail("无法获取当前登录人");
        }
        //租户视角查看压制策略关系
        vmAlarmSuppressStrategyVo.setTenantId(user.getTenantId());
        // sql防注入，已确认前端不会传递该参数
        vmAlarmSuppressStrategyVo.setOrderSql(null);
        vmAlarmSuppressStrategyVo.setLocalNode(user.getPlatformCode());
        return Result.data(strategyDao.selectAllByName(vmAlarmSuppressStrategyVo));
    }

    /**
     * 功能说明 查询设备参数信息
     *
     * @param vo
     * @return Result
     * @author zhen.yan
     * @date 2022/6/15 11:20
     * @version:
     */
    @Override
    public Result getLntlagConfig(VmAlarmAreaSuppressVO vo) {
        SysLntlagConfigVO config = deviceSuppressDao.getLntlagConfig(vo.getDeviceCode());
        if(Objects.isNull(config)){
            return Result.fail(MESS);
        }
        // 获取当前租户下所有告警压制配置策略的数量
        int num = strategyDao.getTenantMaxAreaNum(vo.getTenantId());
        return Result.data(num);
    }

    /**
     * 功能说明 查询设备参数信息
     *
     * @param refPoint
     * @return Result
     * @author
     * @date
     * @version:
     */
    @Override
    public Result queryPtzPositionPtz(VmAlarmAreaSuppressVO refPoint) {
        if (StringUtils.isEmpty(refPoint.getDeviceCode()) || StringUtils.isEmpty(refPoint.getChannelCode())) {
            return Result.fail(NO_DEVICE);
        }
        // 获取当前登录人
        SysUser user = SecurityUtils.getLoginUser().getUser();
        if (user == null  || StringUtils.isEmpty(user.getUserId())) {
            return Result.fail(NO_USER_INFO);
        }
        //调用6.15查询PTZ
        VideoBaseReq videoBaseReq = new VideoBaseReq();
        videoBaseReq.setDeviceCode(refPoint.getDeviceCode());
        videoBaseReq.setChannelCode(refPoint.getChannelCode());
        videoBaseReq.setTenantId(user.getTenantId());
        Result<PresetData> ptzResult = videoQueryAPI.queryPtzPosition(videoBaseReq);
        if (ResultCode.SUCCESS.getCode() != ptzResult.getCode()) {
            return  Result.fail(ptzResult.getMsg());
        }
        PresetData resultPtz = ptzResult.getData();
        //queryPtzPosition接口内已转义 注释此内容
/*        SysLntlagConfigVO config = deviceMapper.getLntlagConfig(refPoint.getDeviceCode());
        String pan = this.paramTrans(String.valueOf(resultPtz.getPan()), config, null);
        String tile = this.paramTrans(String.valueOf(resultPtz.getTilt()), config, TYPE_T);
        String zoom = this.paramTrans(String.valueOf(resultPtz.getZoom()), config, TYPE_Z);
        resultPtz.setPan(Float.valueOf(pan));
        resultPtz.setTilt(Float.valueOf(tile));
        resultPtz.setZoom(Float.valueOf(zoom));*/
        ptzResult.setData(resultPtz);
        return ptzResult;
    }

    /**
     * 功能说明 查询区域屏蔽压制
     *
     * @param vmAlarmSuppressVo
     * @return Result
     * @author cuisj
     * @date 2022/10/17 11:17
     * @version:
     */
    @Override
    public Result<VmAlarmAreaSuppressVO> getAreaSuppress(VmAlarmAreaSuppressVO vmAlarmSuppressVo) {
        try {
            Result resultVm;
            List<VmAlarmAreaSuppressVO> areaList = areaSuppressDao.selectAreaSuppress(vmAlarmSuppressVo);
            resultVm = Result.success("查询区域屏蔽压制成功");
            resultVm.setData(areaList);
            return resultVm;
        } catch (Exception e) {
            log.error("查询区域屏蔽压制信息异常，{}", e);
            return Result.fail("查询区域屏蔽压制失败");
        }
    }

    /**
     * 功能说明 查询策略信息下的区域屏蔽压制
     *
     * @param vmAlarmSuppressVo
     * @return Result
     * @author cuisj
     * @date 2022/10/19
     * @version:
     */
    @Override
    public Result<VmAlarmAreaSuppressVO> getAreaSuppressList(VmAlarmAreaSuppressVO vmAlarmSuppressVo) {
        try {
            if ( vmAlarmSuppressVo.getChannelCode() == null || vmAlarmSuppressVo.getChannelCode().isEmpty()){
                return Result.fail("通道编码必传");
            }
            Result resultVm;
            SysUser user = SecurityUtils.getLoginUser().getUser();
            vmAlarmSuppressVo.setTenantId(user.getTenantId());
            vmAlarmSuppressVo.setIndustryCode(user.getIndustryCode());
            List<VmAlarmAreaSuppressVO> areaList = areaSuppressDao.selectAreaSuppressList(vmAlarmSuppressVo);
            resultVm = Result.success("查询策略信息下的区域屏蔽压制成功");
            resultVm.setData(areaList);
            return resultVm;
        } catch (Exception e) {
            log.error("查询策略信息下的区域屏蔽压制信息异常，{}", e);
            return Result.fail("查询策略信息下的区域屏蔽压制失败");
        }
    }


    /**
     * 功能说明 区域屏蔽压制名称重复校验
     *
     * @param vmAlarmSuppressVo
     * @return Result
     * @author cuisj
     * @date 2022/10/19
     * @version:
     */
    @Override
    public Result<VmAlarmAreaSuppressVO> areaSuppressCheck(VmAlarmAreaSuppressVO vmAlarmSuppressVo) {

        Result resultVm = null;
        List<VmAlarmAreaSuppressVO> areaList = areaSuppressDao.areaSuppressCheck(vmAlarmSuppressVo);
        if (!areaList.isEmpty() || areaList.size()>0){
            resultVm = Result.fail("区域名称重复,请重新设置");
        }else {
            resultVm = Result.success("添加区域屏蔽压制名称不重复");
        }
        return resultVm;
    }

    /**
     * 参数进制转换
     * @param value
     * @param config
     * @param type
     * @return
     */
    public String paramTrans(String value, SysLntlagConfigVO config, String type) {
        String result = value;
        // dahua 大华的设备，上报的是十进制，在十进制下，
        //P=360-P/10
        //T’=T/10
        // Z=摄像机的最大放大倍数 *（Z原始值/128）=（最大焦距/最小焦距）*（Z原始值/128）
        if (Constants.MODELID_DAHUA.equals(config.getModelId()) || Constants.MODELID_DHUA.equals(config.getModelId())) {
            BigDecimal num = new BigDecimal(value);
            if (TYPE_Z.equals(type) && org.apache.commons.lang3.StringUtils.isNotBlank(config.getFocusRange())) {
                String[] focusRange = config.getFocusRange().split("~");
                if (focusRange.length >= 2) {
                    BigDecimal minFocus = new BigDecimal(focusRange[0]);
                    BigDecimal maxFocus = new BigDecimal(focusRange[1]);
                    BigDecimal numBigZ = maxFocus.divide(minFocus, 2, BigDecimal.ROUND_HALF_UP).multiply(num.divide(new BigDecimal(128), 2, BigDecimal.ROUND_HALF_UP));
                    result = String.valueOf(numBigZ.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
                }
            }else if(TYPE_T.equals(type)){
                result = String.valueOf(num.divide(new BigDecimal(10),2,BigDecimal.ROUND_HALF_UP));
            }
            else {
                BigDecimal numBig = new BigDecimal(360).subtract(num.divide(new BigDecimal(10),2,BigDecimal.ROUND_HALF_UP));
                result = String.valueOf(numBig.setScale(2, BigDecimal.ROUND_HALF_UP));
            }
        }
        // 海康威视设备 10进制转16进制 p、t值接口传入为10进制转为16进制再除以10（16进制的10），t再减去360（16进制的360）
        if (Constants.MODELID_HIKVISION.equals(config.getModelId())) {
            // 转为正数
            value = String.valueOf(Math.abs(new Double(value)));
            // 如果包含小数，去除小数点后数据，转16进制
            if (value.contains(".")) {
                value = Integer.toHexString(Integer.valueOf(value.substring(0, value.indexOf("."))));
            }
            else {
                value = Integer.toHexString(Integer.valueOf(value));
            }
            // 除以10，小数点左移一位
            if ("0".equals(value)) {
                result = "0";
            }
            else if (value.endsWith("0")) {
                result = value.substring(0, value.length() - 1);
            }
            else {
                result = value.substring(0, value.length() - 1) + "." + value.substring(value.length() - 1);
            }
            // 如果是T：0~90,T=T  T值不进行操作；  270~360，T=(T-360)  T值减360（按照10进制处理）
            if (TYPE_T.equals(type)) {
                BigDecimal bigDecimal = new BigDecimal(result);
                //0~90,T=T  T值不进行操作，  270~360，T=(T-360)  T值减360
                if ((bigDecimal.compareTo(new BigDecimal("270")) > -1) && (bigDecimal.compareTo(new BigDecimal("360")) < 1)) {
                    result = String.valueOf(bigDecimal.subtract(new BigDecimal("360")));
                }
            }
        }
        if (TYPE_T.equals(type)) {
            // 根据配置对t值进行翻转转换
            if (!Constants.YES_FLAG.equals(config.getVertiDownNeg())) {
                result = new BigDecimal(result).multiply(new BigDecimal("-1")).toPlainString();
            }
        }
        log.info("paramTrans 参数进制转换：value：{},modelId：{},type：{},result：{}", value, JSON.toJSONString(config), type, result);
        return result;
    }

    private String genDateOrderSql(String createDateOrder, String updateDateOrder) {
        StringBuffer orderSql = new StringBuffer();
        if (createDateOrder != null) {
            orderSql.append("create_time ").append(createDateOrder);
        } else if (updateDateOrder != null) {
            orderSql.append("update_time ").append(updateDateOrder);
        }
        return orderSql.toString();
    }

}
