package com.chinatower.event.repository;


import com.chinatower.event.dto.SysLinkViewUrlModel;

import java.util.List;

/**
 * 行业大屏关联服务接口
 * @author chmod
 * @version 1.0
 * @date 2022/2/9 18:40
 */

public interface ISysIndustryViewUrlService {

    List<SysLinkViewUrlModel> querySysLinkViewUrlAll();
}
