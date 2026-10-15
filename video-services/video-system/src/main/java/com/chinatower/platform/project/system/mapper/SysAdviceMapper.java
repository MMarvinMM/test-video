package com.chinatower.platform.project.system.mapper;

import com.chinatower.common.core.domain.SysAdvice;
import com.chinatower.platform.project.system.domain.vo.DeptInfoVO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface SysAdviceMapper {

    int insertAdvice(SysAdvice sysAdvice);

    List<String> selectOrderCustomerOrgList(String tenantId);

    List<DeptInfoVO> selectDeptInfo(DeptInfoVO deptInfoVO);
}
