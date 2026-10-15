package com.chinatower.manage.repository.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.core.exception.CommonErrorCode;
import com.chinatower.common.core.utils.DateUtils;
import com.chinatower.common.core.utils.HMAC_SHA1;
import com.chinatower.common.core.web.domain.AjaxResult;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.manage.api.dto.SysIndustryConfigInfoModel;
import com.chinatower.manage.dto.IndustryViewBannerInfoDTO;
import com.chinatower.manage.dto.SysIndustryViewRelModel;
import com.chinatower.manage.dto.SysThirdPartyScreenConfigModel;
import com.chinatower.manage.dto.ViewModel;
import com.chinatower.manage.mapper.SysIndustryViewRelMapper;
import com.chinatower.manage.repository.ISysIndustryViewRelService;
import com.chinatower.platform.common.exception.CustomException;
import com.chinatower.platform.constants.Constants;
import com.chinatower.platform.entity.admin.SysConfig;
import com.chinatower.platform.entity.admin.SysIndustryVerConfDTO;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.utils.OkhttpUtil;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.page.PageMethod;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

import static com.chinatower.platform.constants.Constants.PLATFORM_CODE;

/**
 * 行业大屏关联业务接口实现
 *
 * @author chmod
 * @version 1.0
 * @date 2022/2/10 09:10
 */
@Slf4j
@Service
public class SysIndustryViewRelServiceImpl implements ISysIndustryViewRelService {
    @Autowired
    private SysIndustryViewRelMapper sysIndustryViewRelMapper;

    @Autowired
    private SystemFeignClient systemFeignClient;

    @Override
    public Result<Object> querySysIndustryViewRel(SysIndustryViewRelModel model) {
        Result<Object> result;

        if (SecurityUtils.getLoginUser() != null && SecurityUtils.getLoginUser().getUser() != null) {
            model.setAcceptNode(SecurityUtils.getLoginUser().getUser().getPlatformCode());
        }

        try {
            if (model.isEnablePage()) {
                Integer pageNum = model.getPageNum();
                Integer pageSize = model.getPageSize();
                if (null == pageNum || pageNum <= 0) {
                    pageNum = 1;
                }
                if (null == pageSize || pageSize <= 0) {
                    pageSize = 15;
                }
                PageMethod.startPage(pageNum, pageSize);
                List<SysIndustryViewRelModel> resultList = sysIndustryViewRelMapper.querySysIndustryViewRel(model);
                Map<String, IndustryViewBannerInfoDTO> bannerInfoMap = new HashMap<>();
                if (!CollectionUtils.isEmpty(resultList)){
                    List<Long> bannerIdList = resultList.stream().filter(item -> item.getBannerId() != null).map(SysIndustryViewRelModel::getBannerId).collect(Collectors.toList());
                    List<IndustryViewBannerInfoDTO> viewBannerInfoList = sysIndustryViewRelMapper.queryCompoByIdList(bannerIdList);
                    if (!CollectionUtils.isEmpty(viewBannerInfoList)) {
                        bannerInfoMap = viewBannerInfoList.stream().collect(Collectors.toMap(IndustryViewBannerInfoDTO::getCompoId, p -> (p), (a, b) -> a));
                    }
                }
                for (SysIndustryViewRelModel viewRel : resultList) {
                    if (viewRel.getBannerId() != null) {
                        viewRel.setBanner(bannerInfoMap.get(String.valueOf(viewRel.getBannerId())));
                        transViewListStrand(viewRel);
                    }
                }
                PageInfo<SysIndustryViewRelModel> pageInfo = new PageInfo<>(resultList);
                result = Result.success("查询成功");
                result.setData(pageInfo);
            } else {
                List<SysIndustryViewRelModel> resultList = sysIndustryViewRelMapper.querySysIndustryViewRel(model);
                Map<String, IndustryViewBannerInfoDTO> bannerInfoMap = new HashMap<>();
                if (!CollectionUtils.isEmpty(resultList)){
                    List<Long> bannerIdList = resultList.stream().filter(item -> item.getBannerId() != null).map(SysIndustryViewRelModel::getBannerId).collect(Collectors.toList());
                    List<IndustryViewBannerInfoDTO> viewBannerInfoList = sysIndustryViewRelMapper.queryCompoByIdList(bannerIdList);
                    if (!CollectionUtils.isEmpty(viewBannerInfoList)) {
                        bannerInfoMap = viewBannerInfoList.stream().collect(Collectors.toMap(IndustryViewBannerInfoDTO::getCompoId, p -> (p), (a, b) -> a));
                    }
                }
                for (SysIndustryViewRelModel viewRel : resultList) {
                    if (viewRel.getBannerId() != null) {
                        viewRel.setBanner(bannerInfoMap.get(String.valueOf(viewRel.getBannerId())));
                        transViewListStrand(viewRel);
                    }
                }
                result = Result.success("查询成功");
                result.setData(resultList);
            }
        } catch (Exception e) {
            log.error("查询行业配置异常", e);
            result = Result.fail(ResultCode.ERROR.getCode(), "查询行业配置异常");
        }
        return result;
    }

