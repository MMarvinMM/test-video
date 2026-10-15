package com.chinatower.device.controller;


import cn.hutool.core.collection.CollectionUtil;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.entity.Result;
import com.chinatower.device.constants.IndustryConstants;
import com.chinatower.device.dto.TalkV2DTO;
import com.chinatower.device.entity.dto.HornDeviceDTO;
import com.chinatower.device.entity.dto.TalkReq;
import com.chinatower.device.entity.dto.TalkV2Req;
import com.chinatower.device.entity.vo.HornDeviceVo;
import com.chinatower.device.entity.vo.LockResultVo;
import com.chinatower.device.entity.vo.TalkResultVO;
import com.chinatower.device.entity.vo.TalkV2VO;
import com.chinatower.device.entity.vo.TalkV3VO;
import com.chinatower.device.repository.IHornDeviceRepository;
import com.chinatower.platform.constants.Constants;
import com.chinatower.platform.constants.DeviceConstants;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.entity.device.DeviceDistanceDTO;
import com.chinatower.platform.entity.device.DeviceDistanceVO;
import com.chinatower.platform.entity.device.DeviceTreeVO;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.service.SysAreaRootService;
import com.chinatower.platform.utils.SecurityUtils;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * video-device
 * 喇叭设备 前端控制器
 * @author Lishihao
 * @since 2023-06-08
 */
@RestController
@Slf4j
@RequestMapping("/hornDevice")
public class HornDeviceController {


    @Autowired
    IHornDeviceRepository iHornDeviceRepository;
    @Autowired
    private SysAreaRootService sysAreaRootService;

    /**
     * 根据条件分页查询喇叭设备信息
     *
     * @param hornDeviceDTO 查询条件实体类
     * @return TableDataInfo
     */
    @PostMapping("/queryHornDevicePage")
    public Result<PageInfo<HornDeviceVo>> queryHornDevicePage(@RequestBody HornDeviceDTO hornDeviceDTO){
        return iHornDeviceRepository.queryHornDevicePage(hornDeviceDTO);
    }

    /**
     * 查询喇叭设备详情
     *
     * @param hornDeviceDTO 查询条件实体类
     * @return TableDataInfo
     */
    @Deprecated
    @PostMapping("/queryHornDeviceDetail")
    public Result<HornDeviceVo> queryHornDeviceDetail(@RequestBody HornDeviceDTO hornDeviceDTO){
        return iHornDeviceRepository.queryHornDeviceDetail(hornDeviceDTO);
    }

    /**
     * 根据设备编号获取上传的附件信息
     * @param hornDeviceDTO
     * @return
     */
    @PostMapping("/getFileUrlByDeviceCode")
    public Result<List<String>> getFileUrlByDeviceCode(@RequestBody HornDeviceDTO hornDeviceDTO){
        return iHornDeviceRepository.getFileUrlByDeviceCode(hornDeviceDTO);
    }

