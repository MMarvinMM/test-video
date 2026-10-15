package com.chinatower.manage.api;

import com.chinatower.common.core.constant.ServiceNameConstants;
import com.chinatower.common.core.dto.IndustryReqModel;
import com.chinatower.common.entity.Result;
import com.chinatower.manage.api.dto.SysIndustryConfigInfoModel;
import com.chinatower.manage.api.factory.IndustryFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(contextId = "industryService", value = ServiceNameConstants.TTVIEW_MANAGE_SERVICE, fallbackFactory = IndustryFallbackFactory.class)
public interface IIndustryService {

    /**
     * 根据行业编码、版本号获取鉴权url
     */
    @PostMapping(value = "/industry/conf/getAuthUrlList")
    Result<List<String>> getAuthUrlList(@RequestBody IndustryReqModel model);


    /**
     * 查询大屏配置信息【包含行业大屏和第三方大屏】
     */
    @PostMapping(value = "/industry/config/queryIndustryConfigInfo")
    Result<List<SysIndustryConfigInfoModel>> queryIndustryConfigInfo(@RequestBody SysIndustryConfigInfoModel param);

    /**
     * 获取大屏配置数据
     * @apiNote 11.10 获取大屏主题类型
     * @apiVersion 11.10
     * @tag 十一、数据能力
     * @requestDemo
     * 请求参数解析：
     * industryViewId 大屏id
     * industryViewType 大屏类型 0-应用大屏 1-第三方大屏
     * {
     *     "industryViewId": 139,
     *     "industryViewType": 2
     * }
     * @responseDemo
     * 返回参数解析：
     * industryViewId 大屏id
     * industryViewName 大屏名称
     * topicType 大屏主题风格, wiseblue-睿智蓝 aquamarine-碧山绿 terracotta-赤土黄
     * {
     *     "code": 200,
     *     "msg": "请求成功",
     *     "time": 1741159999013,
     *     "data": {
     *         "tenantId": null,
     *         "industryCode": null,
     *         "appVerCode": null,
     *         "industryViewId": 139,
     *         "industryViewName": "全景展示",
     *         "displayType": 0,
     *         "industryViewType": 2,
     *         "viewType": null,
     *         "sort": null,
     *         "topicType": null
     *     }
     * }
     */
    @PostMapping("/industry/config/queryTopicInfo")
    Result<SysIndustryConfigInfoModel> queryByIndustryCode(@RequestBody SysIndustryConfigInfoModel param);
}
