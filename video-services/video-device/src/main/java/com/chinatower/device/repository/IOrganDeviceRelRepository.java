package com.chinatower.device.repository;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chinatower.device.dto.StreetDeptDTO;
import com.chinatower.platform.entity.device.DelDeviceRelaDTO;
import com.chinatower.device.dataobject.OrganDeviceRelDO;
import com.chinatower.system.api.model.SysDeptStreetRelVO;

import java.util.List;


/**
 * <p>
 * 设备组织关系服务类
 * </p>
 *
 * @author chenmf
 * @since 2022-01-19
 */
public interface IOrganDeviceRelRepository extends IService<OrganDeviceRelDO> {
    
    
    /**
     * 批量保存设备组织关系
     *
     * @return
     * @author chenmf
     * @date 2022/2/19
     * @version
     */
    void batchSaveDeviceRela(List<OrganDeviceRelDO> organDeviceRelList);
    
    /**
     * 批量删除设备组织关系
     *
     * @return
     * @author chenmf
     * @date 2022/2/19
     * @version
     */
    void batchDelDeviceRela(DelDeviceRelaDTO delDeviceRelaDTO);
    
    /**
     * 查询组织下没有设备的列表
     *
     * @return
     * @author chenmf
     * @date 2022/5/17
     * @version
     */
    List<String> getDeptNoDeviceCode(DelDeviceRelaDTO delDeviceRelaDTO);
    
    /**
     * 查询组织下挂设备的区县关系
     *
     * @return
     * @author chenmf
     * @date 2022/5/18
     * @version
     */
    List<StreetDeptDTO> getDeptRel(String tenantId, String industryCode);

    /**
     * 新增组织行政关系数据
     * @param: list
     * @return void
     * @author chenmf
     * @date 2022/6/13
     * @version
     */
    void insertDeptStreetRel(List<SysDeptStreetRelVO> list);

    /**
     * 查询目前组织行政关系表
     *
     * @return
     * @author chenmf
     * @date 2022/5/18
     * @version
     */
    List<StreetDeptDTO> getStreetDeptRel(String tenantId);
    
    /**
     * 删除组织行政关系数据
     * @param: sysDeptStreetRelVO
     * @return void
     * @author chenmf
     * @date 2022/6/13
     * @version
     */
    void deleteDeptStreetRel(SysDeptStreetRelVO sysDeptStreetRelVO);
}