    /**
     * 查询设备所属的行政区域树形
     *
     * @param hornDeviceDTO 查询条件实体类
     * @return Result<List<DeviceTreeVO>>
     */
    @Deprecated
    @PostMapping("/getHornMonitorTree")
    public Result<List<DeviceTreeVO>> getHornMonitorTree(@RequestBody HornDeviceDTO hornDeviceDTO) {

        try {
            LoginUser currentUser = SecurityUtils.getLoginUser();
            if (null != currentUser) {
                SysUser tokenUser = currentUser.getUser();
                //铁塔视角切换客户视角后，目前从token里取不到客户的userId，所以需要根据userName查一下
                SysUser user = iHornDeviceRepository.selectUserOnlyByUserName(tokenUser.getUserName());
                hornDeviceDTO.setUserId(user.getUserId());
                hornDeviceDTO.setTenantId(tokenUser.getTenantId());
            }
            // 默认查询已订购的
            if (StringUtils.isEmpty(hornDeviceDTO.getOrderStatus())) {
                hornDeviceDTO.setOrderStatus("1");
            }
            if (CollectionUtil.isNotEmpty(hornDeviceDTO.getDeviceStatusList())) {
                // 1 离线，0在线 切换为 0 离线，1在线
                if (hornDeviceDTO.getDeviceStatusList().size() == 1) {
                    if ("1".equals(hornDeviceDTO.getDeviceStatusList().get(0))) {
                        hornDeviceDTO.setDeviceStatusList(Arrays.asList("0"));
                    } else {
                        hornDeviceDTO.setDeviceStatusList(new ArrayList<>(Arrays.asList("1")));
                    }
                }
                if (hornDeviceDTO.getDeviceStatusList().contains("1")) {
                    hornDeviceDTO.getDeviceStatusList().addAll(Arrays.asList("2","3"));
                }
            }
            List<DeviceTreeVO> treeList = iHornDeviceRepository.getHornMonitorTree(hornDeviceDTO);
            List<DeviceTreeVO> list = buildIotMonitorTree(treeList, hornDeviceDTO.isNeedDevice());
            List<DeviceTreeVO> rootTree = new ArrayList<>(1);
            if (!treeList.isEmpty()) {
                // 补充中国根节点
                DeviceTreeVO root = new DeviceTreeVO();
                sysAreaRootService.createRootTreeVO(root);
                root.setOnlineNum(list.stream().mapToInt(DeviceTreeVO::getOnlineNum).sum());
                root.setNum(list.stream().mapToInt(DeviceTreeVO::getNum).sum());
                //去掉异常省份
                list = list.stream().filter(dto ->
                        StringUtils.isNotEmpty(dto.getCode())).collect(Collectors.toList());
                root.setList(list);
                rootTree.add(root);
            }
            return Result.data(rootTree);
        }catch (Exception e){
            log.error("查询设备所属的行政区域树形失败, e:", e);
            return Result.fail("查询设备所属的行政区域树形失败");
        }

    }

    /**
     * 查询行政组织喇叭设备树
     *
     * @param hornDeviceDTO 查询条件实体类
     * @return Result<List<DeviceTreeVO>>
     */
    @Deprecated
    @PostMapping("/getHornMonitorOrgTree")
    public Result<List<DeviceTreeVO>> getHornMonitorOrgTree(@RequestBody HornDeviceDTO hornDeviceDTO) {

        try {
            LoginUser currentUser = SecurityUtils.getLoginUser();
            if (null != currentUser) {
                SysUser tokenUser = currentUser.getUser();
                //铁塔视角切换客户视角后，目前从token里取不到客户的userId，所以需要根据userName查一下
                SysUser user = iHornDeviceRepository.selectUserOnlyByUserName(tokenUser.getUserName());
                hornDeviceDTO.setUserId(user.getUserId());
                hornDeviceDTO.setTenantId(tokenUser.getTenantId());
            }
            if (StringUtils.isEmpty(hornDeviceDTO.getOrderStatus())) {
                hornDeviceDTO.setOrderStatus("1");
            }
            if (CollectionUtil.isNotEmpty(hornDeviceDTO.getDeviceStatusList())) {
                // 1 离线，0在线 切换为 0 离线，1在线
                if (hornDeviceDTO.getDeviceStatusList().size() == 1) {
                    if ("1".equals(hornDeviceDTO.getDeviceStatusList().get(0))) {
                        hornDeviceDTO.setDeviceStatusList(Arrays.asList("0"));
                    } else {
                        hornDeviceDTO.setDeviceStatusList(new ArrayList<>(Arrays.asList("1")));
                    }
                }
                if (hornDeviceDTO.getDeviceStatusList().contains("1")) {
                    hornDeviceDTO.getDeviceStatusList().addAll(Arrays.asList("2","3"));
                }
            }
            List<DeviceTreeVO> treeList = iHornDeviceRepository.getHornMonitorOrgTree(hornDeviceDTO);
            if (!treeList.isEmpty()) {
                treeList.get(0).setFirst(1);
            }
            return Result.data(treeList);
        }catch (Exception e){
            log.error("查询行政组织喇叭设备树形失败, e:", e);
            return Result.fail("查询行政组织喇叭设备树形失败");
        }

    }

