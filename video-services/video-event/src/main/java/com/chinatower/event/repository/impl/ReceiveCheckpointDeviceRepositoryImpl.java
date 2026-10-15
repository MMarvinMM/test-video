package com.chinatower.event.repository.impl;

import com.alibaba.fastjson.JSON;
import com.chinatower.common.core.web.domain.AjaxResult;
import com.chinatower.common.entity.Result;
import com.chinatower.event.constant.MessageConstants;
import com.chinatower.event.entity.vo.AlarmOriginalAreaVO;
import com.chinatower.event.entity.vo.AlarmOriginalPointVO;
import com.chinatower.event.entity.vo.ReceiveAlarmVO;
import com.chinatower.event.mapper.CheckpointDeviceAlarmMapper;
import com.chinatower.event.mapper.CheckpointDeviceMapper;
import com.chinatower.event.repository.IReceiveCheckpointDeviceRepository;
import com.chinatower.event.utils.PKUtils;
import com.chinatower.event.vo.*;
import com.chinatower.platform.constants.Constants;
import com.chinatower.platform.constants.DeviceConstants;
import com.chinatower.platform.entity.admin.SysConfig;
import com.chinatower.platform.entity.devicemgmt.vo.CheckpointDeviceInfoVO;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author：zl
 * @Description：卡口管理接口
 * @Date： 2022/8/10
 * @version release_v1.5
 * @requestID  中国铁塔_铁塔视联平台__卡口管理
 */
@Slf4j
@Service
public class ReceiveCheckpointDeviceRepositoryImpl implements IReceiveCheckpointDeviceRepository {

    @Autowired
    private CheckpointDeviceAlarmMapper checkpointDeviceAlarmMapper;
    @Autowired
    private CheckpointDeviceMapper checkpointDeviceMapper;

    @Autowired
    private SystemFeignClient systemFeignClient;

    /**
     * 功能描述：接收车辆卡口摄像机告警  增加industryCode appVerCode
     * @Author：zl
     * @param receiveAlarmVO
     * @return Result
     * @version release_v1.5
     * @requestID  中国铁塔_铁塔视联平台__出入口管理
     */
    @ApiOperation("接收车辆卡口摄像机告警")
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result receiveCheckpointDeviceAlarm(ReceiveAlarmVO receiveAlarmVO) {
        // 根据车辆卡口摄像机告警中的设备编码+通道编码查询已配置的有效卡口设备信息
        CheckpointDeviceInfoVO vo = new CheckpointDeviceInfoVO();
        vo.setDeviceCode(receiveAlarmVO.getDeviceCode());
        vo.setChannelCode(receiveAlarmVO.getChannelCode());
        List<CheckpointDeviceInfoVO> checkpointDeviceInfoList = checkpointDeviceMapper.getCheckpointDeviceList(vo);
        // 如果未匹配到已配置且订购的卡口设备，则记录错误日志，不保存告警信息
        if (CollectionUtils.isEmpty(checkpointDeviceInfoList)) {
            log.error(MessageConstants.NO_CUSTOMER_MESSAGE);
            return null;
        }
        List<ReceiveCheckpointAlarmVO> list = new ArrayList<>();
        List<CheckpointAlarmOriginalAreaVO> alarmAreas = new ArrayList<>();
        List<CheckpointAlarmOriginalPointVO> alarmPointLists = new ArrayList<>();
        ReceiveCheckpointAlarmVO receiveCheckpointAlarmBaseVO = new ReceiveCheckpointAlarmVO();
        BeanUtils.copyProperties(receiveAlarmVO, receiveCheckpointAlarmBaseVO);
        // 获取告警信息中的卡口设备拓展字段
        RadarParam eExtendField = JSON.parseObject(String.valueOf(receiveAlarmVO.getExtendField()), RadarParam.class);
        if (!StringUtils.isEmpty(eExtendField)) {
            receiveCheckpointAlarmBaseVO.setVehicleColor(eExtendField.getVehicleColor());
            receiveCheckpointAlarmBaseVO.setVehicleNumColor(eExtendField.getVehicleNumColor());
            receiveCheckpointAlarmBaseVO.setVehicleType(eExtendField.getVehicleType());
            receiveCheckpointAlarmBaseVO.setVehicleNumber(eExtendField.getVehicleNumber());
            if(StringUtils.isEmpty(receiveCheckpointAlarmBaseVO.getOriginalPictureUrl())){
                receiveCheckpointAlarmBaseVO.setOriginalPictureUrl(getUrl(receiveAlarmVO.getAccessNode(),eExtendField.getVehicleLitimg()));
            }else{
                if(!StringUtils.isEmpty(eExtendField.getVehicleLitimg())){
                    receiveCheckpointAlarmBaseVO.setOriginalPictureUrl(receiveCheckpointAlarmBaseVO.getOriginalPictureUrl()+ Constants.COMMA +getUrl(receiveAlarmVO.getAccessNode(),eExtendField.getVehicleLitimg()));
                }
            }
        }
        receiveCheckpointAlarmBaseVO.setDeleteFlag(DeviceConstants.DELETE_FLAG_NORMAL_INT);
        List<AlarmOriginalAreaVO> alarmArea = receiveAlarmVO.getAlarmArea();
        for (CheckpointDeviceInfoVO checkpointDeviceInfo : checkpointDeviceInfoList) {
            ReceiveCheckpointAlarmVO receiveCheckpointAlarmVO = new ReceiveCheckpointAlarmVO();
            BeanUtils.copyProperties(receiveCheckpointAlarmBaseVO, receiveCheckpointAlarmVO);
            String warningOrderId = PKUtils.getWarnOrderId(checkpointDeviceInfo.getTenantId(),receiveCheckpointAlarmVO.getAlarmTime());
            receiveCheckpointAlarmVO.setWarningOrderId(warningOrderId);
            //增加industryCode appVerCode
            receiveCheckpointAlarmVO.setIndustryCode(checkpointDeviceInfo.getIndustryCode());
            receiveCheckpointAlarmVO.setAppVerCode(checkpointDeviceInfo.getAppVerCode());
            receiveCheckpointAlarmVO.setTenantId(checkpointDeviceInfo.getTenantId());
            receiveCheckpointAlarmVO.setTenantName(checkpointDeviceInfo.getTenantName());
            receiveCheckpointAlarmVO.setProvinceCode(checkpointDeviceInfo.getProvinceId());
            receiveCheckpointAlarmVO.setProvinceName(checkpointDeviceInfo.getProvinceName());
            receiveCheckpointAlarmVO.setCityCode(checkpointDeviceInfo.getCityId());
            receiveCheckpointAlarmVO.setCityName(checkpointDeviceInfo.getCityName());
            receiveCheckpointAlarmVO.setCountyCode(checkpointDeviceInfo.getCountyId());
            receiveCheckpointAlarmVO.setCountyName(checkpointDeviceInfo.getCountyName());
            list.add(receiveCheckpointAlarmVO);
            // 告警信息中报警区域不为空
            if (!CollectionUtils.isEmpty(alarmArea)) {
                for (AlarmOriginalAreaVO area : alarmArea) {
                    CheckpointAlarmOriginalAreaVO areaVO = new CheckpointAlarmOriginalAreaVO();
                    BeanUtils.copyProperties(area, areaVO);
                    areaVO.setWarningOrderId(warningOrderId);
                    alarmAreas.add(areaVO);
                    List<AlarmOriginalPointVO> alarmPointList = area.getAlarmPointList();
                    // 告警信息中告警点列表不为空
                    if (!CollectionUtils.isEmpty(alarmPointList)) {
                        for (AlarmOriginalPointVO point : alarmPointList) {
                            CheckpointAlarmOriginalPointVO pointVO = new CheckpointAlarmOriginalPointVO();
                            BeanUtils.copyProperties(point, pointVO);
                            pointVO.setWarningOrderId(warningOrderId);
                            pointVO.setAlarmAreaId(area.getAlarmAreaId());
                            alarmPointLists.add(pointVO);
                        }
                    }
                }
            }
        }
        checkpointDeviceAlarmMapper.insertCheckpointDeviceAlarmInfo(list);

        if (!CollectionUtils.isEmpty(alarmAreas)) {
            checkpointDeviceAlarmMapper.insertOriginalAreas(alarmAreas);
        }
        if (!CollectionUtils.isEmpty(alarmPointLists)) {
            checkpointDeviceAlarmMapper.insertOriginalPoints(alarmPointLists);
        }
        return Result.success("");
    }

