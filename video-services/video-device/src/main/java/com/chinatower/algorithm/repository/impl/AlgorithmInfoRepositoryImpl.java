package com.chinatower.algorithm.repository.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chinatower.industry.dto.AlgorithmInfoDO;
import com.chinatower.algorithm.mapper.AlgorithmInfoMapper;
import com.chinatower.algorithm.repository.IAlgorithmInfoRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * <p>
 * 算法基本信息表 服务实现类
 * </p>
 *
 * @author AutoGen
 * @since 2022-02-16
 */
@Service
public class AlgorithmInfoRepositoryImpl extends ServiceImpl<AlgorithmInfoMapper, AlgorithmInfoDO> implements IAlgorithmInfoRepository {

    /**
     * 功能描述: 根据算法CODE获取算法详情信息
     *
     * @param: algorithmInfoDO
     * @return: com.chinatower.common.entity.Result
     * @auther: zhaozj
     * @date: 2022/08/23
     * @version: release_v1.5
     */
    @Override
    public AlgorithmInfoDO getAlgorithmInfo(AlgorithmInfoDO algorithmInfoDO) {
        List<AlgorithmInfoDO> algorithmInfo = this.baseMapper.getAlgorithmInfo(algorithmInfoDO);
        if (!CollectionUtils.isEmpty(algorithmInfo)) {
            return algorithmInfo.get(0);
        } else {
            return new AlgorithmInfoDO();
        }
    }
}
