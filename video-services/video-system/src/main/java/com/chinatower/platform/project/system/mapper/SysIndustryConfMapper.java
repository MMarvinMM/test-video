package com.chinatower.platform.project.system.mapper;

import com.chinatower.platform.entity.admin.SysIndustryConf;
import com.chinatower.platform.entity.even.SysIndustryViewRel;
import com.chinatower.platform.project.system.domain.vo.AssociateDemoAccountVO;
import com.chinatower.platform.project.system.domain.vo.IndustryQueryVO;
import com.chinatower.platform.project.system.domain.vo.IndustrySelectVO;
import com.chinatower.system.api.model.IndustryConfDto;
import com.chinatower.system.api.model.IndustryTypeDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 功能说明:
 *
 * @param:
 * @return:
 * @author: 王梦迪
 * @date: 2022/3/17 10:20
 * @requestID 需求单号及需求名称
 */
public interface SysIndustryConfMapper {


    List<SysIndustryConf> selectIndustryConf(@Param("acceptNode")String acceptNode);

    List<SysIndustryViewRel> selectIndustryViewRel(String industryCode);

    List<SysIndustryViewRel> selectAllIndustryViewRel();

    List<IndustrySelectVO> selectIndustryByUserId(@Param("userId") String userId,@Param("platformCode") String platformCode);

    List<IndustryConfDto> allByUser(@Param("username") String username,@Param("platformCode")String platformCode);

    List<IndustryTypeDto> allByUserPc(@Param("username") String username, @Param("platformCode")String platformCode);

    IndustryConfDto detailByIndustryCode(@Param("industryCode")String industryCode,@Param("platformCode")String platformCode);

    String findUsernameByTenantId(@Param("tenantId") String tenantId);

    List<IndustrySelectVO> queryIndustryByKeyWord(IndustryQueryVO industryQueryVO);

    List<IndustrySelectVO> queryAppVerCodeByKeyWord(IndustryQueryVO industryQueryVO);

    List<IndustrySelectVO> initIndustryByKeyWord(IndustryQueryVO industryQueryVO);

    Integer allByUserCount(@Param("username") String username, @Param("platformCode")String platformCode);

    List<AssociateDemoAccountVO> queryAssociateDemoAccountDeptRelList(AssociateDemoAccountVO associateDemoAccountVO);

    List<AssociateDemoAccountVO> queryAssociateDemoAccountUserRelList(AssociateDemoAccountVO associateDemoAccountVO);

    String getAssociateDemoAccountInfo(AssociateDemoAccountVO associateDemoAccountVO);

}
