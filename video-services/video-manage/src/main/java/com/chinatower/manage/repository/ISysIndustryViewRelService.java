package com.chinatower.manage.repository;

import com.chinatower.common.entity.Result;
import com.chinatower.manage.api.dto.SysIndustryConfigInfoModel;
import com.chinatower.manage.dto.SysIndustryViewRelModel;
import com.chinatower.manage.dto.SysThirdPartyScreenConfigModel;
import com.chinatower.platform.entity.admin.SysIndustryVerConfDTO;

import java.util.List;

/**
 * 行业大屏关联服务接口
 * @author chmod
 * @version 1.0
 * @date 2022/2/9 18:40
 */
public interface ISysIndustryViewRelService {
    /**
     * 根据条件查询
     * @param model
     * @return
     */
    Result<Object> querySysIndustryViewRel(SysIndustryViewRelModel model);

    /**
     * 查询大屏配置信息【包含行业大屏和第三方大屏】
     * @return 大屏配置信息
     */
    Result<List<SysIndustryConfigInfoModel>> queryIndustryConfigInfo(SysIndustryConfigInfoModel param);

    /**
     * 查询数字孪生大屏配置信息
     * @return 数字孪生大屏信息
     */
    Result<SysThirdPartyScreenConfigModel> queryDigitalTwinScreenConfigInfo();

    /**
     * 获取第三方大屏header 封装验签
     * @param model
     * @return
     */
    Result getThirdPartyUrlWithAuthorization(SysIndustryViewRelModel model);

    /**
     * 查询行业大屏主题类型
     * @param model
     * @return
     */
    SysIndustryConfigInfoModel queryByIndustryCode(SysIndustryConfigInfoModel model);

    /**
     * 查询大屏banner配置
     * @return
     */
    SysIndustryVerConfDTO queryIndustryVerConf();
}
