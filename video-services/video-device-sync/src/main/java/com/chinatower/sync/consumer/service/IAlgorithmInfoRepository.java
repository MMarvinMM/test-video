package com.chinatower.sync.consumer.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chinatower.device.entity.dto.AlgoMinTypeReq;
import com.chinatower.device.entity.dto.FuncTypeReq;
import com.chinatower.sync.consumer.entity.AlgorithmInfoDO;

import java.util.List;

/**
 * <p>
 * 算法基本信息表 服务类
 * </p>
 *
 * @author AutoGen
 * @since 2022-02-16
 */
public interface IAlgorithmInfoRepository extends IService<AlgorithmInfoDO> {
    /**
     * 根据算法小类更新对应算法上的功能小类。
     * @param: reqList
     * @return: boolean
     * @author: zheng.yan
     * @date: 2022/2/17
     * @version:
     */
    int updateFuncCodeByMinTypeCode(FuncTypeReq funcTypeReq);


    /**
     * 根据功能小类批量更新对应算法上的特殊参数说名字段-ModelConfig。
     * @param: funcTypeReq
     * @return: int
     * @author: lijiwei
     * @date: 2022/12/8
     * @version:
     */
    int updateModelConfigBatch(List<FuncTypeReq> list);


    int updateFuncCodeByMinTypeCodes(List<FuncTypeReq> list);
}
