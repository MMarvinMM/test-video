package com.chinatower.platform.modules.dept.dao;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.chinatower.system.api.model.SysDeptStreetRelVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 组织管理区域定时
 */
@Mapper
public interface DeptDAO {


    /**
     * 查询组织设备关联信息
     * @return
     */
    List<SysDeptStreetRelVO> getOrganDeviceRelAll();

    /**
     * 清空组织管理区域表
     *
     * @return 结果
     */
    @InterceptorIgnore(blockAttack = "true")
    public int deleteDeptStreetRelAll();

    /**
     * 批量新增组织部门关联信息
     * @param list
     */
    void insertBatchDeptStreetRelTenantAll(List<SysDeptStreetRelVO> list);

}
