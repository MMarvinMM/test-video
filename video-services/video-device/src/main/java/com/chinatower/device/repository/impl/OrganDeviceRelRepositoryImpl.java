package com.chinatower.device.repository.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chinatower.device.dto.StreetDeptDTO;
import com.chinatower.platform.constants.DeviceConstants;
import com.chinatower.platform.entity.device.DelDeviceRelaDTO;
import com.chinatower.device.dataobject.OrganDeviceRelDO;
import com.chinatower.device.mapper.OrganDeviceRelMapper;
import com.chinatower.device.repository.IOrganDeviceRelRepository;
import com.chinatower.system.api.model.SysDeptStreetRelVO;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;


/**
 * <p>
 * 设备组织关系服务实现类
 * </p>
 *
 * @author chenmf
 * @since 2022-01-19
 */
@Slf4j
@Service
public class OrganDeviceRelRepositoryImpl extends ServiceImpl<OrganDeviceRelMapper, OrganDeviceRelDO> implements IOrganDeviceRelRepository {
    
    @Autowired
    private OrganDeviceRelMapper organDeviceRelMapper;
    
    /**
     * 批量保存设备组织关系
     *
     * @return
     * @author chenmf
     * @date 2022/2/19
     * @version
     */
    @Override
    public void batchSaveDeviceRela(List<OrganDeviceRelDO> organDeviceRelList) {
        this.saveBatch(organDeviceRelList);
    }
    
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
    
        QueryWrapper<OrganDeviceRelDO> queryWrapper = new QueryWrapper<>();
    
        queryWrapper.eq("tenant_id", delDeviceRelaDTO.getTenantId());
        queryWrapper.eq("delete_flag", DeviceConstants.DELETE_FLAG_NORMAL);
//        queryWrapper.eq("category_code", delDeviceRelaDTO.getCategoryCode());
        List<String> deviceCodeList = delDeviceRelaDTO.getDeviceList();
        if (!CollectionUtils.isEmpty(deviceCodeList)) {
            queryWrapper.in("device_code", deviceCodeList);
        }
    
        this.remove(queryWrapper);
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
    
        return organDeviceRelMapper.getDeptNoDeviceCode(delDeviceRelaDTO);
    }
    
    /**
     * 查询组织下挂设备的区县关系
     *
     * @return
     * @author chenmf
     * @date 2022/5/18
     * @version
     */
    @Override
    public List<StreetDeptDTO> getDeptRel(String tenantId, String industryCode) {
        
        return organDeviceRelMapper.getDeptRel(tenantId, industryCode);
    }


    /**
     * 新增组织行政关系数据
     * @param: list
     * @return void
     * @author chenmf
     * @date 2022/6/13
     * @version
     */
    @Override
    public void insertDeptStreetRel(List<SysDeptStreetRelVO> list) {
        organDeviceRelMapper.insertBatchDeptStreetRelNew(list);
    }

    
    /**
     * 查询目前组织行政关系表
     *
     * @return
     * @author chenmf
     * @date 2022/5/18
     * @version
     */
    @Override
    public List<StreetDeptDTO> getStreetDeptRel(String tenantId) {
        return organDeviceRelMapper.getStreetDeptRel(tenantId);
    }
    
    /**
     * 删除组织行政关系数据
     * @param: sysDeptStreetRelVO
     * @return void
     * @author chenmf
     * @date 2022/6/13
     * @version
     */
    @Override
    public void deleteDeptStreetRel(SysDeptStreetRelVO sysDeptStreetRelVO) {
        organDeviceRelMapper.deleteDeptStreetRel(sysDeptStreetRelVO);
    }
}
