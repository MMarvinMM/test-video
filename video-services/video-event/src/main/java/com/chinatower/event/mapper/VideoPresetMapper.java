package com.chinatower.event.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import com.chinatower.video.api.entity.VideoPresetDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 预置点信息表 Mapper 接口
 * </p>
 *
 * @author yhb
 * @since 2022-12-22
 */
@Mapper
@DS(EbikeDataBaseConstant.BASE)
public interface VideoPresetMapper {

    VideoPresetDO selectVideoPreById(@Param("presetId") String presetId,@Param("deviceCode") String deviceCode);
}

