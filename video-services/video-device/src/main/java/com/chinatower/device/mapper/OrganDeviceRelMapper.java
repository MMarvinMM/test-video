package com.chinatower.device.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chinatower.device.dataobject.OrganDeviceRelDO;
import com.chinatower.device.dto.StreetDeptDTO;
import com.chinatower.platform.entity.device.DelDeviceRelaDTO;
import com.chinatower.system.api.model.SysDeptStreetRelVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 功能描述: 自定义的 统计数据 DAO层
 *
 * @ClassName OrganDeviceRelMapper
 * @auther: chenmf
 * @date: 2022/2/16
 * @version: 1.0
 */
@Mapper
public interface OrganDeviceRelMapper extends BaseMapper<OrganDeviceRelDO> {
    
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
    List<StreetDeptDTO> getDeptRel(@Param("tenantId")String tenantId, @Param("industryCode") String industryCode);


    /**
     * 新增组织和行政区域关联关系
     *
     * @return int
     * @param: deptStreetRel
     * @author chenmf
     * @date 2022/6/13
     * @version
     */
    int insertBatchDeptStreetRelNew(List<SysDeptStreetRelVO> list);

    /**
     * 查询目前组织行政关系表
     *
     * @return
     * @author chenmf
     * @date 2022/5/18
     * @version
     */
    List<StreetDeptDTO> getStreetDeptRel(@Param("tenantId")String tenantId);
    
    /**
     * 删除组织和行政区域关联关系
     * @param: deptStreetRel
     * @return int
     * @author chenmf
     * @date 2022/6/13
     * @version
     */
    int deleteDeptStreetRel(SysDeptStreetRelVO deptStreetRel);
}
