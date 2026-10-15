package com.chinatower.event.repository;

import com.chinatower.common.entity.Result;
import com.chinatower.event.dataobject.LinkUserDeal;
import com.chinatower.event.dto.LinkUserDealModel;

import java.util.List;

/**
 * @author chmod
 * @version 1.0
 * @date 2022/1/26 14:48
 */
public interface ILinkUserDealService {
    /**
     * 添加流程环节处理用户信息
     * @param model
     * @return
     */
    Result<LinkUserDealModel> addLinkUserDeal(LinkUserDealModel model);

    /**
     * 根据条件查询
     * @param model
     * @return
     */
    Result<List<LinkUserDeal>> queryLinkUserDeal(LinkUserDealModel model);

    /**
     * 修改流程环节处理状态
     * @param model
     * @return
     */
    Result<String> modifyLinkUserDeal(LinkUserDealModel model);
}
