package com.chinatower.manage.repository.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.file.deal.FileUrlEncryptUtil;
import com.chinatower.manage.dataobject.SysIndustryView;
import com.chinatower.manage.dto.SysIndustryViewModel;
import com.chinatower.manage.mapper.SysIndustryViewMapper;
import com.chinatower.manage.repository.ISysViewTemplateService;
import com.chinatower.platform.constants.Constants;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.system.api.feign.client.SystemFeignClient;
import com.chinatower.system.api.model.ScreenMenuConfVO;
import com.chinatower.system.api.model.SysRoleScreenVO;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.page.PageMethod;
import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 视图大屏模板业务实现
 * @author chmod
 * @version 1.0
 * @date 2022/1/14 10:56
 */
@Slf4j
@Service
public class SysViewTemplateServiceImpl implements ISysViewTemplateService {
    @Autowired
    private SysIndustryViewMapper sysIndustryViewMapper;

    @Autowired(required = false)
    private FileUrlEncryptUtil fileUrlEncryptUtil;

    @Autowired
    private SystemFeignClient systemFeignClient;

    @Override
    public Result<Object> queryViewTemplate(SysIndustryViewModel model) {
        Result<Object> result;
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
                List<SysIndustryView> resultList = sysIndustryViewMapper.querySysIndustryView(model);
                Map<String, String> stringStringMap = dealUrl(resultList);
                resultList.stream().forEach(sysIndustryView -> {
                    sysIndustryView.setImgUrl(stringStringMap.get(sysIndustryView.getImgUrl()));
                    sysIndustryView.setCompoList(Joiner.on(",").join(sysIndustryView.getCompoLists()));
                    if (StringUtils.isNotEmpty(sysIndustryView.getBaseMapJson())) {
                        Map<String,String> baseMap = (Map<String,String>) JSON.parse(sysIndustryView.getBaseMapJson());
                        if (StringUtils.isNotEmpty(baseMap.get(Constants.IMG_URL))) {
                            baseMap.put(Constants.IMG_URL, stringStringMap.get(baseMap.get(Constants.IMG_URL)));
                        }
                        sysIndustryView.setBaseMapJson(JSONObject.toJSONString(baseMap));
                    }
                });
                PageInfo<SysIndustryView> pageInfo = new PageInfo<>(resultList);

                result = Result.success("查询成功");
                result.setData(pageInfo);
            } else {
                List<SysIndustryView> resultList = sysIndustryViewMapper.querySysIndustryView(model);
                Map<String, String> stringStringMap = dealUrl(resultList);
                // 处理Obs文件URL加密返回
                resultList.stream().forEach(sysIndustryView -> {
                    try {
                        sysIndustryView.setImgUrl(stringStringMap.get(sysIndustryView.getImgUrl()));
                        sysIndustryView.setCompoList(Joiner.on(",").join(sysIndustryView.getCompoLists()));
                        if (StringUtils.isNotEmpty(sysIndustryView.getBaseMapJson())) {
                            Map<String,String> baseMap = (Map<String,String>) JSON.parse(sysIndustryView.getBaseMapJson());
                            if (StringUtils.isNotEmpty(baseMap.get(Constants.IMG_URL))) {
                                baseMap.put(Constants.IMG_URL, stringStringMap.get(baseMap.get(Constants.IMG_URL)));
                            }
                            sysIndustryView.setBaseMapJson(JSONObject.toJSONString(baseMap));
                        }
                    } catch (Exception e) {
                        log.error("dealObsFileUrl error, e:", e);
                    }
                });
                result = Result.success("查询成功");
                result.setData(resultList);
            }
        } catch (Exception e) {
            log.error("查询视图模板异常", e);
            result = Result.fail(ResultCode.ERROR.getCode(), "查询视图模板异常");
        }

        return result;
    }

    @Override
    public Result<List<SysIndustryView>> queryAllViewTemplate() {
        LoginUser currentUser = SecurityUtils.getLoginUser();
        SysUser user = currentUser.getUser();
        String industryCode = user.getIndustryCode();
        String appVerCode = user.getAppVerCode();
        SysRoleScreenVO param = new SysRoleScreenVO();
        param.setTenantId(user.getTenantId());
        param.setIndustryCode(industryCode);
        param.setAppVerCode(appVerCode);
        param.setUserName(user.getUserName());
        Result<SysRoleScreenVO> roleScreenResult = systemFeignClient.getCurrentRoleScreen(param);
        List<SysIndustryView> resultList = Lists.newArrayList();
        // 需求：有存量角色或默认角色，展示客户关联的所有行业+第三方大屏；无存量角色且无默认角色，展示客户绑定的大屏，有个性化配置的大屏优先展示个性化配置的大屏
        if (roleScreenResult != null && roleScreenResult.getData() != null && CollectionUtils.isNotEmpty(roleScreenResult.getData().getScreenMenuConfVOList())) {
            List<ScreenMenuConfVO> roleScreenDataList = roleScreenResult.getData().getScreenMenuConfVOList();
            log.info("SysViewTemplateServiceImpl queryAllViewTemplate getCurrentRoleScreen, param:{}, result:{}",
                    JSON.toJSONString(param), JSON.toJSONString(roleScreenDataList));

            // 是否全部自定义角色账号
            boolean isCustom = StringUtils.equals(roleScreenResult.getData().getRoleScreenFlag(), "1");

            // 如果有大屏配置，但是没有默认、存量角色，按照返回数据的大屏及顺序处理
            resultList = queryCustomViewList(roleScreenDataList, user);
            // 如果有大屏配置且有默认、存量角色，填充没有返回的大屏
            if (!isCustom) {
                fillDefaultRoleScreen(resultList, user);
            }
            // 是否已排序
            boolean isSorted = StringUtils.equals(roleScreenResult.getData().getPersonalFlag(), "1");
            if (!isSorted) {
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
            }
        } else {
            // 查询所有 行业+第三方大屏
            resultList = queryAllViewList(user);
        }

        // banner加载的第一个大屏打开方式必须是原页面打开，不能为新页面打开
        if (CollectionUtils.isNotEmpty(resultList)) {
            if (!Objects.equals(resultList.get(0).getDisplayType(), 0)) {
                resultList.get(0).setDisplayType(0);
            }

            // TODO 处理数字孪生大屏
//            List<Long> digitalTwinViewIdList = resultList.stream()
//                    .filter(x -> (Objects.equals(x.getViewType(), 1) && Objects.equals(x.getScreenType(), 2)))
//                    .map(SysIndustryView::getViewId).collect(Collectors.toList());
//
//            Map<Long, Object> digitalTwinViewMap = Maps.newHashMap();
//            for (SysIndustryView viewInfo : resultList) {
//
//            }
        }

        return Result.data(resultList);
    }

    private String assembleMapKey(Long industryViewId, Integer viewType, Integer industryViewType) {
        return String.format("%s_%s_%s", industryViewId, viewType, industryViewType);
    }

    private void fillDefaultRoleScreen(List<SysIndustryView> resultList, SysUser user) {
        // 客户关联的所有大屏，将resultList里面没有的大屏填充进去，放到最后面
        List<SysIndustryView> allViewList = queryAllViewList(user);
        Map<String, SysIndustryView> resultViewMap = resultList.stream()
                .collect(Collectors.toMap(x -> assembleMapKey(x.getViewId(),
                        x.getViewType(), x.getIndustryViewType()), (p) -> p, (a, b) -> a));
        List<SysIndustryView> addViewList = Lists.newArrayList();
        for (SysIndustryView industryView : allViewList) {
            // 如果结果集不包含大屏信息，则增加到结果集后面
            String mapKey = assembleMapKey(industryView.getViewId(),
                    industryView.getViewType(), industryView.getIndustryViewType());
            if (!resultViewMap.containsKey(mapKey)) {
                addViewList.add(industryView);
            }
        }
        log.info("SysViewTemplateServiceImpl fillDefaultRoleScreen, resultList size:{}, allViewList size:{}, addViewList:{}",
                resultList.size(), allViewList.size(), JSON.toJSONString(addViewList));

        if (CollectionUtils.isNotEmpty(addViewList)) {
            resultList.addAll(addViewList);
        }
    }

    private List<SysIndustryView> queryCustomViewList(List<ScreenMenuConfVO> roleScreenDataList, SysUser user) {
        List<SysIndustryView> resultList = Lists.newArrayList();
        // 旧-应用大屏编号列表
        List<String> industryViewIdList = roleScreenDataList.stream()
                .filter(x -> (Objects.equals(x.getIndustryViewType(), 0) && (x.getViewType() == null || Objects.equals(x.getViewType(), 1))))
                .map(ScreenMenuConfVO::getIndustryViewId).collect(Collectors.toList());
        Map<String, SysIndustryView> industryViewMap = Maps.newHashMap();
        if (CollectionUtils.isNotEmpty(industryViewIdList)) {
            List<SysIndustryView> industryResultList
                    = sysIndustryViewMapper.querySysIndustryViewConf(industryViewIdList, user.getIndustryCode(), user.getAppVerCode());
            Map<String, String> stringStringMap = dealUrl(industryResultList);
            // 处理Obs文件URL加密返回
            industryResultList.forEach(sysIndustryView -> {
                try {
                    sysIndustryView.setImgUrl(stringStringMap.get(sysIndustryView.getImgUrl()));
                    sysIndustryView.setCompoList(Joiner.on(",").join(sysIndustryView.getCompoLists()));
                    if (StringUtils.isNotEmpty(sysIndustryView.getBaseMapJson())) {
                        Map<String,String> baseMap = (Map<String,String>) JSON.parse(sysIndustryView.getBaseMapJson());
                        if (StringUtils.isNotEmpty(baseMap.get(Constants.IMG_URL))) {
                            baseMap.put(Constants.IMG_URL, stringStringMap.get(baseMap.get(Constants.IMG_URL)));
                        }
                        sysIndustryView.setBaseMapJson(JSONObject.toJSONString(baseMap));
                    }
                } catch (Exception e) {
                    log.error("dealObsFileUrl error, e:", e);
                }
            });
            industryViewMap = industryResultList.stream().collect(Collectors.toMap(((x) -> String.valueOf(x.getViewId())), (p) -> p, (a, b) -> a));
        }

        // 新-应用大屏编号列表
        List<String> newIndustryViewIdList = roleScreenDataList.stream()
                .filter(x -> (Objects.equals(x.getIndustryViewType(), 0) && Objects.equals(x.getViewType(), 2)))
                .map(ScreenMenuConfVO::getIndustryViewId).collect(Collectors.toList());

        Map<String, SysIndustryView> newIndustryViewMap = Maps.newHashMap();
        if (CollectionUtils.isNotEmpty(newIndustryViewIdList)) {
            List<SysIndustryView> thirdViewInfoList
                    = sysIndustryViewMapper.querySysIndustryScreenConf(newIndustryViewIdList, user.getIndustryCode(), user.getAppVerCode());
            newIndustryViewMap = thirdViewInfoList.stream().collect(Collectors.toMap(((x) -> String.valueOf(x.getViewId())), (p) -> p, (a, b) -> a));
        }

        // 三方大屏编号列表
        List<String> thirdViewIdList = roleScreenDataList.stream()
                .filter(x -> Objects.equals(x.getIndustryViewType(), 1))
                .map(ScreenMenuConfVO::getIndustryViewId).collect(Collectors.toList());

        Map<String, SysIndustryView> thirdViewMap = Maps.newHashMap();
        if (CollectionUtils.isNotEmpty(thirdViewIdList)) {
            List<SysIndustryView> thirdViewInfoList = sysIndustryViewMapper.queryThirdViewInfoList(
                    user.getTenantId(), user.getIndustryCode(), user.getAppVerCode(), thirdViewIdList);
            thirdViewMap = thirdViewInfoList.stream().collect(Collectors.toMap(((x) -> String.valueOf(x.getViewId())), (p) -> p, (a, b) -> a));
        }

        for (ScreenMenuConfVO roleScreen : roleScreenDataList) {
            Integer industryViewType = roleScreen.getIndustryViewType();
            Integer viewType = roleScreen.getViewType();
            String industryViewId = roleScreen.getIndustryViewId();
            String attr = roleScreen.getAttr();
            String groupId = roleScreen.getGroupId();
            String tabName = roleScreen.getTabName();

            // 旧 - 行业大屏
            if (Objects.equals(industryViewType, 0) && (viewType == null || Objects.equals(viewType, 1)) && industryViewMap.containsKey(industryViewId)) {
                SysIndustryView industryView = industryViewMap.get(industryViewId);
                industryView.setViewType(industryViewType);
                industryView.setIndustryViewType(viewType);
                industryView.setAttr(attr);
                industryView.setGroupId(groupId);
                industryView.setTabName(tabName);
                resultList.add(industryView);
            }

            // 新 - 行业大屏
            if (Objects.equals(industryViewType, 0) && Objects.equals(viewType, 2) && newIndustryViewMap.containsKey(industryViewId)) {
                SysIndustryView industryView = newIndustryViewMap.get(industryViewId);
                industryView.setViewType(industryViewType);
                industryView.setIndustryViewType(viewType);
                industryView.setAttr(attr);
                industryView.setGroupId(groupId);
                industryView.setTabName(tabName);
                resultList.add(industryView);
            }

            // 第三方大屏
            else if (Objects.equals(industryViewType, 1) && thirdViewMap.containsKey(industryViewId)) {
                SysIndustryView industryView = thirdViewMap.get(industryViewId);
                industryView.setViewType(industryViewType);
                industryView.setAttr(attr);
                industryView.setGroupId(groupId);
                industryView.setTabName(tabName);
                resultList.add(industryView);
            }
        }
        return resultList;
    }

    private List<SysIndustryView> queryAllViewList(SysUser user) {
        List<SysIndustryView> resultList = Lists.newArrayList();
        // 根据客户行业查询所有 旧 - 行业大屏
        List<SysIndustryView> industryViewList = sysIndustryViewMapper.queryIndustryScreenView(user.getIndustryCode(), user.getAppVerCode(),user.getPlatformCode());
        if (CollectionUtils.isNotEmpty(industryViewList)) {
            resultList.addAll(industryViewList);
        }

        // 根据客户行业查询所有 新 - 行业大屏
        List<SysIndustryView> newIndustryViewList = sysIndustryViewMapper.queryNewIndustryScreenView(user.getIndustryCode(), user.getAppVerCode(),user.getPlatformCode());
        if (CollectionUtils.isNotEmpty(newIndustryViewList)) {
            resultList.addAll(newIndustryViewList);
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

        // 根据客户行业查询所有第三方大屏
        List<SysIndustryView> thirdViewList = sysIndustryViewMapper.queryThirdViewInfoList(user.getTenantId(), user.getIndustryCode(), user.getAppVerCode(), null);
        if (CollectionUtils.isNotEmpty(thirdViewList)) {
            resultList.addAll(thirdViewList);
        }
        return resultList;
    }

    private Map<String, String> dealUrl(List<SysIndustryView> resultList){
        // 处理Obs文件URL加密返回
        List<String> urlList = new ArrayList<>();
        resultList.forEach(sysIndustryView -> {
            urlList.add(sysIndustryView.getImgUrl());
            if (StringUtils.isNotEmpty(sysIndustryView.getBaseMapJson())) {
                Map<String,String> baseMap = (Map<String,String>) JSON.parse(sysIndustryView.getBaseMapJson());
                if (StringUtils.isNotEmpty(baseMap.get(Constants.IMG_URL))) {
                    urlList.add(baseMap.get(Constants.IMG_URL));
                }
            }
        });
        return fileUrlEncryptUtil.dealObsFileUrlBatch(urlList);
    }

}
