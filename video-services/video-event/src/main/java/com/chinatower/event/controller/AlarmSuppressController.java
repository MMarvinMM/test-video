package com.chinatower.event.controller;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.core.web.controller.BaseController;
import com.chinatower.common.core.web.page.TableDataInfo;
import com.chinatower.common.entity.Result;
import com.chinatower.event.annotation.EventObsEncrypt;
import com.chinatower.event.entity.dto.AlarmAreaReq;
import com.chinatower.event.entity.dto.AlarmAreaResp;
import com.chinatower.event.entity.dto.AlarmAreaUpdateReq;
import com.chinatower.event.mapper.VmAlarmAreaSuppressMapper;
import com.chinatower.event.repository.AlarmSuppressService;
import com.chinatower.event.vo.SysLntlagConfigVO;
import com.chinatower.event.vo.entity.ChannelSuppressVO;
import com.chinatower.event.vo.entity.VmAlarmAreaSuppressVO;
import com.chinatower.event.vo.entity.VmAlarmSuppressStrategyVO;
import com.chinatower.log.annotation.TowerLog;
import com.chinatower.log.enums.BusinessType;
import com.chinatower.platform.annotation.DataScope;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * video-event
 * 功能说明: 告警压制API
 * @author: li.xufu
 * @date: 2022/5/8
 * @requestID 需求单号及需求名称
 */
@RestController
@Api(tags = {"/alarm/suppressStrategy - 联合告警压制配置"
})
@Slf4j
public class AlarmSuppressController extends BaseController {

    @Autowired
    private AlarmSuppressService alarmSuppressService;

    @Autowired
    private VmAlarmAreaSuppressMapper areaSuppressDao;

    /**
     * 功能描述：查询策略列表
     *
     * @param vmAlarmSuppressStrategyVo
     * @return TableDataInfo
     * @author zhen.yan
     * @date 2022/6/15 11:17
     * @version:
     */
    @PostMapping("/alarm/suppressStrategy/getList")
    @ApiOperation(value = "策略列表", notes = "策略列表")
    @DataScope(dbType = EbikeDataBaseConstant.BASE, tenantColumn="ss.tenant_id", industryCode="ss.industry_code")
    TableDataInfo getList(@RequestBody VmAlarmSuppressStrategyVO vmAlarmSuppressStrategyVo){
        startPage();
        List<VmAlarmSuppressStrategyVO> list = alarmSuppressService.getList(vmAlarmSuppressStrategyVo);
        return getDataTable(list);
    }

    /**
     * 功能描述：根据设备和通道查询对应绘制的告警区域列表
     *
     */
    @PostMapping("/alarm/getAreaList")
    Result<List<AlarmAreaResp>> getAreaList(@RequestBody AlarmAreaReq req){
        log.info("getAreaList-req:{}",JSON.toJSONString(req));
        VmAlarmAreaSuppressVO areaVo =  new VmAlarmAreaSuppressVO();
        areaVo.setDeviceCode(req.getDeviceCode());
        areaVo.setChannelCode(req.getChannelCode());
        List<VmAlarmAreaSuppressVO> list = areaSuppressDao.selectAll(areaVo);
        List<AlarmAreaResp> resList = new ArrayList<>();
        BeanUtil.copyProperties(list,resList);
        log.info("getAreaList-resp:{}",JSON.toJSONString(resList));
        return Result.data(resList);
    }

    /**
     * 功能描述：根据设备和通道查询对应绘制的告警区域列表
     *
     */
    @PostMapping("/alarm/updateAreaPosition")
    Result updateAreaPosition(@RequestBody List<AlarmAreaUpdateReq> req){
        log.info("updateAreaPosition-req:{}",JSON.toJSONString(req));
        return Result.data(areaSuppressDao.batchUpdate(req));
    }

    /**
     * 功能描述：查询所有策略列表
     *
     * @param vmAlarmSuppressStrategyVo
     * @return TableDataInfo
     * @author zhen.yan
     * @date 2022/6/15 11:17
     * @version:
     */
    @PostMapping("/alarm/suppressStrategy/getListAll")
    @ApiOperation(value = "策略列表", notes = "策略列表")
    TableDataInfo getListAll(@RequestBody VmAlarmSuppressStrategyVO vmAlarmSuppressStrategyVo){
        startPage();
        List<VmAlarmSuppressStrategyVO> list = alarmSuppressService.getListAll(vmAlarmSuppressStrategyVo);
        return getDataTable(list);
    }

