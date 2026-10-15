package com.chinatower.algorithm.repository;

import com.chinatower.industry.dto.AlgorithmInfoDO;
import com.baomidou.mybatisplus.extension.service.IService;

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
     * 功能描述: 根据算法CODE获取算法详情信息
     *
     * @param: algorithmInfoDO
     * @return: com.chinatower.common.entity.Result
     * @auther: zhaozj
     * @date: 2022/08/23
     * @version: release_v1.5
     */
    AlgorithmInfoDO getAlgorithmInfo(AlgorithmInfoDO algorithmInfoDO);

}
