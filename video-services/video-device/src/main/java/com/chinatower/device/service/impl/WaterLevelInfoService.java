package com.chinatower.device.service.impl;

import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.entity.TreeSelect;
import com.chinatower.device.dto.SysAreaBusinessWaterLevel;
import com.chinatower.device.dto.WaterLevelInfo;
import com.chinatower.device.mapper.DeviceInfoMapper;
import com.chinatower.device.mapper.WaterLevelInfoMapper;
import com.chinatower.device.service.IWaterLevelInfoService;
import com.chinatower.platform.annotation.DataScope;
import com.chinatower.platform.common.utils.TreeUtils;
import com.chinatower.platform.constants.DeviceConstants;
import com.chinatower.platform.entity.admin.SysArea;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.entity.devicemgmt.vo.SysAreaBusinessCheckpointVO;
import com.chinatower.platform.utils.SecurityUtils;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @program: video-platform
 * @ClassName: WaterLevelInfoService
 * @description: 水位服务层接口实现
 * @author: fengty
 * @create: 2024-12-12 11:35
 */
@Service
@Slf4j
public class WaterLevelInfoService implements IWaterLevelInfoService {
    @Autowired
    WaterLevelInfoMapper waterLevelInfoMapper;

    @Autowired
    private DeviceInfoMapper deviceInfoMapper;
    
    private final String msg = "添加水位球设备信息失败";

    @Override
    @DataScope(industryCode = "t.industry_code",tenantColumn = "t.tenant_id",deviceColumn = "d.device_code",acceptColumn="t.accept_node")
    public List<WaterLevelInfo> queryWaterLevelDeviceInfoPage(WaterLevelInfo waterLevelInfo) {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        waterLevelInfo.setTenantId(user.getTenantId());
        waterLevelInfo.setIndustryCode(user.getIndustryCode());
        PageHelper.startPage(waterLevelInfo.getPageNum(), waterLevelInfo.getPageSize());
        return waterLevelInfoMapper.queryWaterLevelDeviceInfoList(waterLevelInfo);
    }

    @Transactional
    @Override
    public int addWaterDevice(List<String> deviceCodes) {
        try {
            List<WaterLevelInfo> list = waterLevelInfoMapper.queryWaterDeviceList(deviceCodes);
            if(CollectionUtils.isEmpty(list)){
                return 0;
            }
            SysUser user = SecurityUtils.getLoginUser().getUser();
            for(WaterLevelInfo waterLevelInfo : list){
                waterLevelInfo.setTenantId(user.getTenantId());
                waterLevelInfo.setIndustryCode(user.getIndustryCode());
                waterLevelInfo.setCreator(user.getUserName());
                waterLevelInfo.setAppVerCode(user.getAppVerCode());
                waterLevelInfo.setAcceptNode(user.getPlatformCode());
            }
            int count = waterLevelInfoMapper.insertWaterLevelInfo(list);
            if(count == list.size()){
                return count;
            } else {
                throw new RuntimeException(msg);
            }
        } catch (Exception e) {
            log.error(msg, e);
            throw new RuntimeException(msg);
        }
    }

    @Override
    public int deleteWaterDevice(List<Integer> ids) {
        try {
            SysUser user = SecurityUtils.getLoginUser().getUser();
            String userName = user.getUserName();
            return waterLevelInfoMapper.logicDeleteByIds(ids, userName);
        } catch (Exception e) {
            log.error("删除水位球设备信息失败", e);
            throw new RuntimeException("删除水位球设备信息失败");
        }
    }

    @Override
    @DataScope(industryCode = "a.industry_code",tenantColumn = "a.tenant_id",deviceColumn = "d.device_code",acceptColumn="a.accept_node")
    public List<TreeSelect> initWaterLevelDeviceInfo(WaterLevelInfo waterLevelInfo) {
        List<TreeSelect> tree = new ArrayList<>();
        List<SysAreaBusinessWaterLevel> chanList = waterLevelInfoMapper.getDeviceList(waterLevelInfo);
        log.info("chanList:{}", chanList);
        Set<SysAreaBusinessWaterLevel> areaSet = new HashSet<>();
        List<String> areaCodes = chanList.stream().filter(Objects::nonNull)
                .flatMap(x -> Stream.of(com.chinatower.platform.common.utils.StringUtils.nvl(x.getProvinceId(), org.apache.commons.lang3.StringUtils.EMPTY),
                        com.chinatower.platform.common.utils.StringUtils.nvl(x.getCityId(), org.apache.commons.lang3.StringUtils.EMPTY),
                        com.chinatower.platform.common.utils.StringUtils.nvl(x.getCountryId(), org.apache.commons.lang3.StringUtils.EMPTY)))
                .distinct().collect(Collectors.toList());
        waterLevelInfo.setAreaCodes(areaCodes);
        List<SysAreaBusinessWaterLevel> arealist = waterLevelInfoMapper.selectSysArea(waterLevelInfo);
        log.info("arealist:{}", arealist);
        List<SysAreaBusinessWaterLevel> removeArealist = new ArrayList<>();
        for (SysAreaBusinessWaterLevel sysAreaBusinessWaterLevel : arealist) {
            for (SysAreaBusinessWaterLevel vo : chanList) {
                if(StringUtils.isEmpty(vo.getProvinceId())||StringUtils.isEmpty(vo.getCityId())||StringUtils.isEmpty(vo.getCountryId())){
                    removeArealist.add(vo);
                } else if (vo.getProvinceId().equals(sysAreaBusinessWaterLevel.getAreaCode()) || vo.getCityId().equals(sysAreaBusinessWaterLevel.getAreaCode()) || vo.getCountryId().equals(sysAreaBusinessWaterLevel.getAreaCode())) {
                    areaSet.add(sysAreaBusinessWaterLevel);
                }
            }
        }
        chanList.removeAll(removeArealist);
        chanList.addAll(areaSet);
        for (SysAreaBusinessWaterLevel vo : chanList) {
            tree.add(new TreeSelect(vo.getAreaCode(), vo.getAreaName(), vo.getAreaParentCode(), vo.getAreaLevel()));
        }
        List<TreeSelect> treeSelectList = TreeUtils.listToTreeSelect(tree);
        TreeSelect treeSelect = new TreeSelect();
        if(!CollectionUtils.isEmpty(treeSelectList)) {
            String name = DeviceConstants.TOWER_ROOT_AREA_NAME;
            SysArea sysArea = deviceInfoMapper.getAreaRootInfo();
            if(!ObjectUtils.isEmpty(sysArea)){
                name = sysArea.getAreaName();
            }
            treeSelect.setId("1");
            treeSelect.setLabel(name);
            treeSelect.setPid("1");
            treeSelect.setChildren(treeSelectList);
        }
        List<TreeSelect> returnList = new ArrayList<>();
        if(StringUtils.isNotBlank(treeSelect.getId())) {
            returnList.add(treeSelect);
        }
        return returnList;
    }
}
