package com.chinatower.device.service.impl;

import com.chinatower.device.dto.StreetDeptDTO;
import com.chinatower.device.service.IOrganDeviceRelInfoService;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import com.chinatower.system.api.model.SysDeptStreetRelVO;
import org.springframework.beans.BeanUtils;
import com.chinatower.platform.entity.devicemgmt.vo.OrganDeviceRelVO;
import com.chinatower.platform.entity.device.DelDeviceRelaDTO;
import com.chinatower.device.dataobject.OrganDeviceRelDO;
import com.chinatower.device.repository.IOrganDeviceRelRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName OrganDeviceRelService
 * @Deacription 设备组织关系服务
 * @Author chenmf
 * @Date 2022/2/19 15:27
 * @Version 1.0
 **/
@Service
@Slf4j
public class OrganDeviceRelInfoService implements IOrganDeviceRelInfoService {

    @Autowired
    private IOrganDeviceRelRepository organDeviceRelRepository;

    @Autowired
    private SystemFeignClient systemFeignClient;

    /**
     * 批量删除设备组织关系
     *
     * @return
     * @author chenmf
     * @date 2022/2/19
     * @version
     */
    @Override
    public void batchDelDeviceRela(DelDeviceRelaDTO delDeviceRelaDTO) {
        organDeviceRelRepository.batchDelDeviceRela(delDeviceRelaDTO);
    }

    /**
     * 批量保存设备组织关系
     *
     * @return
     * @author chenmf
     * @date 2022/2/19
     * @version
     */
    @Override
    public void batchSaveDeviceRela(List<OrganDeviceRelVO> organDeviceRelList) {
        List<OrganDeviceRelDO> organDeviceRelDOList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(organDeviceRelList)) {
            for (OrganDeviceRelVO organDeviceRelVO : organDeviceRelList) {
                OrganDeviceRelDO deviceRelDO = new OrganDeviceRelDO();
                BeanUtils.copyProperties(organDeviceRelVO, deviceRelDO);
                organDeviceRelDOList.add(deviceRelDO);
            }
        }
        organDeviceRelRepository.batchSaveDeviceRela(organDeviceRelDOList);
    }

    /**
     * 查询组织下没有设备的列表
     *
     * @return
     * @author chenmf
     * @date 2022/5/17
     * @version
     */
    @Override
    public List<String> getDeptNoDeviceCode(DelDeviceRelaDTO delDeviceRelaDTO) {
        return organDeviceRelRepository.getDeptNoDeviceCode(delDeviceRelaDTO);
    }

    /**
     * 更新组织行政区域表（先删后增）
     *
     * @return
     * @author chenmf
     * @date 2022/5/29
     * @version
     */
    @Override
    public void updateDeptStreetRel(String tenantId) {
        String industryCode = SecurityUtils.getLoginUser().getUser().getIndustryCode();

        //删除租户对应的组织行政关系表
        SysDeptStreetRelVO detletDeptStreetRelVO = new SysDeptStreetRelVO();
        detletDeptStreetRelVO.setIndustryCode(industryCode);
        detletDeptStreetRelVO.setTenantId(tenantId);
        organDeviceRelRepository.deleteDeptStreetRel(detletDeptStreetRelVO);

        //查询组织下挂设备的区县关系
        List<StreetDeptDTO> deptList = organDeviceRelRepository.getDeptRel(tenantId, industryCode);
        if (!CollectionUtils.isEmpty(deptList)) {
            List<SysDeptStreetRelVO> list = new ArrayList<>();
            for (StreetDeptDTO streetDeptDTO : deptList) {
                SysDeptStreetRelVO sysDeptStreetRelVO = new SysDeptStreetRelVO();
                sysDeptStreetRelVO.setDeptId(streetDeptDTO.getDeptId());
                sysDeptStreetRelVO.setCityCode(streetDeptDTO.getCityId());
                sysDeptStreetRelVO.setStreetAreaCode(streetDeptDTO.getTownCode());
                sysDeptStreetRelVO.setCountyCode(streetDeptDTO.getCountyId());
                sysDeptStreetRelVO.setProvinceCode(streetDeptDTO.getProvinceId());
                sysDeptStreetRelVO.setTenantId(tenantId);
                sysDeptStreetRelVO.setIndustryCode(industryCode);
                sysDeptStreetRelVO.setAppVerCode(SecurityUtils.getLoginUser().getUser().getAppVerCode());
                list.add(sysDeptStreetRelVO);
            }

            organDeviceRelRepository.insertDeptStreetRel(list);
        }
    }
}
