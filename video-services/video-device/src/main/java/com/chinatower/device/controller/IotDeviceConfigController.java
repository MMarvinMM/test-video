package com.chinatower.device.controller;

import com.chinatower.common.entity.Result;
import com.chinatower.common.page.TableDataInfo;
import com.chinatower.controller.TowerBaseController;
import com.chinatower.device.api.ISensorDeviceService;
import com.chinatower.device.dto.IotDeviceConfigVO;
import com.chinatower.device.entity.vo.SensorAttrVO;
import com.chinatower.device.mapper.IotDeviceConfigMapper;
import com.chinatower.device.service.IotDeviceConfigService;
import com.chinatower.platform.entity.device.DeviceReqDTO;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * video-device
 * 功能描述: 物联设备配置Controller信息
 * @author lzt
 */
@RestController
@Slf4j
@RequestMapping("/iotDeviceConfig")
@Api(value = "物联设备配置模块", tags = "物联设备配置模块")
public class IotDeviceConfigController extends TowerBaseController {

    @Resource
    private IotDeviceConfigService iotDeviceConfigService;
    @Resource
    private ISensorDeviceService iSensorDeviceService;

    @Resource
    private IotDeviceConfigMapper iotDeviceConfigMapper;
    @Resource
    private SystemFeignClient systemFeignClient;

    /**
     * 分页查询物联设备配置列表
     * @param iotDeviceConfigVO
     * @return TableDataInfo
     */
    @PostMapping("/queryIotDeviceConfigList")
    public TableDataInfo queryIotDeviceConfigList(@RequestBody IotDeviceConfigVO iotDeviceConfigVO ) {
        List<IotDeviceConfigVO> list =iotDeviceConfigService.queryIotDeviceConfigList(iotDeviceConfigVO);
        return getDataTable(list);
    }

    /**
     * 绑定物联设备配置
     * @param iotDeviceConfigVO
     * @return
     */
    @PostMapping("/bindIotDeviceConfig")
    public Result bindIotDeviceConfig(@RequestBody IotDeviceConfigVO iotDeviceConfigVO) {
        try {
            if (StringUtils.isBlank(iotDeviceConfigVO.getDeviceCode()) || StringUtils.isBlank(iotDeviceConfigVO.getIotDeviceCode())){
                return Result.fail("摄像机编码和物联设备编码不能为空！");
            }
            //查询当前物联设备是否有效
            if(iotDeviceConfigMapper.getOrderIotDeviceNum(iotDeviceConfigVO)==0){
                return Result.fail("当前物联设备没有订购信息！");
            }
            iotDeviceConfigService.bindIotDeviceConfig(iotDeviceConfigVO);
            return Result.success("绑定成功");
        } catch (Exception e) {
            log.error("绑定物联设备信息失败, e:", e);
            return Result.fail("绑定物联设备信息失败");
        }
    }

    /**
     * 解绑物联设备配置
     * @param iotDeviceConfigVO
     * @return
     */
    @PostMapping("/unbindIotDeviceConfig")
    public Result unbindIotDeviceConfig(@RequestBody IotDeviceConfigVO iotDeviceConfigVO) {
        try {
            if (StringUtils.isBlank(iotDeviceConfigVO.getId().toString())){
                return Result.fail("解绑设备主键Id不能为空！");
            }
            iotDeviceConfigService.unbindIotDeviceConfig(iotDeviceConfigVO);
            return Result.success("解绑成功");
        } catch (Exception e) {
            log.error("解绑物联设备信息失败, e:", e);
            return Result.fail("解绑物联设备信息失败");
        }
    }

    /**
     * 更换物联设备配置
     * @param iotDeviceConfigVO
     * @return
     */
    @PostMapping("/changeIotDeviceConfig")
    public Result changeIotDeviceConfig(@RequestBody IotDeviceConfigVO iotDeviceConfigVO) {
        try {
            if (StringUtils.isBlank(iotDeviceConfigVO.getDeviceCode()) || StringUtils.isBlank(iotDeviceConfigVO.getIotDeviceCode())){
                return Result.fail("摄像机编码和物联设备编码不能为空！");
            }
            //查询当前物联设备是否有效
            if(iotDeviceConfigMapper.getOrderIotDeviceNum(iotDeviceConfigVO)==0){
                return Result.fail("当前物联设备没有订购信息！");
            }
            iotDeviceConfigService.changeIotDeviceConfig(iotDeviceConfigVO);
            return Result.success("更换物联设备成功");
        } catch (Exception e) {
            log.error("更换物联设备信息失败, e:", e);
            return Result.fail("更换物联设备信息失败");
        }
    }

    /**
     * 获取物联设备监测数据
     * @param iotDeviceConfigVO
     * @return
     */
    @PostMapping("/getSensorData")
    public Result getSensorData(@RequestBody IotDeviceConfigVO iotDeviceConfigVO) {
        try {
            if (StringUtils.isBlank(iotDeviceConfigVO.getDeviceCode())){
                return Result.fail("摄像机编码不能为空！");
            }
            // 查询是否配置物联设备
            IotDeviceConfigVO configVO = iotDeviceConfigMapper.getIotDeviceConfig(iotDeviceConfigVO);
            if(null == configVO){
                return Result.fail("该摄像机暂无关联的物联设备！");
            }
            DeviceReqDTO param = new DeviceReqDTO();
            param.setDeviceCode(configVO.getIotDeviceCode());
            Result<List<SensorAttrVO>> result  = iSensorDeviceService.getSensorData(param);
            List<SensorAttrVO> list;
            if(200==result.getCode()){
                list  =  result.getData();
            }else{
                return Result.fail("获取物联设备监测数据失败！");
            }
            return Result.data(list);
        } catch (Exception e) {
            log.error("获取物联设备监测数据失败, e:", e);
            return Result.fail("获取物联设备监测数据失败");
        }
    }
    @PostMapping("/getAreaNameCaffeineMap")
    public Result<Map<String, String>> getAreaNameCaffeineMap(){
        List<String> areaCodes = new ArrayList<>();
        areaCodes.add("110000");
        Result<Map<String, String>> map =  systemFeignClient.getAreaNameCaffeineMap(areaCodes);
        return map;
    }

}
