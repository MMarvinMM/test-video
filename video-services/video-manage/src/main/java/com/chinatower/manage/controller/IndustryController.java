package com.chinatower.manage.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.core.utils.ServletUtils;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.core.vo.ResultEntity;
import com.chinatower.common.core.web.domain.AjaxResult;
import com.chinatower.common.entity.Result;
import com.chinatower.manage.api.dto.SysIndustryConfigInfoModel;
import com.chinatower.manage.dataobject.SysIndustryView;
import com.chinatower.manage.dto.SysIndustryViewModel;
import com.chinatower.manage.dto.SysIndustryViewRelModel;
import com.chinatower.manage.dto.SysThirdPartyScreenConfigModel;
import com.chinatower.manage.repository.ISysIndustryViewRelService;
import com.chinatower.manage.repository.ISysViewTemplateService;
import com.chinatower.platform.entity.admin.SysConfig;
import com.chinatower.platform.entity.admin.SysIndustryVerConfDTO;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * video-manage
 * 行业应用相关控制器
 *
 * @author chmod
 * @version 1.0
 * @date 2022/1/18 10:12
 */
@RestController
@Slf4j
public class IndustryController {
    @Autowired
    private ISysIndustryViewRelService sysIndustryViewRelService;
    @Autowired
    private ISysViewTemplateService sysViewTemplateService;

    @Autowired
    private SystemFeignClient systemFeignClient;

    /**
     * 根据条件查询旧大屏关联信息
     * @param model 参数
     * @return 返回值
     */
    @PostMapping("/industry/rel/queryIndustryViewRel")
    public Result<Object> queryIndustryViewRel(@RequestBody SysIndustryViewRelModel model) {
        return sysIndustryViewRelService.querySysIndustryViewRel(model);
    }

    /**
     * 根据条件查询旧大屏配置信息
     * @param model 参数
     * @return 返回值
     */
    @PostMapping("/industry/compo/queryViewTemplate")
    public Result<Object> queryViewTemplate(@RequestBody SysIndustryViewModel model) {
        return sysViewTemplateService.queryViewTemplate(model);
    }

    /**
     * 根据租户和行业查询大屏配置信息
     * 查询大屏配置信息【包含行业大屏和第三方大屏】
     * @param param 参数
     * @return 返回值
     */
    @PostMapping("/industry/config/queryIndustryConfigInfo")
    public Result<List<SysIndustryConfigInfoModel>> queryIndustryConfigInfo(@RequestBody SysIndustryConfigInfoModel param) {
        return sysIndustryViewRelService.queryIndustryConfigInfo(param);
    }

    /**
     * 查询数字孪生大屏配置信息
     * @return 数字孪生大屏信息
     */
    @PostMapping("/industry/config/queryDigitalTwinScreenConfigInfo")
    public Result<SysThirdPartyScreenConfigModel> queryDigitalTwinScreenConfigInfo() {
        return sysIndustryViewRelService.queryDigitalTwinScreenConfigInfo();
    }

    /**
     * 查询客户权限下的所有大屏数据
     *
     * @return 包含所有视图模板的Result对象
     */
    @PostMapping("/industry/config/queryAllViewTemplate")
    public Result<List<SysIndustryView>> queryAllViewTemplate() {
        return sysViewTemplateService.queryAllViewTemplate();
    }

    /**
     * 获取第三方大屏免登录地址
     *
     * @param model 包含行业视图关系的模型
     * @return 返回带有授权的第三方URL
     */
    @PostMapping("/industry/config/getThirdPartyUrlWithAuthorization")
    public Result<String> getThirdPartyUrlWithAuthorization(@RequestBody SysIndustryViewRelModel model) {
        return sysIndustryViewRelService.getThirdPartyUrlWithAuthorization(model);
    }


    /**
     * 三方大屏免登录地址mock接口
     * @param data 参数
     * @return 返回值
     */
    @PostMapping("/industry/config/authorizationMock")
    public Object authorizationMock(@RequestBody JSONObject data) {
        log.info("模拟鉴权入参：{}", JSON.toJSONString(data));
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = servletRequestAttributes.getRequest();
        Map<String, String> map = ServletUtils.getHeaders(request);
        log.info("模拟鉴权header：{}", JSON.toJSONString(map));
        String httpStr = "https://fh.dji.com/organization/72d0ea22-047b-461e-8c01-27cbc27f322b/project/5195819f-0b4f-4c87-8141-69a572da27ac#/team";
        AjaxResult ajaxResult = systemFeignClient.getConfValue("AUTHORIZATIONMOCK");
        if ("200".equals(String.valueOf(ajaxResult.get(AjaxResult.CODE_TAG)))) {
            SysConfig sysConfig = JSON.parseObject(JSON.toJSONString(ajaxResult.get(AjaxResult.DATA_TAG)), SysConfig.class);
            if (sysConfig != null && StringUtils.isNotEmpty(sysConfig.getConfigValue())) {
                httpStr = sysConfig.getConfigValue();
            }
        }
        Map url = new HashMap<>();
        url.put("url", httpStr);
        ResultEntity resultEntity = ResultEntity.success(url);
        return resultEntity;
    }

    /**
     * 根据大屏id和当前登录行业查询大屏配置信息
     * @param param
     * @return
     */
    @PostMapping("/industry/config/queryTopicInfo")
    public Result<SysIndustryConfigInfoModel> queryByIndustryCode(@RequestBody SysIndustryConfigInfoModel param) {
        if (Objects.isNull(param.getIndustryViewId()) || Objects.isNull(param.getIndustryViewType())) {
            return Result.fail("查询失败,参数不能为空");
        }
        return Result.data(sysIndustryViewRelService.queryByIndustryCode(param));
    }

    /**
     * 查询大屏banner配置
     * @param
     * @return
     */
    @PostMapping("/industry/config/queryIndustryVerConf")
    public Result<SysIndustryVerConfDTO> queryIndustryVerConf() {
        return Result.data(sysIndustryViewRelService.queryIndustryVerConf());
    }
}