    /**
     * 功能描述：查询策略信息
     *
     * @param vmAlarmSuppressStrategyVo
     * @return Result
     * @author zhen.yan
     * @date 2022/6/15 11:17
     * @version:
     */
    @PostMapping("/alarm/suppressStrategy/getStrategyInfo")
    @EventObsEncrypt
    @ApiOperation(value = "策略信息", notes = "策略信息")
    Result<VmAlarmSuppressStrategyVO> getStrategyInfo(@RequestBody VmAlarmSuppressStrategyVO vmAlarmSuppressStrategyVo){
        return alarmSuppressService.getStrategyInfo(vmAlarmSuppressStrategyVo);
    }

    /**
     * 功能描述：查询设备通道
     *
     * @param channelSuppressVo
     * @return Result
     * @author zhen.yan
     * @date 2022/6/15 11:18
     * @version:
     */
    @PostMapping("/alarm/suppressStrategy/queryChannelByDeviceCode")
    @ApiOperation(value = "查询设备通道", notes = "查询设备通道")
    Result<List<ChannelSuppressVO>> queryChannelByDeviceCode(@RequestBody ChannelSuppressVO channelSuppressVo){
        return alarmSuppressService.queryChannelByDeviceCode(channelSuppressVo);
    }

    /**
     * 功能描述：区域实时进行坐标转绘
     *
     * @param map
     * @return Result
     * @author zhen.yan
     * @date 2022/6/15 11:18
     * @version:
     */
    @PostMapping("/alarm/suppressStrategy/tranformPT")
    @ApiOperation(value = "区域实时进行坐标转绘", notes = "区域实时进行坐标转绘")
    Result tranformPT(@RequestBody Map map){
        VmAlarmAreaSuppressVO areaSuppressVo = JSONObject.parseObject(JSON.toJSONString(map), VmAlarmAreaSuppressVO.class);
        return alarmSuppressService.tranformPT(areaSuppressVo);
    }

    /**
     * 功能描述：新增告警压制
     *
     * @param map
     * @return Result
     * @author zhen.yan
     * @date 2022/6/15 11:18
     * @version:
     */
    @PostMapping("/alarm/suppressStrategy/add")
    @TowerLog(title = "新增告警压制", businessType = BusinessType.INSERT)
    @ApiOperation(value = "新增告警压制", notes = "新增告警压制")
    Result add(@RequestBody Map map){

        log.info("AlarmSuppressController-add:{}",JSON.toJSONString(map));
        VmAlarmSuppressStrategyVO vmAlarmSuppressStrategyVo = JSONObject.parseObject(JSON.toJSONString(map), VmAlarmSuppressStrategyVO.class);
        return alarmSuppressService.add(vmAlarmSuppressStrategyVo);
    }

    /**
     * 功能描述：获取默认名称
     * @param suppressStrategyVO
     * @return
     */
    @PostMapping("/alarm/suppressStrategy/getAutoStrategyName")
    public Result<String> getAutoStrategyName(@RequestBody VmAlarmSuppressStrategyVO suppressStrategyVO) {
        return alarmSuppressService.getAutoStrategyName(suppressStrategyVO);
    }

    /**
     * 功能描述：获取默认名称：告警压制策略名称、屏蔽区域名称
     * @param suppressStrategyVO
     * @return
     */
    @PostMapping("/alarm/suppressStrategy/getAutoStrategyAndRegionName")
    public Result<Map<String, Object>> getAutoStrategyAndRegionName(@RequestBody VmAlarmSuppressStrategyVO suppressStrategyVO) {
        return alarmSuppressService.getAutoStrategyAndRegionName(suppressStrategyVO);
    }

    /**
     * 功能描述：修改告警压制
     *
     * @param map
     * @return Result
     * @author zhen.yan
     * @date 2022/6/15 11:18
     * @version:
     */
    @PostMapping("/alarm/suppressStrategy/mod")
    @TowerLog(title = "修改告警压制", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "修改告警压制", notes = "修改告警压制")
    Result mod(@RequestBody Map map){
        VmAlarmSuppressStrategyVO vmAlarmSuppressStrategyVo = JSONObject.parseObject(JSON.toJSONString(map), VmAlarmSuppressStrategyVO.class);
        return alarmSuppressService.mod(vmAlarmSuppressStrategyVo);
    }

    /**
     * 功能描述：修改告警压制状态
     *
     * @param vmAlarmSuppressStrategyVo
     * @return Result
     * @author zhen.yan
     * @date 2022/6/15 11:19
     * @version:
     */
    @PostMapping("/alarm/suppressStrategy/modStatus")
    @TowerLog(title = "修改告警压制状态", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "修改告警压制状态", notes = "修改告警压制状态")
    Result modStatus(@RequestBody VmAlarmSuppressStrategyVO vmAlarmSuppressStrategyVo){
        return alarmSuppressService.modStatus(vmAlarmSuppressStrategyVo);
    }

