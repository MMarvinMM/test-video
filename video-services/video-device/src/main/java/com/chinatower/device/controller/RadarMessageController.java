package com.chinatower.device.controller;

import com.chinatower.common.core.dto.IotDeviceModeDTO;
import com.chinatower.common.entity.Result;
import com.chinatower.common.page.TableDataInfo;
import com.chinatower.controller.TowerBaseController;
import com.chinatower.device.dto.RadarDeviceRelDTO;
import com.chinatower.device.entity.vo.IotDeviceVO;
import com.chinatower.device.repository.IHornDeviceRepository;
import com.chinatower.device.repository.IIotDeviceModeRepository;
import com.chinatower.device.repository.IIotDeviceRepository;
import com.chinatower.device.utils.FormatDeviceTreeUtils;
import com.chinatower.log.annotation.TowerLog;
import com.chinatower.log.enums.BusinessType;
import com.chinatower.platform.annotation.DataScope;
import com.chinatower.platform.common.utils.StringUtils;
import com.chinatower.platform.constants.Constants;
import com.chinatower.platform.constants.DeviceConstants;
import com.chinatower.platform.entity.admin.SysDept;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.entity.device.DeviceDistanceDTO;
import com.chinatower.platform.entity.device.DeviceDistanceVO;
import com.chinatower.platform.entity.device.DeviceReqDTO;
import com.chinatower.platform.entity.device.DeviceTreeVO;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.service.SysAreaRootService;
import com.chinatower.platform.utils.SecurityUtils;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * video-device
 */
@RestController
@Slf4j
@Api(value = "资源管理雷达相关模块", tags = "雷达相关模块")
@RequestMapping("/radar")
public class RadarMessageController extends TowerBaseController {
    @Autowired
    IIotDeviceRepository iotDeviceRepository;

    @Autowired
    IIotDeviceModeRepository iotDeviceModeRepository;

    @Autowired
    IHornDeviceRepository iHornDeviceRepository;
    @Autowired
    private SysAreaRootService sysAreaRootService;




    /**
     * 查询雷达设备详情
     * @param iotDeviceVO
     * @return
     */
    @PostMapping("/queryRadarDeviceInfo")
    public Result<List<IotDeviceVO>> queryRadarDeviceInfo(@RequestBody IotDeviceVO iotDeviceVO){
        List<IotDeviceVO> radarDeviceInfo = iotDeviceRepository.queryRadarDeviceInfo(iotDeviceVO);
        return Result.data(radarDeviceInfo);
    }



    /**
     *
     * 查询行政组织雷达设备树
     *
     * @param iotDeviceVO 查询条件实体类
     * @return Result<List<DeviceTreeVO>>
     */
    @PostMapping("/getRadarMonitorOrgTree")
    public Result<List<DeviceTreeVO>> getRadarMonitorOrgTree(@RequestBody IotDeviceVO iotDeviceVO){
        try{
            LoginUser currentUser = SecurityUtils.getLoginUser();
            if (null != currentUser){
                SysUser tokenUser = currentUser.getUser();
                //铁塔视角切换客户视角后，目前从token里取不到客户的userId，所以需要根据userName查一下
                SysUser sysUser = iHornDeviceRepository.selectUserOnlyByUserName(tokenUser.getUserName());
                iotDeviceVO.setTenantId(tokenUser.getTenantId());
                iotDeviceVO.setUserId(sysUser.getUserId());
            }
            List<DeviceTreeVO> treeList = iotDeviceRepository.getRadarMonitorOrgTree(iotDeviceVO);
            if (!treeList.isEmpty()) {
                treeList.get(0).setFirst(1);
            }
            return Result.data(treeList);
        }catch (Exception e){
            log.error("查询行政组织雷达设备树形失败, e:", e);
            return Result.fail("查询行政组织雷达设备树形失败");
        }

    }

