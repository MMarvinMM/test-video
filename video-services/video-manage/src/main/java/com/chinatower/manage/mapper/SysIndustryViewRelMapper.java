package com.chinatower.manage.mapper;

import com.chinatower.manage.api.dto.SysIndustryConfigInfoModel;
import com.chinatower.manage.dto.IndustryViewBannerInfoDTO;
import com.chinatower.manage.dto.SysIndustryViewRelModel;
import com.chinatower.manage.dto.SysThirdPartyScreenConfigModel;
import com.chinatower.platform.entity.admin.SysIndustryVerConfDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 行业应用配置Mapper
 *
 * @author chmod
 * @version 1.0
 * @date 2022/2/9 15:12
 */
@Mapper
public interface SysIndustryViewRelMapper {

    /**
     * 根据条件查询
     *
     * @param model
     * @return
     */
    List<SysIndustryViewRelModel> querySysIndustryViewRel(SysIndustryViewRelModel model);

    /**
     * 根据组件id获取组件
     *
     * @param idList 组件id
     * @return 组件信息
     */
    List<IndustryViewBannerInfoDTO> queryCompoByIdList(@Param("list") List<Long> idList);

    /**
     * 根据行业信息查询行业大屏信息
     *
     * @param param 参数
     * @return 行业大屏信息
     */
    List<SysIndustryConfigInfoModel> queryByIndustryInfo(@Param("param") SysIndustryConfigInfoModel param);

    /**
     * 根据行业信息查询行业大屏信息
     *
     * @param param 参数
     * @return 行业大屏信息
     */
    List<SysIndustryConfigInfoModel> queryByNewIndustryInfo(@Param("param") SysIndustryConfigInfoModel param);

    /**
     * 根据行业信息查询第三方大屏信息
     *
     * @param param 参数
     * @return 第三方大屏信息
     */
    List<SysIndustryConfigInfoModel> queryThirdIndustryConfigByIndustryInfo(@Param("param") SysIndustryConfigInfoModel param);

    SysThirdPartyScreenConfigModel selectThirdIndustryConfigById(@Param("industryViewId")Long industryViewId);

    SysThirdPartyScreenConfigModel queryDigitalTwinScreenConfigInfo(@Param("tenantId")String tenantId, @Param("industryCode")String industryCode);

    /**
     * 查询主题信息
     * @return
     */
    SysIndustryConfigInfoModel queryByIndustryCode(SysIndustryConfigInfoModel param);

    /**
     * 查询banner配置信息
     * @param industryCode
     * @param appVerCode
     * @param platformCode
     * @return
     */
    SysIndustryVerConfDTO findBannerInfo(@Param("industryCode") String industryCode, @Param("appVerCode") String appVerCode, @Param("platformCode") String platformCode);
}
