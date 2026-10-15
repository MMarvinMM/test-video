package com.chinatower.sync.consumer.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chinatower.device.entity.dto.AlgoMinTypeReq;
import com.chinatower.device.entity.dto.FuncTypeReq;
import com.chinatower.industry.dto.IndustryAlgorithmDTO;
import com.chinatower.industry.vo.IndustryAlgorithmQueryVo;
import com.chinatower.sync.consumer.entity.AlgorithmInfoDO;

import java.util.List;

/**
 * <p>
 * 算法基本信息表 Mapper 接口
 * </p>
 *
 * @author AutoGen
 * @since 2022-02-16
 */
public interface AlgorithmInfoMapper extends BaseMapper<AlgorithmInfoDO> {
    /**
     * 根据算法小类更新对应算法上的功能小类。
     * @param: reqList
     * @return: boolean
     * @author: zheng.yan
     * @date: 2022/2/17
     * @version:
     */
    int updateFuncCodeByMinTypeCode(FuncTypeReq funcTypeReq);

    int updateModelConfigBatch(List<FuncTypeReq> list);

    int updateFuncCodeByMinTypeCodes(List<FuncTypeReq> list);

}
