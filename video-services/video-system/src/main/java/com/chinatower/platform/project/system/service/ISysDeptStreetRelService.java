package com.chinatower.platform.project.system.service;

import com.chinatower.common.entity.Result;
import com.chinatower.system.api.model.SysDeptStreetRelVO;

import java.util.List;

public interface ISysDeptStreetRelService {

    /**
     * 组织和行政区域关联关系查询
     * @param vo
     * @return
     */
    Result<List<SysDeptStreetRelVO>> selectDeptStreetRelList(SysDeptStreetRelVO vo);
    /**
     * 新增组织和行政区域关联关系
     * @param list
     * @return
     */
    Result insertDeptStreetRel(List<SysDeptStreetRelVO> list);

    /**
     * 删除组织和行政区域关联关系
     *
     * @param vo
     * @return 结果
     */
    public Result deleteDeptStreetRel(SysDeptStreetRelVO vo);


}
