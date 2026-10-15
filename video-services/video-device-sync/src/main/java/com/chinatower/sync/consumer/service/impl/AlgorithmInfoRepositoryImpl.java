package com.chinatower.sync.consumer.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chinatower.device.entity.dto.AlgoMinTypeReq;
import com.chinatower.device.entity.dto.FuncTypeReq;
import com.chinatower.sync.consumer.dao.AlgorithmInfoMapper;
import com.chinatower.sync.consumer.entity.AlgorithmInfoDO;
import com.chinatower.sync.consumer.service.IAlgorithmInfoRepository;
import org.springframework.stereotype.Service;

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

    @Override
    public int updateFuncCodeByMinTypeCode(FuncTypeReq funcTypeReq) {
        return this.baseMapper.updateFuncCodeByMinTypeCode(funcTypeReq);
    }

    /**
     * 根据功能小类批量更新对应算法上的特殊参数说名字段-ModelConfig。
     * @param: funcTypeReq
     * @return: int
     * @author: lijiwei
     * @date: 2022/12/8
     * @version:
     */
    @Override
    public int updateModelConfigBatch(List<FuncTypeReq> list){
        return this.baseMapper.updateModelConfigBatch(list);
    }

    @Override
    public int updateFuncCodeByMinTypeCodes(List<FuncTypeReq> list) {
        return this.baseMapper.updateFuncCodeByMinTypeCodes(list);
    }

}
