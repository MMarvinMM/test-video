package com.chinatower.device.service;

import com.chinatower.platform.entity.device.DelDeviceRelaDTO;
import com.chinatower.platform.entity.devicemgmt.vo.OrganDeviceRelVO;

import java.util.List;

/**
 * 设备组织关系服务
 *
 * @return
 * @author chenmf
 * @date 2022/2/19
 * @version
 */
public interface IOrganDeviceRelInfoService {
    
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
     * 批量保存设备组织关系
     *
     * @return
     * @author chenmf
     * @date 2022/2/19
     * @version
     */
    void batchSaveDeviceRela(List<OrganDeviceRelVO> organDeviceRelList);
    
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
     * 更新组织行政区域表（先删后增）
     *
     * @return
     * @author chenmf
     * @date 2022/5/29
     * @version
     */
    void updateDeptStreetRel(String tenantId);
    
}
