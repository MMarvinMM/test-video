package com.chinatower.platform.project.system.controller;

import com.chinatower.common.entity.Result;
import com.chinatower.common.page.TableDataInfo;
import com.chinatower.controller.TowerBaseController;
import com.chinatower.file.deal.FileUrlEncryptUtil;
import com.chinatower.log.annotation.TowerLog;
import com.chinatower.log.enums.BusinessType;
import com.chinatower.platform.common.utils.FileUtils;
import com.chinatower.platform.common.utils.poi.ExcelUtil;
import com.chinatower.platform.framework.web.domain.AjaxResult;
import com.chinatower.platform.project.system.domain.AppUpgradeInfo;
import com.chinatower.platform.project.system.domain.vo.MiniProgramUpgradeInfoVO;
import com.chinatower.platform.project.system.domain.vo.WgtUpgradeInfoVO;
import com.chinatower.platform.project.system.service.IAppUpgradeInfoService;
import com.chinatower.platform.project.system.service.IWgtUpgradeInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.stream.Collectors;

/**
 * video-system
 * 功能说明: APP版本升级管理
 *
 * @param:
 * @return:
 * @author: 王梦迪
 * @date: 2022/4/13 17:48
 * @requestID 需求单号及需求名称
 */
@Api(tags = "APP版本升级管理")
@RestController
@Slf4j
@RequestMapping("/app/appupgradeinfo")
public class AppUpgradeInfoController extends TowerBaseController {

    @Autowired
    private IAppUpgradeInfoService appUpgradeInfoService;
    @Resource
    private IWgtUpgradeInfoService wgtUpgradeInfoService;
    @Resource
    private FileUrlEncryptUtil fileUrlEncryptUtil;

    /**
     * 查询APP版本升级列表
     */
    @ApiOperation("查询APP版本升级列表")
    @GetMapping("/list")
    public TableDataInfo list(AppUpgradeInfo appUpgradeInfo) {
        startPage();
        List<AppUpgradeInfo> list = appUpgradeInfoService.selectAppUpgradeInfoList(appUpgradeInfo);
        return getDataTable(list);
    }

    /**
     * 根据应用标识查询app最新版本
     */
    @ApiOperation("根据应用标识查询app最新版本")
    @PostMapping("/queryUpgrade")
    public AjaxResult queryUpgrade(@RequestBody AppUpgradeInfo appUpgradeInfo) {
        if (StringUtils.isBlank(appUpgradeInfo.getAppCode())) {
            return AjaxResult.error("应用程序升级显示信息参数异常appCode不能空");
        }
        try {
            return appUpgradeInfoService.queryUpgrade(appUpgradeInfo);
        } catch (Exception e) {
            log.error("应用程序升级显示信息异常" + e.getMessage());
        }
        return AjaxResult.error();
    }

    /**
     * 查询app最新版本（ios+安卓）
     */
    @ApiOperation("查询app最新版本（ios+安卓）")
    @PostMapping("/queryNewVersion")
    public TableDataInfo queryNewVersion(@RequestBody AppUpgradeInfo appUpgradeInfo) {
        List<AppUpgradeInfo> list = appUpgradeInfoService.queryNewVersion();
        return getDataTable(list);
    }

