package com.chinatower.receive.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 告警分级：行业-算法-场景模板关系
 */
@Mapper
public interface AiJudgeSceneRelMapper {

    /**
     * 根据行业编码 + 算法编码查询场景模板；查不到表示未配置，不触发外呼
     *
     * @param industryCode  行业编码
     * @param algorithmCode 算法编码
     * @return scene_template，未配置返回 null
     */
    @DS(EbikeDataBaseConstant.BASE)
    String selectSceneTemplate(@Param("industryCode") String industryCode,
                               @Param("algorithmCode") String algorithmCode);
}
