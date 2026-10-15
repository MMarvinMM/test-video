package com.chinatower.device.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chinatower.platform.entity.device.PolygonPatchAlgoAreaDO;
import com.chinatower.platform.entity.device.PolygonPatchAlgoAreaVO;
import com.chinatower.device.entity.dto.PolygonPatchConfig;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 算法与图斑识别区域关系表Mapper 接口
 * @author zhaozj
 * @since 2022-04-10
 */
public interface PolygonPatchAlgoAreaMapper extends BaseMapper<PolygonPatchAlgoAreaDO> {

    List<PolygonPatchAlgoAreaDO> getPolygonPatchAlgoAreaList(PolygonPatchAlgoAreaVO polygonPatchAlgoAreaVO);

    List<PolygonPatchAlgoAreaVO> getPolygonAlgoAreaInfo(PolygonPatchAlgoAreaVO polygonPatchAlgoAreaVO);

    int delPolygonPatchAlgoAreaBatch(@Param("list") List<String> idList);

    int addPolygonPatchAlgoAreaBatch(@Param("list") List<PolygonPatchConfig> polyList);

}