    /**
     * 功能描述：PT转换
     *
     * @param map
     * @return Result
     * @author zhen.yan
     * @date 2022/6/15 11:19
     * @version:
     */
    @PostMapping("/alarm/suppressStrategy/ptTransform")
    @ApiOperation(value = "PT转换", notes = "PT转换")
    Result ptTransform(@RequestBody Map map){
        VmAlarmAreaSuppressVO areaSuppressVo = JSONObject.parseObject(JSON.toJSONString(map), VmAlarmAreaSuppressVO.class);
        return alarmSuppressService.ptTransform(areaSuppressVo);
    }

    /**
     * 功能描述：删除告警配置
     *
     * @param strategyId
     * @return Result
     * @author zhen.yan
     * @date 2022/6/15 11:19
     * @version:
     */
    @GetMapping("/alarm/suppressStrategy/del/{strategyId}")
    @TowerLog(title = "删除告警配置", businessType = BusinessType.DELETE)
    @ApiOperation(value = "删除告警配置", notes = "删除告警配置")
    Result del(@PathVariable(value="strategyId") Integer strategyId){
        return alarmSuppressService.del(strategyId);
    }

    /**
     * 功能描述：租户视角查看压制策略关系
     *
     * @param vmAlarmSuppressStrategyVo
     * @return Result
     * @author zhen.yan
     * @date 2022/6/15 11:19
     * @version:
     */
    @PostMapping("/alarm/suppressStrategy/checkAlarmSuppressName")
    @ApiOperation(value = "租户视角查看压制策略关系", notes = "租户视角查看压制策略关系")
    Result checkAlarmSuppressName(@RequestBody VmAlarmSuppressStrategyVO vmAlarmSuppressStrategyVo){
        return alarmSuppressService.checkAlarmSuppressName(vmAlarmSuppressStrategyVo);
    }

    /**
     * 功能描述：查询设备参数信息
     *
     * @param vo
     * @return Result
     * @author zhen.yan
     * @date 2022/6/15 11:20
     * @version:
     */
    @PostMapping("/alarm/suppressStrategy/getLntlagConfig")
    @ApiOperation(value = "查询设备参数信息", notes = "查询设备参数信息")
    Result<SysLntlagConfigVO> getLntlagConfig(@RequestBody VmAlarmAreaSuppressVO vo){
        return alarmSuppressService.getLntlagConfig(vo);
    }

    /**
     * 功能描述：查询设备参数信息
     *
     * @param areaSuppressVo
     * @return Result
     * @author
     * @date
     * @version:
     */
    @PostMapping("/alarm/suppressStrategy/queryPtzPositionPtz")
    @ApiOperation(value = "获取当前ptz", notes = "获取当前ptz")
    Result queryPtzPositionPtz(@RequestBody VmAlarmAreaSuppressVO areaSuppressVo){
        return alarmSuppressService.queryPtzPositionPtz(areaSuppressVo);
    }

    /**
     * 功能描述：查询区域屏蔽压制
     *
     * @param areaSuppressVo
     * @return Result
     * @author
     * @date
     * @version:
     */
    @PostMapping("/alarm/suppressStrategy/queryAreaSuppress")
    @ApiOperation(value = "获取区域屏蔽压制", notes = "获取区域屏蔽压制")
    Result queryAreaSuppress(@RequestBody VmAlarmAreaSuppressVO areaSuppressVo){
        startPage();
        return alarmSuppressService.getAreaSuppress(areaSuppressVo);
    }

    /**
     * 功能描述：查询区域屏蔽压制
     *
     * @param areaSuppressVo
     * @return Result
     * @author
     * @date
     * @version:
     */
    @PostMapping("/alarm/suppressStrategy/queryAreaSuppressList")
    @ApiOperation(value = "获取区域屏蔽压制", notes = "获取区域屏蔽压制")
    Result queryAreaSuppressList(@RequestBody VmAlarmAreaSuppressVO areaSuppressVo){
        startPage();
        return alarmSuppressService.getAreaSuppressList(areaSuppressVo);
    }

    /**
     * 功能描述：区域屏蔽压制名称重复校验
     *
     * @param areaSuppressVo
     * @return Result
     * @author
     * @date
     * @version:
     */
    @PostMapping("/alarm/suppressStrategy/areaSuppressCheck")
    @ApiOperation(value = "区域屏蔽压制名称重复校验", notes = "区域屏蔽压制名称重复校验")
    Result areaSuppressCheck(@RequestBody VmAlarmAreaSuppressVO areaSuppressVo){
        return alarmSuppressService.areaSuppressCheck(areaSuppressVo);
    }
}
