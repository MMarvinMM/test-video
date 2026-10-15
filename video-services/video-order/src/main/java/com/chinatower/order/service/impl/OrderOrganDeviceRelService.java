package com.chinatower.order.service.impl;

import com.chinatower.order.domain.OrganDeviceRelDO;
import com.chinatower.order.dto.DeviceIndustryDTO;
import com.chinatower.order.dto.StreetDeptDTO;
import com.chinatower.order.repository.IOrganDeviceRelRepository;
import com.chinatower.order.service.IOrderOrganDeviceRelService;
import com.chinatower.platform.entity.admin.SysDept;
import com.chinatower.platform.entity.device.DelDeviceRelaDTO;
import com.chinatower.platform.entity.devicemgmt.vo.OrganDeviceRelVO;
import com.chinatower.platform.entity.order.OrganStreetRelVO;
import com.chinatower.system.api.model.SysDeptStreetRelVO;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName IOrderOrganDeviceRelService
 * @Deacription 设备组织服务类
 * （为保证订购事务一致性，相关接口在order工程重写一份，后续逻辑有改动需要同步修改）
 * @Author chenmf
 * @Date 2022/6/10 16:17
 * @Version 1.0
 **/
@Slf4j
@Service
public class OrderOrganDeviceRelService implements IOrderOrganDeviceRelService {

    @Autowired
    private IOrganDeviceRelRepository organDeviceRelRepository;
    private static final String DEPT_TREE_ROOT_ID = "000000";
    private static final String DEPT_TREE_ROOT_ID_CHAR = "无";
    /**
     * 更新组织行政区域表（先删后增）
     * @param: deviceOrgRelByTenantIdDTO
     * @return void
     * @author chenmf
     * @date 2022/6/10
     * @version
     */
    @Override
    public void updateDeptStreetRel(String tenantId, String industryCode, String appVerCode) {

        //删除租户对应的组织行政关系表
        SysDeptStreetRelVO deptStreetRelVO = new SysDeptStreetRelVO();
        deptStreetRelVO.setTenantId(tenantId);
        deptStreetRelVO.setIndustryCode(industryCode);
        organDeviceRelRepository.deleteDeptStreetRel(deptStreetRelVO);

        //查询组织下挂设备的区县关系
        List<StreetDeptDTO> deptList = organDeviceRelRepository.getDeptRel(tenantId, industryCode);
        if (!CollectionUtils.isEmpty(deptList)) {
            List<SysDeptStreetRelVO> list = new ArrayList<>();
            for (StreetDeptDTO streetDeptDTO : deptList) {
                SysDeptStreetRelVO sysDeptStreetRelVO = new SysDeptStreetRelVO();
                sysDeptStreetRelVO.setDeptId(streetDeptDTO.getDeptId());
                sysDeptStreetRelVO.setCityCode(streetDeptDTO.getCityId());
                sysDeptStreetRelVO.setCountyCode(streetDeptDTO.getCountyId());
                sysDeptStreetRelVO.setProvinceCode(streetDeptDTO.getProvinceId());
                sysDeptStreetRelVO.setStreetAreaCode(streetDeptDTO.getTownCode());
                sysDeptStreetRelVO.setTenantId(tenantId);
                sysDeptStreetRelVO.setIndustryCode(industryCode);
                sysDeptStreetRelVO.setAppVerCode(appVerCode);
                list.add(sysDeptStreetRelVO);
            }
            organDeviceRelRepository.insertDeptStreetRel(list);
        }
    }

    @Override
    public void updateDeptStreetRelBatch(String tenantId, Map<String, String> industryMap) {

        List<String> industryCodeList = industryMap.keySet().stream().distinct().collect(Collectors.toList());

        //删除租户对应的组织行政关系表
        organDeviceRelRepository.deleteDeptStreetRelBatch(tenantId, industryCodeList);

        //查询组织下挂设备的区县关系
        List<StreetDeptDTO> deptList = organDeviceRelRepository.getDeptRelBatch(tenantId, industryCodeList);
        if (!CollectionUtils.isEmpty(deptList)) {
            String industryCode;
            List<SysDeptStreetRelVO> list = new ArrayList<>();
            for (StreetDeptDTO streetDeptDTO : deptList) {
                SysDeptStreetRelVO sysDeptStreetRelVO = new SysDeptStreetRelVO();
                sysDeptStreetRelVO.setDeptId(streetDeptDTO.getDeptId());
                sysDeptStreetRelVO.setCityCode(streetDeptDTO.getCityId());
                sysDeptStreetRelVO.setCountyCode(streetDeptDTO.getCountyId());
                sysDeptStreetRelVO.setProvinceCode(streetDeptDTO.getProvinceId());
                sysDeptStreetRelVO.setStreetAreaCode(streetDeptDTO.getTownCode());
                sysDeptStreetRelVO.setTenantId(tenantId);
                industryCode = streetDeptDTO.getIndustryCode();
                sysDeptStreetRelVO.setIndustryCode(industryCode);
                sysDeptStreetRelVO.setAppVerCode(industryMap.get(industryCode));
                list.add(sysDeptStreetRelVO);
            }
            organDeviceRelRepository.insertDeptStreetRel(list);
        }
    }