    private List<DeviceTreeVO> buildIotMonitorTree(List<DeviceTreeVO> treeList, Boolean needDevice) {
        List<String> onlineNum = Arrays.asList("1","2","3");
        treeList.forEach(pro -> {
            if (pro.getList() != null) {
                pro.getList().forEach(city -> {
                    city.getList().forEach(country -> {
                        country.getList().forEach(townCode -> {
                            townCode.setOnlineNum((int) townCode.getList()
                                    .stream()
                                    .filter(townCodeVo -> onlineNum.contains(townCodeVo.getStatus())).count());
                            townCode.setNum(townCode.getList().size());
                            townCode.getList().forEach(device -> {
                                if (device.getIsMonitor().equals(String.valueOf(IndustryConstants.COLLECTION))) {
                                    device.setIsMonitorNum(1);
                                }
                            });
                            townCode.setIsMonitorNum(townCode.getList().stream().mapToInt(DeviceTreeVO::getIsMonitorNum).sum());
                            //子节点都收藏标记上级节点为收藏
                            if (townCode.getNum() == townCode.getIsMonitorNum()) {
                                townCode.setIsMonitor(String.valueOf(IndustryConstants.COLLECTION));
                            }
                            townCode.getList().forEach(dev -> {
                                dev.setStatus(onlineNum.contains(dev.getStatus()) ? Constants.ZERO : Constants.ONE);
                            });
                            if (needDevice == null || !needDevice) {
                                townCode.getList().clear();
                            }
                        });
                        country.setOnlineNum(country.getList().stream().mapToInt(DeviceTreeVO::getOnlineNum).sum());
                        country.setNum(country.getList().stream().mapToInt(DeviceTreeVO::getNum).sum());
                        country.setIsMonitorNum(country.getList().stream().mapToInt(DeviceTreeVO::getIsMonitorNum).sum());
                        //子节点都收藏标记上级节点为收藏
                        if (country.getNum() == country.getIsMonitorNum()) {
                            country.setIsMonitor(String.valueOf(IndustryConstants.COLLECTION));
                        }
                        //去掉异常乡镇
                        if (needDevice == null || !needDevice) {
                            country.setList(country.getList().stream().filter(townDto ->
                                    StringUtils.isNotEmpty(townDto.getCode()) && StringUtils.isNotEmpty(townDto.getName())).collect(Collectors.toList()));
                        } else {
                            //将有异常的乡镇数据移动到区县目录下
                            List<DeviceTreeVO> list = country.getList().stream().filter(townDto ->
                                    StringUtils.isEmpty(townDto.getCode())).collect(Collectors.toList());
                            list.forEach(deviceTreeVO -> {
                                country.getList().addAll(deviceTreeVO.getList());
                            });
                            country.setList(country.getList().stream().filter(townDto ->
                                    StringUtils.isNotEmpty(townDto.getCode())).collect(Collectors.toList()));
                        }

                    });
                    city.setOnlineNum(city.getList().stream().mapToInt(DeviceTreeVO::getOnlineNum).sum());
                    city.setNum(city.getList().stream().mapToInt(DeviceTreeVO::getNum).sum());
                    city.setIsMonitorNum(city.getList().stream().mapToInt(DeviceTreeVO::getIsMonitorNum).sum());
                    //子节点都收藏标记上级节点为收藏
                    if (city.getNum() == city.getIsMonitorNum()) {
                        city.setIsMonitor(String.valueOf(IndustryConstants.COLLECTION));
                    }
                    //去掉异常区县
                    city.setList(city.getList().stream().filter(areaDto ->
                            StringUtils.isNotEmpty(areaDto.getCode())).collect(Collectors.toList()));
                });
                pro.setOnlineNum(pro.getList().stream().mapToInt(DeviceTreeVO::getOnlineNum).sum());
                pro.setNum(pro.getList().stream().mapToInt(DeviceTreeVO::getNum).sum());
                pro.setIsMonitorNum(pro.getList().stream().mapToInt(DeviceTreeVO::getIsMonitorNum).sum());
                //子节点都收藏标记上级节点为收藏
                if (pro.getNum() == pro.getIsMonitorNum()) {
                    pro.setIsMonitor(String.valueOf(IndustryConstants.COLLECTION));
                }
                //去掉异常地市
                pro.setList(pro.getList().stream().filter(prioDto ->
                        StringUtils.isNotEmpty(prioDto.getCode())).collect(Collectors.toList()));
            } else {
                pro.setOnlineNum(0);
                pro.setNum(0);
                pro.setIsMonitor("0");
            }
        });
        return treeList;
    }

