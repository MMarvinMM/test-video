package com.chinatower.order.repository.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chinatower.order.domain.OrganDeviceRelDO;
import com.chinatower.order.dto.DeviceIndustryDTO;
import com.chinatower.order.dto.StreetDeptDTO;
import com.chinatower.order.mapper.OrganDeviceRelMapper;
import com.chinatower.order.repository.IOrganDeviceRelRepository;
import com.chinatower.platform.constants.DeviceConstants;
import com.chinatower.platform.entity.admin.SysDept;
import com.chinatower.platform.entity.device.DelDeviceRelaDTO;
import com.chinatower.platform.entity.order.OrganStreetRelVO;
import com.chinatower.system.api.model.SysDeptStreetRelVO;
import lombok.extern.slf4j.Slf4j;
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
        this.saveBatch(organDeviceRelList, 5000);
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
        queryWrapper.eq("device_type", delDeviceRelaDTO.getDeviceType());
        queryWrapper.eq("industry_code", delDeviceRelaDTO.getIndustryCode());
        List<String> deviceCodeList = delDeviceRelaDTO.getDeviceList();
        if (!CollectionUtils.isEmpty(deviceCodeList)) {
            queryWrapper.in("device_code", deviceCodeList);
        }
        this.remove(queryWrapper);
    }

    @Override
    public List<OrganDeviceRelDO> selectOrganDeviceInfoByCondition(DelDeviceRelaDTO param) {
        return organDeviceRelMapper.selectOrganDeviceInfoByCondition(param.getTenantId(), param.getDeviceType(),
                param.getIndustryCodeList(), param.getDeviceList());
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
    public List<StreetDeptDTO> getDeptRel(String tenantId,String industryCode) {

        return organDeviceRelMapper.getDeptRel(tenantId,industryCode);
    }

    @Override
    public List<StreetDeptDTO> getDeptRelBatch(String tenantId, List<String> industryCodeList) {

        return organDeviceRelMapper.getDeptRelBatch(tenantId, industryCodeList);
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

    @Override
    public void deleteDeptStreetRelBatch(String tenantId, List<String> industryCodeList) {
        organDeviceRelMapper.deleteDeptStreetRelBatch(tenantId, industryCodeList);
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
     * 新增组织行政权限数据
     * @param: list
     * @return void
     * @author chenmf
     * @date 2022/6/13
     * @version
     */
    @Override
    public void batchSaveStreetRela(List<OrganStreetRelVO> list) {
        organDeviceRelMapper.insertBatchOrganStreetRel(list);
    }

    /**
     * 批量删除组织区域关系
     */
    @Override
    public void batchDelStreetRela(String tenantId, int categoryCode,String industryCode,
                                   String appVerCode, List<String> areaCodeList) {
        organDeviceRelMapper.batchDelStreetRela(tenantId,categoryCode,industryCode,appVerCode,areaCodeList);
    }

    @Override
    public List<OrganStreetRelVO> selectOrganStreetInfoByCondition(String tenantId, int categoryCode,
                                                                   List<String> industryCodeList,
                                                                   List<String> areaCodeList) {
        return organDeviceRelMapper.selectOrganStreetInfoByCondition(tenantId, categoryCode, industryCodeList, areaCodeList);
    }

    @Override
    public void batchDelStreetRelaByIds(List<Long> ids) {
        organDeviceRelMapper.batchDelStreetRelaByIds(ids);
    }

    @Override
    public List<SysDept> selectTenantDeptId(String customerCode, String industryCode) {
        return organDeviceRelMapper.selectTenantDeptId(customerCode,industryCode);
    }

    @Override
    public List<SysDept> selectTenantRootDeptId(String customerCode, List<String> industryCodeList) {
        return organDeviceRelMapper.selectTenantRootDeptId(customerCode, industryCodeList);
    }

    @Override
    public List<DeviceIndustryDTO> selectExistDeviceCodeList(String tenantId, List<String> industryCodeList, List<String> deviceCodeList, List<Integer> deviceTypeList) {
        return organDeviceRelMapper.selectExistDeviceCodeList(tenantId, industryCodeList, deviceCodeList, deviceTypeList);
    }

    @Override
    public List<String> selectExistServiceCountyCodeList(String tenantId, String industryCode, List<String> areaCodeList, int categoryCode) {
        return organDeviceRelMapper.selectExistServiceCountyCodeList(tenantId, industryCode, areaCodeList, categoryCode);
    }
}
