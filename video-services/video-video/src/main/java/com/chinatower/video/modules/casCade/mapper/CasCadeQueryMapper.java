package com.chinatower.video.modules.casCade.mapper;

import com.chinatower.video.api.entity.CasCadeVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CasCadeQueryMapper {
    String queryDeviceLocationCode(String deviceCode);

    String queryHornDeviceLocationCode(String deviceCode);

    String queryDeviceLocationCodeByCode(@Param("deviceCode") String deviceCode, @Param("tableName") String tableName);
    List<CasCadeVO> getCasCadeByDeviceCodeBatch(List<String> list);

    String getCasCadeByTaskId(String taskId);

    List<CasCadeVO> getCasCadeByChannelCodeBatch(List<String> list);
}