    public String getUrl(String accessNode,String url){
        //中心节点文件地址无需拼接属地编码
        if("10000000001".equals(accessNode)){
            return url;
        }
        //纯属化不拼接节点编码
        SysConfig sysConfig = new SysConfig();
        sysConfig.setConfigKey("dependence_manage");
        AjaxResult result1 = systemFeignClient.getConfigInfo(sysConfig);
        log.info("获取当前环境字段返回:{}",JSON.toJSONString(result1));
        if(result1 != null && 200 == Integer.valueOf(String.valueOf(result1.get("code")))) {
            SysConfig sysConfigInfo = JSON.parseObject(JSON.toJSONString(result1.get("data")), SysConfig.class);
            if (sysConfigInfo != null && com.chinatower.event.constants.Constants.ZERO.equals(sysConfigInfo.getConfigValue())) {
                return url;
            }
        }
        if(StringUtils.isEmpty(url)){
            return url;
        }
        String[] urls = url.split(com.chinatower.event.constants.Constants.COMMA);
        String resUrl="";
        for (String realUrl:urls) {
            if(realUrl.contains("http") || realUrl.contains("https")){
                //将属地编码拼接在域名之后
                int thirdSlashIndex = realUrl.indexOf('/', realUrl.indexOf('/', realUrl.indexOf('/') + 1) + 1);
                // 拼接字符串，在第三个斜杠之后
                StringBuilder result = new StringBuilder(realUrl.substring(0, thirdSlashIndex + 1));
                result.append(accessNode+"/");
                result.append(realUrl.substring(thirdSlashIndex + 1));
                realUrl = result.toString();
            }else{
                realUrl = "/"+accessNode+realUrl;
            }
            if(StringUtils.isEmpty(resUrl)){
                resUrl = realUrl;
            }else{
                resUrl += com.chinatower.event.constants.Constants.COMMA+realUrl;
            }
        }
        return resUrl;
    }
}
