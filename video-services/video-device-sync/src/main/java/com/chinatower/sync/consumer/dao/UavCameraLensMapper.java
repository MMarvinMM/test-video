package com.chinatower.sync.consumer.dao;

import com.chinatower.sync.consumer.item.UavCameraParamReport;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UavCameraLensMapper {
    void insertUavCameraLensBatch(@Param("list") List<UavCameraParamReport> allLenList);

    void deleteUavCameraLensBatch();
}
