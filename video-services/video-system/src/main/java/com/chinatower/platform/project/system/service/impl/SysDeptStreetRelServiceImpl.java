package com.chinatower.platform.project.system.service.impl;

import com.chinatower.common.core.constant.CommonErrorEnum;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.entity.Result;
import com.chinatower.platform.project.system.mapper.SysDeptStreetRelMapper;
import com.chinatower.platform.project.system.service.ISysDeptStreetRelService;
import com.chinatower.system.api.model.SysDeptStreetRelVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class SysDeptStreetRelServiceImpl implements ISysDeptStreetRelService {
    @Resource
    private SysDeptStreetRelMapper sysDeptStreetRelMapper;

    /**
     * 组织和行政区域关联关系查询
     *
     * @param vo
     * @return
     */
    @Override
    public Result<List<SysDeptStreetRelVO>> selectDeptStreetRelList(SysDeptStreetRelVO vo) {
        if(vo == null || StringUtils.isEmpty(vo.getDeptId())){
            return Result.fail(CommonErrorEnum.PARAM_FORMAT_ERROR.getErrorMsg());
        }
        Result result = null;
        List<SysDeptStreetRelVO> list = sysDeptStreetRelMapper.selectDeptStreetRelList(vo);
        if (CollectionUtils.isNotEmpty(list)) {
            result = Result.success("查询成功");
            result.setData(list);
        }else {
            result = Result.success("查询成功");
            result.setData(new ArrayList<SysDeptStreetRelVO>());
        }
        return result;
    }

    /**
     * 新增组织和行政区域关联关系
     *
     * @param list
     * @return
     */
    @Override
    public Result insertDeptStreetRel(List<SysDeptStreetRelVO> list) {
        for(SysDeptStreetRelVO vo:list){

            if(vo == null || StringUtils.isEmpty(vo.getDeptId())){
                return Result.fail(CommonErrorEnum.PARAM_FORMAT_ERROR.getErrorMsg());
            }
        }

        return Result.condition(sysDeptStreetRelMapper.insertBatchDeptStreetRelNew(list) > 0);
    }
    /**
     * 删除组织和行政区域关联关系
     *
     * @param vo
     * @return 结果
     */
    @Override
    public Result deleteDeptStreetRel(SysDeptStreetRelVO vo) {
        if(vo == null || StringUtils.isEmpty(vo.getDeptId())){
            return Result.fail(CommonErrorEnum.PARAM_FORMAT_ERROR.getErrorMsg());
        }

        return Result.condition(sysDeptStreetRelMapper.deleteDeptStreetRel(vo) > 0);

    }



}