    /**
     * 修改喇叭设备
     * @param hornDeviceDTO 喇叭设备入参信息
     * @return Result<String>
     */
    @PostMapping("/updateHornDevice")
    public Result<String> updateHornDevice(@RequestBody HornDeviceDTO hornDeviceDTO){
        return iHornDeviceRepository.updateHornDevice(hornDeviceDTO);
    }

    /**
     * 实时喊话-开始喊话v1
     * @param talkReq 入参-设备编码
     * @return Result<LockResultVo>
     */
    @PostMapping("/startTalk")
    public Result<LockResultVo> startTalk(@RequestBody TalkReq talkReq){
       return iHornDeviceRepository.startTalk(talkReq);
    }

    /**
     * 实时喊话-停止v1
     * @param talkReq 入参-设备编码
     * @return Result<LockResultVo>
     */
    @PostMapping("/stopTalk")
    public Result<String> stopTalk(@RequestBody TalkReq talkReq){
        return iHornDeviceRepository.stopTalk(talkReq);
    }

    /**
     * 实时喊话-开始喊话v2
     */
    @Deprecated
    @PostMapping("/startTalk/v2")
    public Result<TalkResultVO<TalkV2VO>> startTalkV2(@RequestBody TalkV2DTO request){
        return iHornDeviceRepository.startTalkV2(request);
    }

    /**
     * 实时喊话-停止v2
     * @param talkReq 入参
     * @return Result<LockResultVo>
     */
    @Deprecated
    @PostMapping("/stopTalk/v2")
    public Result<String> stopTalkV2(@RequestBody TalkV2Req talkReq){
        return iHornDeviceRepository.stopTalkV2(talkReq);
    }

    /**
     * 实时喊话心跳v2
     * @param talkReq 入参
     * @return Result<LockResultVo>
     */
    @Deprecated
    @PostMapping("/heartbeat/v2")
    public Result<String> heartbeatV2(@RequestBody TalkV2Req talkReq){
        return iHornDeviceRepository.heartbeatV2(talkReq);
    }

    /**
     * 实时喊话-开始喊话v3
     */
    @PostMapping("/startTalk/v3")
    public Result<TalkV3VO> startTalkV3(@RequestBody TalkV2DTO request){
        return iHornDeviceRepository.startTalkV3(request);
    }

    /**
     * 实时喊话-停止v3
     * @param request 入参
     * @return Result<LockResultVo>
     */
    @PostMapping("/stopTalk/v3")
    public Result<List<TalkV2VO>> stopTalkV3(@RequestBody List<TalkV2Req> request){
        return iHornDeviceRepository.stopTalkV3(request);
    }

    /**
     * 实时喊话心跳v3
     * @param request 入参
     * @return Result<LockResultVo>
     */
    @PostMapping("/heartbeat/v3")
    public Result<List<TalkV2VO>> heartbeatV3(@RequestBody List<TalkV2Req> request){
        return iHornDeviceRepository.heartbeatV3(request);
    }

    /**
     * 实时喊话app-开始喊话v2
     */
    @PostMapping("/startTalk/app/v2")
    public Result<TalkResultVO<TalkV2VO>> startTalkAppV2(@RequestBody TalkV2DTO request){
        return iHornDeviceRepository.startTalkAppV2(request);
    }

    /**
     * 实时喊话app-停止v2
     * @param talkReq 入参
     * @return Result<LockResultVo>
     */
    @PostMapping("/stopTalk/app/v2")
    public Result<String> stopTalkAppV2(@RequestBody TalkV2Req talkReq){
        return iHornDeviceRepository.stopTalkAppV2(talkReq);
    }


