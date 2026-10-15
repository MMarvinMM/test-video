package com.chinatower.video.modules.arButton.handler;

import cn.hutool.extra.spring.SpringUtil;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.video.modules.arButton.entity.ArButtonDeviceRelation;
import com.chinatower.video.modules.arButton.entity.ArButtonInfo;
import com.chinatower.video.modules.arButton.mapper.ArbuttonDeviceRelationMapper;
import com.chinatower.video.modules.arButton.mapper.ButtonMapper;
import com.chinatower.video.modules.constants.ArConstants;
import com.chinatower.video.modules.device.entity.DeviceDTO;
import com.chinatower.video.modules.device.mapper.DeviceMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;

/**
 * @program: video-platform
 * @ClassName: ArButtonHandler
 * @description: AR大屏按钮配置帮助类
 * @author: fengty
 * @create: 2024-06-06 17:45
 */
@Component
@Slf4j
public class ArButtonHandler {
    @Autowired
    DeviceMapper deviceMapper;

    @Autowired
    ArbuttonDeviceRelationMapper arbuttonDeviceRelationMapper;
    @Autowired
    private ButtonMapper buttonMapper;

    /**
     * 判断是否返回错误信息，如果需要返回错误信息，则组装错误信息
     * @param returnMap
     * @param list
     * @return
     */
    public boolean buildReturnMap(SysUser user, Map<String, Object> returnMap, List<String> list) throws Exception{
        DeviceDTO deviceDTO = new DeviceDTO();
        deviceDTO.setDeviceCodes(list);
        deviceDTO.setTenantId(user.getTenantId());
        deviceDTO.setAcceptNode(user.getPlatformCode());
        deviceDTO.setIndustryCode(user.getIndustryCode());
        try {
            List<DeviceDTO> deviceList  = deviceMapper.getMonitorList(deviceDTO);
            String hint = buildReturnStr(deviceList);
            if(StringUtils.isNotBlank(hint)) {
                returnMap.put(ArConstants.MSG_KEY,  ArConstants.RETURN_STRING_BUTTON_PART_ONE+hint+ArConstants.RETURN_STRING_BUTTON_PART_TWO);
                return true;
            }
        } catch (Exception e) {
            log.error("查询摄像机名称出现错误", e);
            throw new Exception("操作失败！");
        }
        return false;
    }

    /**
     * 判断重名
     * @param buttonInfo
     * @param user
     * @return
     */
    public int getTheSameName(ArButtonInfo buttonInfo, SysUser user) {
        buttonInfo.setTenantId(user.getTenantId());
        buttonInfo.setIndustryCode(user.getIndustryCode());
        buttonInfo.setAcceptNode(user.getPlatformCode());
        buttonInfo.setDeleteFlag(0);
        Integer rangeCache = buttonInfo.getDeviceRange();
        buttonInfo.setDeviceRange(null);
        // 查询 同一节点同一客户同一行业应用下的按钮名称是否有重复
        int count = buttonMapper.selectCountByConditions(buttonInfo);
        buttonInfo.setDeviceRange(rangeCache);
        return count;
    }

    /**
     *  构建返回信息，筛查出所有配置超限的摄像机
     * @param deviceDTOS
     * @return
     */
    public String buildReturnStr(List<DeviceDTO> deviceDTOS) {
        // 此变量用于整合返回信息
        String hint = "";
        // 此变量用于拼接返回信息
        StringBuilder sb = new StringBuilder("");
        for(DeviceDTO dto : deviceDTOS) {
            sb.append(dto.getDeviceName()).append("、");
        }
        if(StringUtils.isNotBlank(sb)) {
            sb.setLength(sb.length() - 1);
            hint = sb.toString();
        }
        return hint;
    }

    /**
     * 判断是否超限
     * @param buttonInfo
     * @param originalButton
     * @param user
     * @param returnMap
     * @throws Exception
     */
    @Transactional(isolation = Isolation.REPEATABLE_READ,propagation = Propagation.REQUIRED)
    public void handleEnableButtonIsOverLimit(ArButtonInfo buttonInfo, ArButtonInfo originalButton, SysUser user, Map<String, Object> returnMap) throws Exception {
        buttonInfo.setTenantId(originalButton.getTenantId());
        buttonInfo.setIndustryCode(originalButton.getIndustryCode());
        buttonInfo.setAcceptNode(originalButton.getAcceptNode());
        buttonInfo.setDeleteFlag(0);
        buttonInfo.setId(null);
        buttonInfo.setButtonName(null);
        buttonInfo.setDeviceRange(ArConstants.DICT_ALL);
        buttonInfo.setEnableStatus(1);
        // 查询指定范围为全部切位启用状态的按钮配置有几个
        int count = buttonMapper.selectCountByConditions(buttonInfo);
        if (count > ArConstants.TURN_UPPER_LIMIT && buildReturnMap(user, returnMap, null)){
            throw new Exception(returnMap.get(ArConstants.MSG_KEY).toString());
        }
        ArButtonDeviceRelation arButtonDeviceRelation = initRelationForSelectDB(buttonInfo, originalButton, count);
        List<String> overLimitDeviceCode = arbuttonDeviceRelationMapper.selectListForGroupByConditions(arButtonDeviceRelation);
        if (!CollectionUtils.isEmpty(overLimitDeviceCode) && buildReturnMap(user, returnMap, overLimitDeviceCode)) {
            throw new Exception(returnMap.get(ArConstants.MSG_KEY).toString());
        }
    }

    /**
     * 初始化数据库查询参数对象
     * @param buttonInfo
     * @param originalButton
     * @param count
     * @return
     */
    public static ArButtonDeviceRelation initRelationForSelectDB(ArButtonInfo buttonInfo, ArButtonInfo originalButton, int count) {
        ArButtonDeviceRelation arButtonDeviceRelation = new ArButtonDeviceRelation();
        arButtonDeviceRelation.setIndustryCode(originalButton.getIndustryCode());
        arButtonDeviceRelation.setAcceptNode(originalButton.getAcceptNode());
        arButtonDeviceRelation.setTenantId(originalButton.getTenantId());
        arButtonDeviceRelation.setEnableStatus(buttonInfo.getEnableStatus());
        arButtonDeviceRelation.setVarCount(ArConstants.TURN_UPPER_LIMIT- count);
        if (buttonInfo.getDeviceRange() != null && buttonInfo.getDeviceRange() != ArConstants.DICT_ALL) {
            arButtonDeviceRelation.setButtonId(originalButton.getId());
        } else if (buttonInfo.getDeviceRange() == null && originalButton.getDeviceRange() !=  ArConstants.DICT_ALL) {
            arButtonDeviceRelation.setButtonId(originalButton.getId());
        }
        return arButtonDeviceRelation;
    }
}
