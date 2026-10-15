package com.chinatower.sync.consumer.dao;

import com.chinatower.sync.consumer.item.UavCameraModelReport;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 获取全量相机型号列表
 */
@Mapper
public interface UavCameraModelMapper {

    List<String> getAllUavCameraModelCode();

    int insertUavCameraModelBatch(@Param("list") List<UavCameraModelReport> insertList);

    int deleteUavCameraModelBatch(@Param("deleteIdList") List<Long> deleteIdList);

    int updateUavCameraModelBatch(@Param("list") List<UavCameraModelReport> updateList);

    List<Long> getAllUavCameraModelYgId();

    List<String> queryUavCameraModelCode(@Param("deleteIdList") List<Long> deleteIdList);
}