    /**
     * @param sysIndustryViewRelModel 参数描述
     * @return void
     * @Description 转换viewList字符串
     * @author peng.haibo
     * @date 2022/8/16
     */
    private void transViewListStrand(SysIndustryViewRelModel sysIndustryViewRelModel) {
        ViewModel viewModel = new ViewModel();
        viewModel.setBanner(sysIndustryViewRelModel.getBanner());
        viewModel.setMenuList(sysIndustryViewRelModel.getMenuList());
        sysIndustryViewRelModel.setViewList(JSONObject.toJSONString(viewModel));
        sysIndustryViewRelModel.setIndustryViewId(sysIndustryViewRelModel.getIndustryId());
        sysIndustryViewRelModel.setMenuList(null);
        sysIndustryViewRelModel.setBanner(null);
    }

    @Override
    public Result<List<SysIndustryConfigInfoModel>> queryIndustryConfigInfo(SysIndustryConfigInfoModel param) {
        if (param == null
                || StringUtils.isEmpty(param.getTenantId())
                || StringUtils.isEmpty(param.getIndustryCode())
                || StringUtils.isEmpty(param.getAppVerCode())) {
            throw new CustomException("必填参数不能为空");
        }
        List<SysIndustryConfigInfoModel> resultList = Lists.newArrayList();
        // 旧大屏
        List<SysIndustryConfigInfoModel> industryConfigList = sysIndustryViewRelMapper.queryByIndustryInfo(param);
        if (CollectionUtils.isNotEmpty(industryConfigList)) {
            resultList.addAll(industryConfigList);
        }
        // 新大屏
        List<SysIndustryConfigInfoModel> newIndustryConfigList = sysIndustryViewRelMapper.queryByNewIndustryInfo(param);
        if (CollectionUtils.isNotEmpty(newIndustryConfigList)) {
            resultList.addAll(newIndustryConfigList);
        }

        // 新旧大屏按照顺序排序
        if (CollectionUtils.isNotEmpty(resultList)) {
            resultList.sort((o1, o2) -> {
                // 如果o1的值为null，则认为它大于任何非null的值
                if (o1.getSort() == null) {
                    return o2.getSort() == null ? 0 : 1;
                }
                // 如果o2的值为null，则认为o1小于它
                if (o2.getSort() == null) {
                    return -1;
                }
                // 两者都不为null，则按自然顺序比较它们的值
                return Integer.compare(o1.getSort(), o2.getSort());
            });
        }

        // 第三方大屏
        List<SysIndustryConfigInfoModel> industryThirdConfigList = sysIndustryViewRelMapper.queryThirdIndustryConfigByIndustryInfo(param);
        if (CollectionUtils.isNotEmpty(industryThirdConfigList)) {
            resultList.addAll(industryThirdConfigList);
        }
        return Result.data(resultList);
    }