    /**
     *
     * 查询订购物联设备所属的行政区域树形  资源管理 客户视角 客户id为条件
     * 获取 行政区域及摄像头 资源管理 客户视角
     *
     * @param deviceInfoVO
     * @return
     */
    @Deprecated
    @PostMapping("/getRadarMonitorTree")
    public Result<List<DeviceTreeVO>> getRadarMonitorTree(@RequestBody IotDeviceVO deviceInfoVO) {

        LoginUser currentUser = SecurityUtils.getLoginUser();
        if (null != currentUser){
            SysUser tokenUser = currentUser.getUser();
            //铁塔视角切换客户视角后，目前从token里取不到客户的userId，所以需要根据userName查一下
            SysUser sysUser = iHornDeviceRepository.selectUserOnlyByUserName(tokenUser.getUserName());
            deviceInfoVO.setTenantId(tokenUser.getTenantId());
            deviceInfoVO.setUserId(sysUser.getUserId());
        }
        List<DeviceTreeVO> treeList = iotDeviceRepository.getRadarMonitorTree(deviceInfoVO);
        List<DeviceTreeVO> list = FormatDeviceTreeUtils.buildIotMonitorTree(treeList, deviceInfoVO.isNeedDevice(), Boolean.FALSE);
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
    }

    /**
     * 查询客户订购摄像机树
     */
    @DataScope(tenantColumn = "osd.tenant_id", industryCode = "osd.industry_code", acceptColumn = "osd.accept_node", deviceColumn = "m.device_code", alarmSourceVal = "8,10")
    @PostMapping("/getDeviceMonitorTree")
    public Result<List<DeviceTreeVO>> getDeviceMonitorTree(@RequestBody DeviceReqDTO deviceReqDTO) {
        if (StringUtils.isEmpty(deviceReqDTO.getTenantId())){
            deviceReqDTO.setTenantId(SecurityUtils.getLoginUser().getUser().getTenantId());
        }
        List<DeviceTreeVO> treeList = iotDeviceRepository.getDeviceMonitorTree(deviceReqDTO);
        List<DeviceTreeVO> list = FormatDeviceTreeUtils.buildIotMonitorTree(treeList, Boolean.TRUE, Boolean.FALSE);
        List<DeviceTreeVO> rootTree = new ArrayList<>(1);
        if (!treeList.isEmpty()) {
            // 补充中国根节点
            DeviceTreeVO root = new DeviceTreeVO();
            sysAreaRootService.createRootTreeVO(root);
            root.setList(list);
            root.setOnlineNum(root.getList().stream().mapToInt(DeviceTreeVO::getOnlineNum).sum());
            root.setNum(root.getList().stream().mapToInt(DeviceTreeVO::getNum).sum());
            rootTree.add(root);
        }
        return Result.data(rootTree);
    }

    /**
     * 查询客户订购摄像机通道树
     */
    @DataScope(tenantColumn = "osd.tenant_id", industryCode = "osd.industry_code", acceptColumn = "osd.accept_node", deviceColumn = "m.device_code", alarmSourceVal = "4")
    @PostMapping("/getDeviceChannelMonitorTree")
    public Result<List<DeviceTreeVO>> getDeviceChannelMonitorTree(@RequestBody DeviceReqDTO param) {
        if (StringUtils.isEmpty(param.getTenantId()) && CollectionUtils.isEmpty(param.getTenantIdList())){
            param.setTenantId(SecurityUtils.getLoginUser().getUser().getTenantId());
        }

        String platformCode = SecurityUtils.getLoginUser().getUser().getPlatformCode();
        List<DeviceTreeVO> treeList = iotDeviceRepository.getDeviceChannelMonitorTree(param, platformCode);
        List<DeviceTreeVO> list = FormatDeviceTreeUtils.buildMonitorTree4Customer(treeList, param.isNeedDevice());
        List<DeviceTreeVO> rootTree = new ArrayList<>(1);
        if (!treeList.isEmpty()) {
            // 补充中国根节点
            DeviceTreeVO root = new DeviceTreeVO();
            sysAreaRootService.createRootTreeVO(root);
            root.setList(list);
            root.setOnlineNum(root.getList().stream().mapToInt(DeviceTreeVO::getOnlineNum).sum());
            root.setNum(root.getList().stream().mapToInt(DeviceTreeVO::getNum).sum());
            rootTree.add(root);
        }
        return Result.data(rootTree);
    }

    /**
     * 获取部门列表(树形)
     */
    @Deprecated
    @DataScope(deptColumn = "d.dept_id", tenantColumn = "d.tenant_id")
    @ApiOperation(value = "获取部门列表(树形)")
    @PostMapping("/deptList")
    public Result<List<SysDept>> deptList(SysDept dept) {
        return Result.data(iotDeviceRepository.selectDeptTree(dept));
    }


