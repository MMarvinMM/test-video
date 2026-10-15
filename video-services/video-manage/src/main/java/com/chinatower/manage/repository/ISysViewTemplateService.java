package com.chinatower.manage.repository;

import com.chinatower.common.entity.Result;
import com.chinatower.manage.dataobject.SysIndustryView;
import com.chinatower.manage.dto.SysIndustryViewModel;

import java.util.List;

/**
 * @author chmod
 * @version 1.0
 * @date 2022/1/14 10:44
 */
public interface ISysViewTemplateService {
    /**
     * 根据条件查询
     * @param model
     * @return
     */
    Result<Object> queryViewTemplate(SysIndustryViewModel model);

    /**
     * 查询客户权限下的所有大屏数据
     * @return 大屏数据
     */
    Result<List<SysIndustryView>> queryAllViewTemplate();
}