    @Override
    public Result<SysThirdPartyScreenConfigModel> queryDigitalTwinScreenConfigInfo() {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null || loginUser.getUser() == null) {
            throw new CustomException("用户未登录");
        }
        SysUser sysUser = loginUser.getUser();
        return Result.data(sysIndustryViewRelMapper.queryDigitalTwinScreenConfigInfo(sysUser.getTenantId(), sysUser.getIndustryCode()));
    }

    private String getUrlPath(String screenUrl) {
        String urlPath = "";
        try {
            URL url = new URL(screenUrl);
            urlPath = url.getPath();
        } catch (Exception e) {
            log.error("SysIndustryViewRelServiceImpl getUrlPath error, screenUrl:{}, e:", screenUrl, e);
        }
        return urlPath;
    }

    @Override
    public Result getThirdPartyUrlWithAuthorization(SysIndustryViewRelModel model) {
        //获取第三方大屏详情
        SysThirdPartyScreenConfigModel configModel = sysIndustryViewRelMapper.selectThirdIndustryConfigById(model.getIndustryViewId());
        if (configModel == null) {
            return Result.fail("第三方大屏不存在");
        }
        //封装验签
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null || loginUser.getUser() == null) {
            throw new CustomException("用户未登录");
        }
        SysUser sysUser = loginUser.getUser();
        //签名字符串 = URL的Path部分+ X-AppKey + X-MsgId + X-RequestTime + X-CustomerCode（如果有）+ X-UserCode（如果有）。
        Date now = new Date();
        String msgId = DateUtils.dateToString(now);
        String requestTime = DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, now);

        String urlPath = getUrlPath(configModel.getScreenUrl());
        String signature = urlPath +
                configModel.getAppKey() +
                msgId +
                requestTime +
                sysUser.getTenantId() +
                sysUser.getUserId();
        String signatureStr = signature.toUpperCase();
        log.info("验签加密前====>{}", signatureStr);
        String mySignature = HMAC_SHA1.signByHmacSHA1(configModel.getSecretKey(), signatureStr);
        log.info("验签加密后====>{}", mySignature);
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("X-MsgId", msgId);
        headerMap.put("X-RequestTime", requestTime);
        headerMap.put("X-AppKey", configModel.getAppKey());
        headerMap.put("X-CustomerCode", sysUser.getTenantId());
        headerMap.put("X-UserCode", sysUser.getUserId());
        headerMap.put("X-Signature", mySignature);

        Map<String, String> bodyMap = new HashMap<>();
        bodyMap.put("msgId", msgId);
        bodyMap.put("requestTime", requestTime);
        bodyMap.put("appKey", configModel.getAppKey());
        bodyMap.put("customerCode", sysUser.getTenantId());
        bodyMap.put("userCode", sysUser.getUserId());
        bodyMap.put("signature", mySignature);
        //todo 调用鉴权接口 获取跳转url
        String screenUrl = configModel.getScreenUrl();
        JSONObject resultObject = OkhttpUtil.doPost(screenUrl, JSON.toJSONString(bodyMap), headerMap);
        if (resultObject == null) {
            return Result.fail("调用第三方鉴权失败");
        }
        String returnCode = resultObject.getString("returnCode");
        if (CommonErrorCode.SUCCESSFUL_CODE.equals(returnCode)) {
            JSONObject dataObject = JSON.parseObject(JSON.toJSONString(resultObject.get("data")));
            if (dataObject == null) {
                return Result.fail("调用第三方鉴权返回信息为空");
            }
            return Result.data(dataObject.getString("url"));
        } else {
            return Result.fail(resultObject.getString("returnMsg"));
        }
        //return Result.data(screenUrl);
    }

    /**
     * 根据大屏id和登录行业查询大屏配置信息
     * @param model
     * @return
     */
    public SysIndustryConfigInfoModel queryByIndustryCode(SysIndustryConfigInfoModel model) {
        // 获取当前的用户
        LoginUser loginUser = SecurityUtils.getLoginUser();
        model.setIndustryCode(loginUser.getUser().getIndustryCode());
        model.setAppVerCode(loginUser.getUser().getAppVerCode());
        return sysIndustryViewRelMapper.queryByIndustryCode(model);
    }

    /**
     * 查询大屏banner配置
     * @return
     */
    @Override
    public SysIndustryVerConfDTO queryIndustryVerConf() {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        SysIndustryVerConfDTO sysIndustryVerConfDTO = null;
        try {
            AjaxResult ajaxResult = systemFeignClient.getConfValue(PLATFORM_CODE);
            if (ResultCode.SUCCESS.getCode() == (int)ajaxResult.get(AjaxResult.CODE_TAG)) {
                SysConfig sysConfig = JSON.parseObject(JSON.toJSONString(ajaxResult.get(AjaxResult.DATA_TAG)), SysConfig.class);
                if(!ObjectUtils.isEmpty(sysConfig)) {
                    sysIndustryVerConfDTO = sysIndustryViewRelMapper.findBannerInfo(user.getIndustryCode(), user.getAppVerCode(), sysConfig.getConfigValue());
                }
            }
        }
        catch (Exception e) {
            log.error("查询平台编码异常", e);
        }
        return sysIndustryVerConfDTO;
    }


}