    /**
     * 导出APP版本升级列表
     */
    @ApiOperation("导出APP版本升级列表")
    @TowerLog(title = "APP版本升级", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(@RequestBody AppUpgradeInfo appUpgradeInfo, HttpServletResponse response) {
        List<AppUpgradeInfo> list = appUpgradeInfoService.selectAppUpgradeInfoList(appUpgradeInfo);
        byte[] data = new ExcelUtil<>(AppUpgradeInfo.class).exportBytes(list);
        FileUtils.download(response, data);
    }

    /**
     * 获取APP版本升级详细信息
     */
    @ApiOperation("获取APP版本升级详细信息")
    @GetMapping(value = "/detail/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        AppUpgradeInfo appUpgradeInfo = appUpgradeInfoService.selectAppUpgradeInfoById(id);
        appUpgradeInfo.setEncryptionUrl(fileUrlEncryptUtil.dealObsFileUrl(appUpgradeInfo.getUpgradeUrl()));
        return AjaxResult.success(appUpgradeInfo);
    }

    /**
     * 新增APP版本升级
     */
    @ApiOperation("新增APP版本升级")
    @TowerLog(title = "APP版本升级", businessType = BusinessType.INSERT)
    @PostMapping(value = "/add")
    public Result add(@Validated @RequestBody AppUpgradeInfo appUpgradeInfo) {
        return toAjax(appUpgradeInfoService.insertAppUpgradeInfo(appUpgradeInfo));
    }

    /**
     * 修改APP版本升级
     */
    @ApiOperation("修改APP版本升级")
    @TowerLog(title = "APP版本升级", businessType = BusinessType.UPDATE)
    @PostMapping(value = "/update")
    public Result edit(@Validated @RequestBody AppUpgradeInfo appUpgradeInfo) {
        return toAjax(appUpgradeInfoService.updateAppUpgradeInfo(appUpgradeInfo));
    }

    /**
     * 删除APP版本升级
     */
    @ApiOperation("删除APP版本升级")
    @TowerLog(title = "APP版本升级", businessType = BusinessType.DELETE)
    @GetMapping("/del/{ids}")
    public Result remove(@PathVariable Long[] ids) {
        return toAjax(appUpgradeInfoService.deleteAppUpgradeInfoByIds(ids));
    }

    /**
     * 查询wgt最新版本
     */
    @ApiOperation("查询wgt最新版本")
    @PostMapping("/queryWgtUpgrade")
    public AjaxResult queryWgtUpgrade(@RequestBody WgtUpgradeInfoVO wgtUpgradeInfoVO) {
        if (StringUtils.isEmpty(wgtUpgradeInfoVO.getColonyCode())) {
            return AjaxResult.error("属地平台编码不能为空！");
        }
        // 获取属地版本
        String colonyVersion = wgtUpgradeInfoService.queryColonyVersion(wgtUpgradeInfoVO.getColonyCode());
        WgtUpgradeInfoVO wgtUpgradeInfoVO1;
        if (StringUtils.isNotEmpty(colonyVersion)) {
            wgtUpgradeInfoVO1 = wgtUpgradeInfoService.queryWgtUpgradeInfo(colonyVersion, null == wgtUpgradeInfoVO.getType() ? 1 : wgtUpgradeInfoVO.getType());
            if (null != wgtUpgradeInfoVO1 && StringUtils.isNotEmpty(wgtUpgradeInfoVO1.getUpgradeUrl())) {
                wgtUpgradeInfoVO1.setUpgradeUrl(fileUrlEncryptUtil.dealObsFileUrl(wgtUpgradeInfoVO1.getUpgradeUrl()));
            }
        } else {
            return AjaxResult.error("属地平台编码查询不到对应的属地版本信息！");
        }
        return AjaxResult.success(wgtUpgradeInfoVO1);
    }

    /**
     * 分页查询wgt版本管理
     *
     * @param wgtUpgradeInfoVO
     * @return TableDataInfo
     */
    @PostMapping("/queryWgtList")
    public TableDataInfo queryWgtList(@RequestBody WgtUpgradeInfoVO wgtUpgradeInfoVO) {
        List<WgtUpgradeInfoVO> list = wgtUpgradeInfoService.queryWgtList(wgtUpgradeInfoVO);
        // 收集所有URL（包含父级和子级）
        if (!CollectionUtils.isEmpty(list)) {
            Set<String> fileUrls = new HashSet<>();
            for (WgtUpgradeInfoVO parent : list) {
                // 父级的URL
                if (StringUtils.isNotEmpty(parent.getUpgradeUrl())) {
                    fileUrls.add(parent.getUpgradeUrl());
                }
                // 子级的URL
                if (!CollectionUtils.isEmpty(parent.getChildren())) {
                    for (WgtUpgradeInfoVO child : parent.getChildren()) {
                        if (StringUtils.isNotEmpty(child.getUpgradeUrl())) {
                            fileUrls.add(child.getUpgradeUrl());
                        }
                    }
                }
            }
            // 批量处理URL
            if (!fileUrls.isEmpty()) {
                Map<String, String> result = fileUrlEncryptUtil.dealObsFileUrlBatch(new ArrayList<>(fileUrls));
                // 更新URL
                for (WgtUpgradeInfoVO parent : list) {
                    if (StringUtils.isNotEmpty(parent.getUpgradeUrl()) && result.containsKey(parent.getUpgradeUrl())) {
                        parent.setUpgradeUrl(result.get(parent.getUpgradeUrl()));
                    }
                    // 更新子级URL
                    if (!CollectionUtils.isEmpty(parent.getChildren())) {
                        for (WgtUpgradeInfoVO child : parent.getChildren()) {
                            if (StringUtils.isNotEmpty(child.getUpgradeUrl()) && result.containsKey(child.getUpgradeUrl())) {
                                child.setUpgradeUrl(result.get(child.getUpgradeUrl()));
                            }
                        }
                    }
                }
            }
        }
        return getDataTable(list);
    }

    /**
     * 更新wgt版本管理信息
     *
     * @param wgtUpgradeInfoVO
     * @return
     */
    @PostMapping("/updateWgt")
    public Result updateWgt(@RequestBody WgtUpgradeInfoVO wgtUpgradeInfoVO) {
        wgtUpgradeInfoService.updateWgt(wgtUpgradeInfoVO);
        return Result.success("更新wgt版本信息成功");
    }

    /**
     * 查询下载地址信息
     * @param wgtUpgradeInfoVO
     * @return
     */
    @PostMapping("/getDownloadUrl")
    public Result<WgtUpgradeInfoVO> getDownloadUrl(@RequestBody WgtUpgradeInfoVO wgtUpgradeInfoVO) {
        WgtUpgradeInfoVO wgtUpgradeInfoVO1 = wgtUpgradeInfoService.getDownloadUrl(wgtUpgradeInfoVO);
        if (null != wgtUpgradeInfoVO1 && StringUtils.isNotEmpty(wgtUpgradeInfoVO1.getUpgradeUrl())) {
            wgtUpgradeInfoVO1.setUpgradeUrl(fileUrlEncryptUtil.dealObsFileUrl(wgtUpgradeInfoVO1.getUpgradeUrl()));
        }
        return Result.data(wgtUpgradeInfoVO1);
    }

    /**
     * 查询小程序最新版本
     * @param miniProgramUpgradeInfoVO
     * @return
     */
    @ApiOperation("查询小程序最新版本")
    @PostMapping("/queryMiniProgramUpgrade")
    public AjaxResult queryMiniProgramUpgrade(@RequestBody MiniProgramUpgradeInfoVO miniProgramUpgradeInfoVO) {
        if (StringUtils.isEmpty(miniProgramUpgradeInfoVO.getColonyVersion())) {
            return AjaxResult.error("属地版本不能为空！");
        }
        // 获取属地版本
        MiniProgramUpgradeInfoVO returnMiniProgramUpgradeInfoVO = wgtUpgradeInfoService.queryMiniProgramUpgradeInfo(miniProgramUpgradeInfoVO.getColonyVersion());
        return AjaxResult.success(returnMiniProgramUpgradeInfoVO);
    }


    /**
     * 分页查询小程序版本管理
     *
     * @param miniProgramUpgradeInfoVO
     * @return TableDataInfo
     */
    @PostMapping("/queryMiniProgramList")
    public TableDataInfo queryMiniProgramList(@RequestBody MiniProgramUpgradeInfoVO miniProgramUpgradeInfoVO) {
        startPage();
        List<MiniProgramUpgradeInfoVO> list = wgtUpgradeInfoService.queryMiniProgramList(miniProgramUpgradeInfoVO);
        return getDataTable(list);
    }


    /**
     * 更新小程序版本管理信息
     *
     * @param miniProgramUpgradeInfoVO
     * @return
     */
    @PostMapping("/updateMiniProgram")
    public Result updateMiniProgram(@RequestBody MiniProgramUpgradeInfoVO miniProgramUpgradeInfoVO) {
        wgtUpgradeInfoService.updateMiniProgram(miniProgramUpgradeInfoVO);
        return Result.success("更新小程序版本信息成功");
    }



    /**
     * 新增子节点
     * @param wgtUpgradeInfoVO
     * @return
     */
    @PostMapping("/addChildWgt")
    public Result<String> addChildWgt(@RequestBody WgtUpgradeInfoVO wgtUpgradeInfoVO) {
        return wgtUpgradeInfoService.addChildWgt(wgtUpgradeInfoVO);
    }




}
