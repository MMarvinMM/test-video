package com.chinatower.event.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.chinatower.common.entity.Result;
import com.chinatower.event.dto.zhongshi.FunctionValidationDTO;
import com.chinatower.event.dto.zhongshi.LargeModelValidationDTO;
import com.chinatower.event.dto.zhongshi.ModelValidationDTO;
import java.util.List;

/**
 * 中试验证数据维护服务
 */
public interface ZhongShiValidationManageService {

    /**
     * 功能验证新增
     */
    Result<Boolean> addFunctionValidation(FunctionValidationDTO req);

    /**
     * 功能验证编辑
     */
    Result<Boolean> editFunctionValidation(FunctionValidationDTO req);

    /**
     * 功能验证删除
     */
    Result<Boolean> deleteFunctionValidation(Long id);

    /**
     * 功能验证列表查询
     */
    List<FunctionValidationDTO> listFunctionValidation(FunctionValidationDTO req);

    /**
     * 功能验证分页查询
     */
    IPage<FunctionValidationDTO> pageFunctionValidation(FunctionValidationDTO req);

    /**
     * 小模型验证新增
     */
    Result<Boolean> addModelValidation(ModelValidationDTO req);

    /**
     * 小模型验证编辑
     */
    Result<Boolean> editModelValidation(ModelValidationDTO req);

    /**
     * 小模型验证删除
     */
    Result<Boolean> deleteModelValidation(Long id);

    /**
     * 小模型验证列表查询
     */
    List<ModelValidationDTO> listModelValidation(ModelValidationDTO req);

    /**
     * 小模型验证分页查询
     */
    IPage<ModelValidationDTO> pageModelValidation(ModelValidationDTO req);

    /**
     * 大模型验证新增
     */
    Result<Boolean> addLargeModelValidation(LargeModelValidationDTO req);

    /**
     * 大模型验证编辑
     */
    Result<Boolean> editLargeModelValidation(LargeModelValidationDTO req);

    /**
     * 大模型验证删除
     */
    Result<Boolean> deleteLargeModelValidation(Long id);

    /**
     * 大模型验证列表查询
     */
    List<LargeModelValidationDTO> listLargeModelValidation(LargeModelValidationDTO req);

    /**
     * 大模型验证分页查询
     */
    IPage<LargeModelValidationDTO> pageLargeModelValidation(LargeModelValidationDTO req);
}
