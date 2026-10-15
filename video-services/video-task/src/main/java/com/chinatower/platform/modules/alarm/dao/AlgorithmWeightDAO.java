package com.chinatower.platform.modules.alarm.dao;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import com.chinatower.platform.modules.alarm.entity.VmAlgorithmWeightDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
@DS(EbikeDataBaseConstant.BASE)
public interface AlgorithmWeightDAO {

    /**
     * 批量更新算法权重
     * @param list
     * @return
     */
    int batchUpdateWeights(@Param("list") List<VmAlgorithmWeightDO> list);


    /**
     * 批量存在更新，不存在插入
     */
    int batchUpsert(@Param("list") List<VmAlgorithmWeightDO> list);

    /**
     * 查询算法列表
     * @param list
     * @return
     */
    List<VmAlgorithmWeightDO> selectAlgorithmCodeList(@Param("list") List<String> list);
}
