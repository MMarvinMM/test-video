package com.chinatower.algorithm.repository;

import com.chinatower.algorithm.dataobject.FunctionTypeDO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.chinatower.device.entity.vo.FunctionTypeVO;

import java.util.Map;

/**
 * <p>
 * 功能类别表 服务类
 * </p>
 *
 * @author AutoGen
 * @since 2022-02-16
 */
public interface IFunctionTypeRepository extends IService<FunctionTypeDO> {

    Map getFunction(String tenantId);

    FunctionTypeVO getFunctionDetail(String id);
}
