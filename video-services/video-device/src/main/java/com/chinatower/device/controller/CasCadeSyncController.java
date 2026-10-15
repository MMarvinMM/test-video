package com.chinatower.device.controller;

import cn.hutool.core.collection.CollUtil;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.common.core.vo.ResultEntity;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.device.constants.DeviceSyncConstants;
import com.chinatower.device.dto.CascadeBindingImportVO;
import com.chinatower.device.service.ICasCadeSyncService;
import com.chinatower.platform.common.exception.CustomException;
import com.chinatower.platform.common.utils.poi.ExcelUtil;
import com.chinatower.platform.constants.DeviceConstants;
import com.chinatower.platform.entity.ImportParams;
import com.chinatower.platform.entity.device.*;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.service.ISysImportService;
import com.chinatower.platform.utils.SecurityUtils;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * video-device
 */
@RestController
@Slf4j
@Api(value = "基础级联平台信息模块", tags = "基础级联平台信息模块")
public class CasCadeSyncController {
    @Autowired
    private ICasCadeSyncService cascadeSyncService;

    @Resource
    private ISysImportService importService;

    /**
     * 同步应用级联平台信息
     */
    @PostMapping(value = "/device/synCascadePlatform")
    public ResultEntity synCascadePlatform(@RequestBody List<SynCobtainVO> reqList) {
        log.info("同步应用级联平台信息:{}",reqList);
        for (SynCobtainVO synCobtainVO: reqList) {
            if (StringUtils.isEmpty(synCobtainVO.getCreator())){
                synCobtainVO.setCreator("system-yg");
            }
        }
        Result<String> result = cascadeSyncService.synCascadePlatform(reqList);
        ResultEntity resultEntity = ResultEntity.builder()
                .returnCode(ResultCode.SUCCESS.getCode() == result.getCode() ? DeviceConstants.SUCCESS_CODE : DeviceConstants.ERORR_CODE)
                .returnMsg(result.getMsg())
                .build();
        return resultEntity;
    }

    /**
     * 订购变更后需要重新同步级联平台信息
     * 根据tennantId查询0，1，2状态的deciceCode，与sys_tenant_cascade_device_rel比较，减少的device，调用6.52下发
     */
    @PostMapping(value = "/device/synDelCascadeByOrder")
    public Result synDelCascadeByOrder(@RequestBody CasCadeSyncVO casCadeSyncVO) {
        return cascadeSyncService.synDelCascadeByOrder(casCadeSyncVO);
    }

    /**
     * 获取未被选择的级联平台信息
     */
    @PostMapping(value = "/device/findCascadeList")
    public Result<List<SynCobtainVO>> findCascadeList(@RequestBody CasCadeSyncVO casCadeSyncVO) {
        return cascadeSyncService.findCascadeList(casCadeSyncVO);
    }

    /**
     * 级联平台信息绑定下发
     */
    @PostMapping(value = "/device/cascadeBinding")
    public Result<String> cascadeBinding(@Validated @RequestBody CasCadeBindPlatInfoVO param) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null || loginUser.getUser() == null) {
            throw new CustomException("用户未登录");
        }
        param.setOperator(loginUser.getUser().getUserName());
        param.setOperatorName(loginUser.getUser().getNickName());
        param.setModifyTime(new Date());
        return cascadeSyncService.cascadeBinding(param);
    }

    /**
     * 根据客户id查询级联平台绑定关系
     */
    @GetMapping(value = "/device/queryCascadeBindingTenantId")
    public Result<List<String>> queryCascadeBindingTenantId(@RequestParam("platCode") String platCode) {
        return cascadeSyncService.queryCascadeBindingTenantId(platCode);
    }

    /**
     * 导入级联绑定关系
     */
    @PostMapping(value = "/device/importCascadeBinding")
    public Result<String> importCascadeBinding(MultipartFile file, String importType, String fileUrl, String platCode, String tenantId) throws Exception {

        List<CascadeBindingImportVO> list = new ExcelUtil<>(CascadeBindingImportVO.class).importExcel(file.getInputStream());
        if (CollUtil.isEmpty(list)) {
            throw new CustomException(DeviceSyncConstants.IMPORT_DATA_NOT_NULL);
        }
        if (list.size() > DeviceSyncConstants.EXCEL_IMPORT_MAX_NUM) {
            throw new CustomException(DeviceSyncConstants.EXCEL_IMPORT_MAX_NUM_ERROR);
        }
        if (StringUtils.isEmpty(platCode) || StringUtils.isEmpty(tenantId)) {
            throw new CustomException("必填参数不能为空");
        }
        ImportParams<CascadeBindingImportVO> params = new ImportParams<>(list, importType, file.getOriginalFilename(), fileUrl);
        params.put(DeviceSyncConstants.LOGIN_USER, SecurityUtils.getLoginUser().getUser());
        return importService.autoThreadImport(params, p -> cascadeSyncService.importData(p, platCode, tenantId));
    }

    /**
     * 查询导入级联绑定关系列表
     */
    @GetMapping(value = "/device/queryCascadeBindingImportList")
    public Result<CasCadeBindPlatInfoVO> queryCascadeBindingImportList(@RequestParam("importId") String importId) {
        return cascadeSyncService.queryCascadeBindingImportList(importId);
    }

    /**
     * 根据客户id查询级联平台信息
     */
    @PostMapping(value = "/device/queryCascadeInfoList")
    public Result<CasCadeBindPlatInfoVO> queryCascadeInfoList(@RequestBody CasCadeSyncVO casCadeSyncVO) {
        return cascadeSyncService.queryCascadeInfoList(casCadeSyncVO);
    }

    /**
     *  生成级联平台组织编码
     */
    @PostMapping(value = "/device/generateCascadeOrgCode")
    public Result<String> generateCascadeOrgCode(@RequestBody CasCadeGenerateOrgCodeVO param) {
        return cascadeSyncService.generateCascadeOrgCode(param);
    }
}