    // 查询列表 资源管理 客户视角

    /**
     * 根据条件分页查询雷达设备信息
     *
     * @param: deviceInfoVO
     * @return: TableDataInfo
     * @author: zheng.yan
     * @date: 2022/5/7
     * @version:
     */
    @Deprecated
    @DataScope(tenantColumn = "od.tenant_id", industryCode = "od.industry_code", acceptColumn = "od.accept_node", deviceColumn = "m.device_code", alarmSourceVal = "8,10")
    @PostMapping("/queryRadarListPage")
    public TableDataInfo queryRadarListPage(@RequestBody IotDeviceVO deviceInfoVO) {
        if(deviceInfoVO.isExport()){
            deviceInfoVO.setPageNum(null);
            deviceInfoVO.setPageSize(null);
        } else {
            startPage();
        }
        deviceInfoVO.setTenantId(SecurityUtils.getLoginUser().getUser().getTenantId());
        List<IotDeviceVO> list = iotDeviceRepository.queryRadarListPage(deviceInfoVO);
        return getDataTable(list);
    }

    /**
     * 根据条件分页查询雷达设备信息--APP使用
     *
     * @param deviceInfoVO
     * @return TableDataInfo
     */
    @Deprecated
    @DataScope(tenantColumn = "od.tenant_id", industryCode = "od.industry_code", acceptColumn = "od.accept_node", deviceColumn = "m.device_code", alarmSourceVal = "10")
    @PostMapping("/queryRadarList")
    public Result<PageInfo<IotDeviceVO>> queryRadarDeviceList(@RequestBody IotDeviceVO deviceInfoVO) {
        deviceInfoVO.setTenantId(SecurityUtils.getLoginUser().getUser().getTenantId());
        return iotDeviceRepository.queryRadarOrIotDeviceList(deviceInfoVO,Constants.ONE_NUM);
    }

