package com.chinatower.video.modules.arButton.mapper;

import com.chinatower.video.modules.arButton.entity.ArButtonDeviceRelation;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @program: video-platform
 * @ClassName: ArbuttonDeviceRelationMapper
 * @description: mybatis持久化接口
 * @author: fengty
 * @create: 2024-05-28 18:27
 */
@Repository
public interface ArbuttonDeviceRelationMapper {

    int batchInsert(List<ArButtonDeviceRelation> relations);

    int selectCountByCondition(ArButtonDeviceRelation arButtonDeviceRelation);

    int deleteByCondition(ArButtonDeviceRelation arButtonDeviceRelation);

    List<ArButtonDeviceRelation> selectListByCondition(ArButtonDeviceRelation arButtonDeviceRelation);

    /**
     * 查询出超过配置个数限制的配置按钮deviceCode
     * @param arButtonDeviceRelation
     * @return
     */
    List<String> selectListForGroupByConditions(ArButtonDeviceRelation arButtonDeviceRelation);

    void updateByButtonId(ArButtonDeviceRelation relationUpdateParam);
}