    /**
     * 批量保存设备组织关系
     * @param: orgList
     * @return void
     * @author chenmf
     * @date 2022/6/10
     * @version
     */
    @Override
    public void batchSaveDeviceRela(List<OrganDeviceRelVO> organDeviceRelList) {
        if (!CollectionUtils.isEmpty(organDeviceRelList)) {
            List<OrganDeviceRelDO> organDeviceRelDOList = new ArrayList<>();
            for (OrganDeviceRelVO organDeviceRelVO : organDeviceRelList) {
                OrganDeviceRelDO deviceRelDO = new OrganDeviceRelDO();
                BeanUtils.copyProperties(organDeviceRelVO, deviceRelDO);
                organDeviceRelDOList.add(deviceRelDO);
            }
            organDeviceRelRepository.batchSaveDeviceRela(organDeviceRelDOList);
        }
    }


    /**
     * 批量删除组织设备关系表
     * @param: delDeviceRelaDTO
     * @return void
     * @author chenmf
     * @date 2022/6/10
     * @version
     */
    @Override
    public void batchDelDeviceRela(DelDeviceRelaDTO delDeviceRelaDTO) {
        organDeviceRelRepository.batchDelDeviceRela(delDeviceRelaDTO);
    }

    @Override
    public void batchDelDeviceRelaByIds(List<Long> ids) {
        organDeviceRelRepository.removeByIds(ids);
    }

    @Override
    public List<OrganDeviceRelDO> selectOrganDeviceInfoByCondition(DelDeviceRelaDTO param) {
        return organDeviceRelRepository.selectOrganDeviceInfoByCondition(param);
    }

    /**
     * 批量保存设备组织关系
     * @param: orgList
     * @return void
     * @author chenmf
     * @date 2022/6/10
     * @version
     */
    @Override
    public void batchSaveStreetRela(List<OrganStreetRelVO> organStreetRelVOList) {
        organDeviceRelRepository.batchSaveStreetRela(organStreetRelVOList);
    }

    /**
     * 批量删除组织区域关系
     */
    @Override
    public void batchDelStreetRela(String tenantId, int categoryCode, String industryCode,
                                   String appVerCode, List<String> areaCodeList) {
        organDeviceRelRepository.batchDelStreetRela(tenantId,categoryCode,industryCode,appVerCode,areaCodeList);
    }

    @Override
    public List<OrganStreetRelVO> selectOrganStreetInfoByCondition(String tenantId, int categoryCode,
                                                            List<String> industryCodeList,
                                                            List<String> areaCodeList) {
        return organDeviceRelRepository.selectOrganStreetInfoByCondition(tenantId, categoryCode, industryCodeList, areaCodeList);
    }

    @Override
    public void batchDelStreetRelaByIds(List<Long> ids) {
        organDeviceRelRepository.batchDelStreetRelaByIds(ids);
    }

    @Override
    public List<SysDept> selectTenantDeptId(String customerCode, String industryCode) {
        List<SysDept> sysDepts = organDeviceRelRepository.selectTenantDeptId(customerCode,industryCode);
        return sysDepts.stream().filter(item ->
                StringUtils.equals(item.getParentId(), DEPT_TREE_ROOT_ID)
                        || StringUtils.equals(item.getParentId(), DEPT_TREE_ROOT_ID_CHAR) ).collect(Collectors.toList());
    }

    @Override
    public List<SysDept> selectTenantRootDeptId(String customerCode, List<String> industryCodeList) {
        return organDeviceRelRepository.selectTenantRootDeptId(customerCode, industryCodeList);
    }

    @Override
    public List<DeviceIndustryDTO> selectExistDeviceCodeList(String tenantId, List<String> industryCodeList, List<String> deviceCodeList, List<Integer> deviceTypeList) {
        if (StringUtils.isEmpty(tenantId) || CollectionUtils.isEmpty(industryCodeList)
                || org.apache.commons.collections4.CollectionUtils.isEmpty(deviceCodeList)) {
            return Lists.newArrayList();
        }
        return organDeviceRelRepository.selectExistDeviceCodeList(tenantId, industryCodeList, deviceCodeList, deviceTypeList);
    }

    @Override
    public List<String> selectExistServiceCountyCodeList(String tenantId, String industryCode, List<String> areaCodeList, int categoryCode) {
        if (StringUtils.isEmpty(tenantId) || StringUtils.isEmpty(industryCode)
                || org.apache.commons.collections4.CollectionUtils.isEmpty(areaCodeList)) {
            return Lists.newArrayList();
        }
        return organDeviceRelRepository.selectExistServiceCountyCodeList(tenantId, industryCode, areaCodeList, categoryCode);
    }
}