    /**
     * 根据区域编码查询喇叭列表
     * @param hornDeviceDTO
     * @return
     */
    @PostMapping("/queryHornForArea")
    public Result<List<HornDeviceVo>> queryHornForArea(@RequestBody HornDeviceDTO hornDeviceDTO) {
        LoginUser currentUser = SecurityUtils.getLoginUser();
        if (null != currentUser) {
            SysUser tokenUser = currentUser.getUser();
            //铁塔视角切换客户视角后，目前从token里取不到客户的userId，所以需要根据userName查一下
            SysUser user = iHornDeviceRepository.selectUserOnlyByUserName(tokenUser.getUserName());
            hornDeviceDTO.setUserId(user.getUserId());
            hornDeviceDTO.setTenantId(tokenUser.getTenantId());
        }
        return Result.data(iHornDeviceRepository.queryHornForArea(hornDeviceDTO));
    }

    /**
     * 喇叭站址分布
     * @param hornDeviceDTO
     * @return
     */
    @Deprecated
    @PostMapping("/querySiteDevTree")
    public Result<List<HornDeviceVo>> querySiteDevTree(@RequestBody HornDeviceDTO hornDeviceDTO) {
        LoginUser currentUser = SecurityUtils.getLoginUser();
        if (null != currentUser) {
            SysUser tokenUser = currentUser.getUser();
            //铁塔视角切换客户视角后，目前从token里取不到客户的userId，所以需要根据userName查一下
            SysUser user = iHornDeviceRepository.selectUserOnlyByUserName(tokenUser.getUserName());
            hornDeviceDTO.setUserId(user.getUserId());
            hornDeviceDTO.setTenantId(tokenUser.getTenantId());
        }
        return Result.data(iHornDeviceRepository.querySiteDevTree(hornDeviceDTO));
    }

    /**
     * 根据站址编码查询喇叭列表
     * @param hornDeviceDTO
     * @return
     */
    @Deprecated
    @PostMapping("/queryHornForSite")
    public Result<List<HornDeviceVo>> queryHornForSite(@RequestBody HornDeviceDTO hornDeviceDTO) {
        LoginUser currentUser = SecurityUtils.getLoginUser();
        if (null != currentUser) {

            SysUser tokenUser = currentUser.getUser();
            //铁塔视角切换客户视角后，目前从token里取不到客户的userId，所以需要根据userName查一下
            SysUser user = iHornDeviceRepository.selectUserOnlyByUserName(tokenUser.getUserName());
            hornDeviceDTO.setUserId(user.getUserId());
            hornDeviceDTO.setTenantId(tokenUser.getTenantId());
        }
        return Result.data(iHornDeviceRepository.queryHornForSite(hornDeviceDTO));
    }

    /**
     * 查询喇叭设备距离信息
     * @param param
     * @return
     */
    @Deprecated
    @ApiOperation(value = "查询喇叭设备距离信息")
    @PostMapping("/queryHornDeviceDistanceInfo")
    public Result<List<DeviceDistanceVO>> queryHornDeviceDistanceInfo(@RequestBody DeviceDistanceDTO param) {
        return Result.data(iHornDeviceRepository.queryHornDeviceDistanceInfo(param));
    }

    /**
     * APP查询喇叭设备距离信息
     * @param param
     * @return
     */
    @Deprecated
    @ApiOperation(value = "APP查询喇叭设备距离信息")
    @PostMapping("/queryHornDeviceDistanceInfo4App")
    public Result<List<DeviceDistanceVO>> queryHornDeviceDistanceInfo4App(@RequestBody DeviceDistanceDTO param) {
        return Result.data(iHornDeviceRepository.queryHornDeviceDistanceInfo4App(param));
    }

    /**
     * 根据喇叭编码查询喇叭详情--APP端用
     * @param hornDeviceDTO 入参-设备编码
     * @return HornDeviceVo 喇叭详情
     */
    @PostMapping("/queryHornByCode")
    public Result<HornDeviceVo> queryHornByCode(@RequestBody HornDeviceDTO hornDeviceDTO){
        return iHornDeviceRepository.queryHornInfoByCode(hornDeviceDTO);
    }

    /**
     * 根据条件分页查询喇叭设备信息--APP端使用
     *
     * @param hornDeviceDTO 查询条件实体类
     * @return TableDataInfo
     */
    @Deprecated
    @PostMapping("/queryHornDeviceList")
    public Result<PageInfo<HornDeviceVo>> queryHornDeviceList(@RequestBody HornDeviceDTO hornDeviceDTO){
        return iHornDeviceRepository.queryHornDeviceList(hornDeviceDTO);
    }

}
