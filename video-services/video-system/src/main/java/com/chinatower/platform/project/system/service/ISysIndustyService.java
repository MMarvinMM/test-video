package com.chinatower.platform.project.system.service;

import com.chinatower.platform.entity.admin.SysIndustryConf;
import com.chinatower.platform.entity.even.SysIndustryViewRel;
import com.chinatower.platform.project.system.domain.vo.IndustryQueryVO;
import com.chinatower.platform.project.system.domain.vo.IndustrySelectVO;
import com.chinatower.system.api.model.IndustryConfDto;
import com.chinatower.system.api.model.IndustryTypeDto;

import java.util.List;


public interface ISysIndustyService
{

    List<SysIndustryConf> selectIndustryConf();

    List<SysIndustryViewRel> selectIndustryViewRel(String industryCode);

    List<SysIndustryViewRel> selectAllIndustryViewRel();

    List<IndustrySelectVO> selectIndustryByUserId(String userId);

    List<IndustryConfDto> allByUser();

    List<IndustryTypeDto> allByUserPc();

    IndustryConfDto detailByIndustryCode(String industryCode);

    List<IndustrySelectVO> queryIndustryByKeyWord(IndustryQueryVO industryQueryVO);

    List<IndustrySelectVO> queryAppVerCodeByKeyWord(IndustryQueryVO industryQueryVO);

    List<IndustrySelectVO> initIndustryByKeyWord(IndustryQueryVO industryQueryVO);

    Integer allByUserCount();

}
