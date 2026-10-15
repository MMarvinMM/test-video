package com.chinatower.algorithm.mapper;

import com.chinatower.algorithm.dataobject.FunctionTypeDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chinatower.device.entity.vo.FunctionTypeVO;

import java.util.List;

/**
 * <p>
 * 功能类别表 Mapper 接口
 * </p>
 *
 * @author AutoGen
 * @since 2022-02-16
 */
public interface FunctionTypeMapper extends BaseMapper<FunctionTypeDO> {

    /**
     * 根据TenantId获取功能类别
     * @param tenantId
     * @return
     */
    List<FunctionTypeVO> getAlgorithmByTenantId(String tenantId, String industryCode, String appVerCode);

    List<FunctionTypeVO> getAlgorithm(String tenantId, String industryCode, String appVerCode);

    List<FunctionTypeVO> getFunctionDetail(String id);
}
