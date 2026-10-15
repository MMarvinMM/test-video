package com.chinatower.platform.project.system.controller;

import java.util.*;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.common.utils.StringUtils;
import com.chinatower.controller.TowerBaseController;
import com.chinatower.log.annotation.TowerLog;
import com.chinatower.log.enums.BusinessType;
import com.chinatower.platform.entity.admin.SysTenantInfo;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.project.system.domain.dto.SwithUserDTO;
import com.chinatower.platform.project.system.domain.vo.TenantUserSelectVO;
import com.chinatower.platform.project.system.service.ISysTenantInfoService;
import com.chinatower.platform.utils.SecurityUtils;
import com.chinatower.system.api.request.tenant.AddTenantInfoRequest;
import com.chinatower.system.api.request.tenant.ModifyTenantInfoRequest;
import com.chinatower.system.api.request.tenant.QuTenantInfoRequest;
import com.chinatower.system.api.request.tenant.TenantTradeRequest;
import com.chinatower.system.api.resp.SysIndustryViewRelResponse;
import com.chinatower.system.api.resp.tenant.SysTenantInfoBaseResponse;
import com.chinatower.system.api.resp.tenant.SysTenantInfoResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * video-system
 * 租户Controller
 *
 * @author liuhuanhuan
 * @date 2022-01-19
 */
@Api(tags = "租户管理")
@RestController
@Validated
@RequestMapping("/system/tenant")
public class SysTenantInfoController extends TowerBaseController {
    @Autowired
    private ISysTenantInfoService sysTenantInfoService;

    @ApiOperation(value = "根据租户Id查询租户信息")
    @GetMapping("/selectByTenantId/{tenantId}")
    public Result<List<SysTenantInfoResponse>> selectByTenantId(@PathVariable String tenantId) {
        return Result.data(sysTenantInfoService.selectTenantInfoByTenantId(tenantId));
    }

    /**
     * 查询租户管理列表
     */
    @ApiOperation(value = "查询租户管理列表")
    @PostMapping("/list")
    public Result<Map<String, Object>> list(@RequestBody QuTenantInfoRequest quTenantInfoRequest) {
        if (StringUtils.isNotNull(quTenantInfoRequest.getPageNum()) && StringUtils.isNotNull(quTenantInfoRequest.getPageSize())) {
            PageHelper.startPage(quTenantInfoRequest.getPageNum(), quTenantInfoRequest.getPageSize());
        }
        List<SysTenantInfo> list = sysTenantInfoService.selectSysTenantInfoList(quTenantInfoRequest);
        return result(list);
    }

    /**
     * 查询租户管理列表(feign调用)
     */
    @ApiOperation(value = "查询租户管理列表")
    @PostMapping("/listTenantInfo")
    public Result<Map<String, Object>> listTenantInfo(@RequestBody QuTenantInfoRequest quTenantInfoRequest) {
        if (StringUtils.isNotNull(quTenantInfoRequest.getPageNum()) && StringUtils.isNotNull(quTenantInfoRequest.getPageSize())) {
            PageHelper.startPage(quTenantInfoRequest.getPageNum(), quTenantInfoRequest.getPageSize());
        }
        SysUser user = SecurityUtils.getLoginUser().getUser();
        quTenantInfoRequest.setPlatformCode(user.getPlatformCode());
        List<SysTenantInfo> list = sysTenantInfoService.selectSysTenantInfoList(quTenantInfoRequest);
        return result(BeanUtil.copyToList(list, SysTenantInfoResponse.class));
    }

    /**
     * 获取租户管理详细信息
     */
    @ApiOperation(value = "获取租户管理详细信息")
    @GetMapping(value = "/detail/{id}")
    public Result<SysTenantInfo> getInfo(@PathVariable("id") Long id) {
        return Result.data(sysTenantInfoService.selectSysTenantInfoById(id));
    }

    /**
     * 新增租户管理
     */
    @ApiOperation(value = "新增租户")
    @TowerLog(title = "租户管理", businessType = BusinessType.INSERT)
    @PostMapping(value = "/add")
    public Result add(@Validated @RequestBody AddTenantInfoRequest addTenantInfoRequest) {
        Result result = this.validTenantTrade(addTenantInfoRequest.getTenantTrades());
        if(ResultCode.FAILURE.getCode() == result.getCode()) {
            return result;
        }
        SysTenantInfo sysTenantInfo = new SysTenantInfo();
        BeanUtils.copyProperties(addTenantInfoRequest, sysTenantInfo);
        return toAjax(sysTenantInfoService.insertSysTenantInfo(sysTenantInfo));
    }

    /**
     * 校验行业信息
     * @param tenantTrades
     * @return
     */
    private Result validTenantTrade(List<TenantTradeRequest> tenantTrades) {
        if(CollUtil.isNotEmpty(tenantTrades)) {
            for(TenantTradeRequest tenantTrade:tenantTrades) {
                if(tenantTrade.isEmpty()) {
                    return Result.fail("行业类别和行业版本不能为空");
                }
            }
            Set<TenantTradeRequest> set = new HashSet<>(tenantTrades);
            if(set.size() != tenantTrades.size()) {
                return Result.fail("行业信息重复");
            }
        }
        return Result.success("");
    }