    /**
     * 关联摄像机 设备管理 铁塔视角 通过摄像机device_code 查询关联的摄像机 信息
     *
     * @param radarDeviceRelDTO
     * @return
     */
    @PostMapping("/getDeviceListByRadar")
    @DataScope(tenantColumn = "oc.tenant_id")
    public Result getDeviceListByRadar(@RequestBody RadarDeviceRelDTO radarDeviceRelDTO) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null || loginUser.getUser() == null) {
            return Result.fail("获取登入信息失败");
        }
        radarDeviceRelDTO.setTenantId(loginUser.getUser().getTenantId());
        radarDeviceRelDTO.setIndustryCode(loginUser.getUser().getIndustryCode());
        return iotDeviceRepository.getDeviceListByRadar(radarDeviceRelDTO);
    }

    //修改  资源管理 铁塔视角（只能修改所属组织）

    /**
     * 修改物联设备信息
     *
     * @param: qryCond
     * @return: Result<TenantIotDeviceConfVO>
     * @author: zheng.yan
     * @date: 2022/5/9
     * @version:
     */
    @TowerLog(title = "修改雷达设备信息", businessType = BusinessType.UPDATE)
    @PostMapping("/updateRadarOrg")
    public Result updateRadarOrg(@RequestBody IotDeviceVO iotDevice) {
        if (StringUtils.isEmpty(iotDevice.getTenantId())) {
            iotDevice.setTenantId(SecurityUtils.getLoginUser().getUser().getTenantId());
        }
        iotDevice.setDeviceType(String.valueOf(DeviceConstants.DEVICETYPE_10));
        iotDeviceRepository.updateIotDevice(iotDevice);
        return Result.success("修改成功");
    }

    //关联摄像机-修改 资源管理 铁塔视角

    /**
     * 关联摄像机-修改 资源管理 铁塔视角
     *
     * @param radarDeviceRelDTO
     * @return
     */
    @TowerLog(title = "修改关联摄像机信息", businessType = BusinessType.UPDATE)
    @PostMapping("/saveOrUpdateRel")
    public Result saveOrUpdateRel(@RequestBody RadarDeviceRelDTO radarDeviceRelDTO) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null || loginUser.getUser() == null) {
            return Result.fail("获取登入信息失败");
        }
        radarDeviceRelDTO.setTenantId(loginUser.getUser().getTenantId());
        return iotDeviceRepository.saveOrUpdateRel(radarDeviceRelDTO);
    }

    /**
     *
     * 根据站址编码查询雷达列表
     * @param iotDeviceVO
     * @return
     */
    @Deprecated
    @PostMapping("/queryRadarDeviceBySite")
    public Result<List<IotDeviceVO>> queryRadarDeviceBySite(@RequestBody IotDeviceVO iotDeviceVO){
        LoginUser currentUser = SecurityUtils.getLoginUser();
        if (null != currentUser){
            SysUser tokenUser = currentUser.getUser();
            //铁塔视角切换客户视角后，目前从token里取不到客户的userId，所以需要根据userName查一下
            SysUser sysUser = iHornDeviceRepository.selectUserOnlyByUserName(tokenUser.getUserName());
            iotDeviceVO.setTenantId(tokenUser.getTenantId());
            iotDeviceVO.setUserId(sysUser.getUserId());
        }
        return Result.data(iotDeviceRepository.queryRadarDeviceBySite(iotDeviceVO));
    }

    /**
     *
     * 根据区域编码查询雷达列表
     * @param iotDeviceVO
     * @return
     */
    @Deprecated
    @PostMapping("queryRadarDeviceByArea")
    public Result<List<IotDeviceVO>> queryRadarDeviceByArea(@RequestBody IotDeviceVO iotDeviceVO){
        LoginUser currentUser = SecurityUtils.getLoginUser();
        if (null != currentUser){
            SysUser tokenUser = currentUser.getUser();
            //铁塔视角切换客户视角后，目前从token里取不到客户的userId，所以需要根据userName查一下
            SysUser sysUser = iHornDeviceRepository.selectUserOnlyByUserName(tokenUser.getUserName());
            iotDeviceVO.setTenantId(tokenUser.getTenantId());
            iotDeviceVO.setUserId(sysUser.getUserId());
        }
        return Result.data(iotDeviceRepository.queryRadarDeviceByArea(iotDeviceVO));
    }


    /**
     * 功能描述 查询雷达设备型号【前端在用】
     *
     * @param model
     * @return Result
     * @author lishihao
     * @date 2023/4/4
     */
    @ApiOperation(value = "查询雷达设备类型列表", notes = "查询雷达设备类型列表")
    @PostMapping("/queryIotDeviceModeList")
    public Result<Object> queryIotDeviceModeList(@RequestBody IotDeviceModeDTO model) {
        return iotDeviceModeRepository.queryIotDeviceModeList(model);
    }

    /**
     *
     * 周边分析-周边雷达信息搜索
     * @param param
     * @return
     */
    @Deprecated
    @PostMapping("/queryRadarDevDistanceInfo")
    public Result<List<DeviceDistanceVO>> queryRadarDevDistanceInfo(@RequestBody DeviceDistanceDTO param) {
        return Result.data(iotDeviceRepository.queryRadarDevDistanceInfo(param));
    }

    /**
     *
     * 周边分析-周边雷达信息搜索
     * @param param
     * @return
     */
    @Deprecated
    @PostMapping("/queryRadarDevDistanceInfo4App")
    public Result<List<DeviceDistanceVO>> queryRadarDevDistanceInfo4App(@RequestBody DeviceDistanceDTO param) {
        return Result.data(iotDeviceRepository.queryRadarDevDistanceInfo4App(param));
    }

    /**
     * 根据雷达编码查询雷达详情--APP端用  合并到/deviceCommon/getDeviceInfo
     * @param iotDeviceVO 入参-设备编码
     * @return IotDeviceVO 喇叭详情
     */
    @Deprecated
    @PostMapping("/queryRadarDeviceByCode")
    @DataScope(tenantColumn = "osd.tenant_id", industryCode = "osd.industry_code", acceptColumn = "osd.accept_node", deviceColumn = "m.device_code", alarmSourceVal = "8,10")
    public Result<IotDeviceVO> queryRadarDeviceByCode(@RequestBody IotDeviceVO iotDeviceVO){
        return iotDeviceRepository.queryRadarDeviceByCode(iotDeviceVO);
    }

}
