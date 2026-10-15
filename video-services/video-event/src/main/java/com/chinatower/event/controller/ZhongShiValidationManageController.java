package com.chinatower.event.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.chinatower.common.entity.Result;
import com.chinatower.event.dto.zhongshi.FunctionValidationDTO;
import com.chinatower.event.dto.zhongshi.LargeModelValidationDTO;
import com.chinatower.event.dto.zhongshi.ModelValidationDTO;
import com.chinatower.event.service.ZhongShiValidationManageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

/**
 * 中试验证数据维护控制层
 */
@RestController
@Slf4j
@RequestMapping("/zhongshi/validation/manage")
public class ZhongShiValidationManageController {

    @Autowired
    private ZhongShiValidationManageService validationManageService;

    /**
     * 功能验证新增
     */
    @PostMapping("/function/add")
    public Result<Boolean> addFunctionValidation(@RequestBody FunctionValidationDTO req) {
        log.info("新增功能验证，入参: {}", req);
        return validationManageService.addFunctionValidation(req);
    }

    /**
     * 功能验证编辑
     */
    @PostMapping("/function/edit")
    public Result<Boolean> editFunctionValidation(@RequestBody FunctionValidationDTO req) {
        log.info("编辑功能验证，入参: {}", req);
        return validationManageService.editFunctionValidation(req);
    }

    /**
     * 功能验证删除
     */
    @PostMapping("/function/delete")
    public Result<Boolean> deleteFunctionValidation(@RequestBody FunctionValidationDTO req) {
        log.info("删除功能验证，入参: {}", req);
        return validationManageService.deleteFunctionValidation(req.getId());
    }

    /**
     * 功能验证列表查询
     */
    @PostMapping("/function/list")
    public Result<List<FunctionValidationDTO>> listFunctionValidation(@RequestBody FunctionValidationDTO req) {
        log.info("列表查询功能验证，入参: {}", req);
        return Result.data(validationManageService.listFunctionValidation(req));
    }

    /**
     * 功能验证分页查询
     */
    @PostMapping("/function/page")
    public Result<IPage<FunctionValidationDTO>> pageFunctionValidation(@RequestBody FunctionValidationDTO req) {
        log.info("分页查询功能验证，入参: {}", req);
        return Result.data(validationManageService.pageFunctionValidation(req));
    }

    /**
     * 小模型验证新增
     */
    @PostMapping("/model/add")
    public Result<Boolean> addModelValidation(@RequestBody ModelValidationDTO req) {
        log.info("新增小模型验证，入参: {}", req);
        return validationManageService.addModelValidation(req);
    }

    /**
     * 小模型验证编辑
     */
    @PostMapping("/model/edit")
    public Result<Boolean> editModelValidation(@RequestBody ModelValidationDTO req) {
        log.info("编辑小模型验证，入参: {}", req);
        return validationManageService.editModelValidation(req);
    }

    /**
     * 小模型验证删除
     */
    @PostMapping("/model/delete")
    public Result<Boolean> deleteModelValidation(@RequestBody ModelValidationDTO req) {
        log.info("删除小模型验证，入参: {}", req);
        return validationManageService.deleteModelValidation(req.getId());
    }

    /**
     * 小模型验证列表查询
     */
    @PostMapping("/model/list")
    public Result<List<ModelValidationDTO>> listModelValidation(@RequestBody ModelValidationDTO req) {
        log.info("列表查询小模型验证，入参: {}", req);
        return Result.data(validationManageService.listModelValidation(req));
    }

    /**
     * 小模型验证分页查询
     */
    @PostMapping("/model/page")
    public Result<IPage<ModelValidationDTO>> pageModelValidation(@RequestBody ModelValidationDTO req) {
        log.info("分页查询小模型验证，入参: {}", req);
        return Result.data(validationManageService.pageModelValidation(req));
    }

    /**
     * 大模型验证新增
     */
    @PostMapping("/large-model/add")
    public Result<Boolean> addLargeModelValidation(@RequestBody LargeModelValidationDTO req) {
        log.info("新增大模型验证，入参: {}", req);
        return validationManageService.addLargeModelValidation(req);
    }

    /**
     * 大模型验证编辑
     */
    @PostMapping("/large-model/edit")
    public Result<Boolean> editLargeModelValidation(@RequestBody LargeModelValidationDTO req) {
        log.info("编辑大模型验证，入参: {}", req);
        return validationManageService.editLargeModelValidation(req);
    }

    /**
     * 大模型验证删除
     */
    @PostMapping("/large-model/delete")
    public Result<Boolean> deleteLargeModelValidation(@RequestBody LargeModelValidationDTO req) {
        log.info("删除大模型验证，入参: {}", req);
        return validationManageService.deleteLargeModelValidation(req.getId());
    }

    /**
     * 大模型验证列表查询
     */
    @PostMapping("/large-model/list")
    public Result<List<LargeModelValidationDTO>> listLargeModelValidation(@RequestBody LargeModelValidationDTO req) {
        log.info("列表查询大模型验证，入参: {}", req);
        return Result.data(validationManageService.listLargeModelValidation(req));
    }

    /**
     * 大模型验证分页查询
     */
    @PostMapping("/large-model/page")
    public Result<IPage<LargeModelValidationDTO>> pageLargeModelValidation(@RequestBody LargeModelValidationDTO req) {
        log.info("分页查询大模型验证，入参: {}", req);
        return Result.data(validationManageService.pageLargeModelValidation(req));
    }
}