    /**
     * 新增租户管理
     */
    @ApiOperation(value = "新增租户，视联feign接口专用")
    @TowerLog(title = "租户管理", businessType = BusinessType.INSERT)
    @PostMapping(value = "/addTenantInfo")
    public Result addTenantInfo(@Validated @RequestBody AddTenantInfoRequest addTenantInfoRequest) {
        Result result = this.validTenantTrade(addTenantInfoRequest.getTenantTrades());
        if(ResultCode.FAILURE.getCode() == result.getCode()) {
            return result;
        }
        SysTenantInfo sysTenantInfo = new ObjectMapper().convertValue(addTenantInfoRequest, new TypeReference<SysTenantInfo>() {});
        return toAjax(sysTenantInfoService.insertSysTenantInfo4Video(sysTenantInfo));
    }

    /**
     * 修改租户管理
     */
    @ApiOperation(value = "修改租户")
    @TowerLog(title = "租户管理", businessType = BusinessType.UPDATE)
    @PostMapping(value = "/update")
    public Result edit(@Validated @RequestBody ModifyTenantInfoRequest modifyTenantInfoRequest) {
        Result result = this.validTenantTrade(modifyTenantInfoRequest.getTenantTrades());
        if(ResultCode.FAILURE.getCode() == result.getCode()) {
            return result;
        }
        SysTenantInfo sysTenantInfo = new SysTenantInfo();
        BeanUtils.copyProperties(modifyTenantInfoRequest, sysTenantInfo);
        sysTenantInfo.setDeptId(null);
        return toAjax(sysTenantInfoService.updateSysTenantInfo(sysTenantInfo));
    }

    /**
     * 删除租户管理
     */
    @ApiOperation(value = "删除租户")
    @TowerLog(title = "租户管理", businessType = BusinessType.DELETE)
    @GetMapping("/del/{id}")
    public Result remove(@PathVariable Long id) {
        return toAjax(sysTenantInfoService.deleteSysTenantInfoById(id));
    }

    @ApiOperation(value = "查询租户编码是否存在")
    @GetMapping("/checkTenantId/{tenantId}")
    public Result<Integer> checkTenantId(@PathVariable String tenantId){
        return Result.data(sysTenantInfoService.selectCountByTenantId(tenantId));
    }

    @ApiOperation(value = "根据租户名称查询租户信息")
    @GetMapping("/selectByTenantNameAuser/{tenantName}")
    public Result<List<SysTenantInfoResponse>> selectByTenantNameAuser(@PathVariable String tenantName) {
        SysTenantInfo sysTenantInfo=new SysTenantInfo();
        sysTenantInfo.setTenantName(tenantName);
        return Result.data(sysTenantInfoService.selectByTenantNameAuser(sysTenantInfo));
    }


    @ApiOperation(value = "根据租户id查询所有行业版本信息")
    @GetMapping("/selectAppVerCodeByTenId/{tenantId}")
    public Result<List<SysIndustryViewRelResponse>> selectAppVerCodeByTenId(@PathVariable String tenantId) {
        return Result.data(sysTenantInfoService.selectAppVerCodeByTenId(tenantId, null));
    }

    @ApiOperation(value = "根据租户id查询所有行业版本信息")
    @GetMapping("/selectAppVerCodeByTenId/{tenantId}/{acceptNode}")
    public Result<List<SysIndustryViewRelResponse>> selectAppVerCodeByTenIdV2(@PathVariable String tenantId, @PathVariable String acceptNode) {
        return Result.data(sysTenantInfoService.selectAppVerCodeByTenId(tenantId, acceptNode));
    }

    @ApiOperation(value = "根据租户id查询所有属地行业版本信息")
    @GetMapping("/selectLocalizationAppVerCodeByTenId")
    public Result<List<SysIndustryViewRelResponse>> selectLocalizationAppVerCodeByTenId(String tenantId, String acceptNode) {
        return Result.data(sysTenantInfoService.selectLocalizationAppVerCodeByTenId(tenantId, acceptNode));
    }

    @ApiOperation(value = "根据租户Id查询租户信息")
    @GetMapping("/selectByTenantIdAuser/{tenantId}")
    public Result<List<SysTenantInfoBaseResponse>> selectByTenantIdAuser(@PathVariable String tenantId) {
        SysTenantInfo sysTenantInfo=new SysTenantInfo();
        sysTenantInfo.setTenantId(tenantId);
        return Result.data(sysTenantInfoService.selectByTenantIdAuser(sysTenantInfo));
    }

    @ApiOperation(value = "根据租户Id/租户名称/账号查询租户信息")
    @GetMapping("/selectTenantUserByKeyword")
    public Result<List<TenantUserSelectVO>> selectTenantUserByKeyword(@RequestParam("keyword") String keyword){
        SwithUserDTO dto = new SwithUserDTO();
        dto.setKeyword(keyword);
        return Result.data(sysTenantInfoService.selectTenantUserByKeyword(dto));
    }





}
