package com.chinatower.manage.mapper;

import com.chinatower.manage.dataobject.SysIndustryView;
import com.chinatower.manage.dto.SysIndustryViewModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author chmod
 * @version 1.0
 * @date 2022/1/13 17:59
 */
@Mapper
public interface SysIndustryViewMapper {
    /**
     * 根据条件查询
     * @param model
     * @return
     */
    List<SysIndustryView> querySysIndustryView(SysIndustryViewModel model);

    List<SysIndustryView> querySysIndustryViewConf(@Param("viewIdList") List<String> viewIdList,
                                                   @Param("industryCode") String industryCode,
                                                   @Param("appVerCode") String appVerCode);

    List<SysIndustryView> querySysIndustryScreenConf(@Param("viewIdList") List<String> viewIdList,
                                                     @Param("industryCode") String industryCode,
                                                     @Param("appVerCode") String appVerCode);

    /**
     * 根据大屏ID查询组件ID列表
     * @param industryViewId
     * @return
     */
    List<String> queryCompoIds(@Param("industryViewId") String industryViewId);

    /**
     * 根据大行业信息查询应用大屏信息列表
     * @param industryCode 行业编码
     * @param appVerCode 行业版本编码
     * @return 大屏信息列表
     */
    List<SysIndustryView> queryIndustryScreenView(@Param("industryCode") String industryCode,
                                                  @Param("appVerCode") String appVerCode,
                                                  @Param("acceptNode") String acceptNode);

    /**
     * 根据行业信息查询新应用大屏信息列表
     * @param industryCode 行业编码
     * @param appVerCode 行业版本编码
     * @return 大屏信息列表
     */
    List<SysIndustryView> queryNewIndustryScreenView(@Param("industryCode") String industryCode,
                                                  @Param("appVerCode") String appVerCode,
                                                     @Param("acceptNode") String acceptNode);

    /**
     * 根据大屏信息查询第三方大屏列表
     * @param tenantId 客户编码
     * @param industryCode 行业编码
     * @param appVerCode 行业版本编码
     * @param industryViewIdList 第三方大屏编码列表
     * @return 第三方大屏列表
     */
    List<SysIndustryView> queryThirdViewInfoList(@Param("tenantId") String tenantId,
                                                 @Param("industryCode") String industryCode,
                                                 @Param("appVerCode") String appVerCode,
                                                 @Param("industryViewIdList") List<String> industryViewIdList);
}
