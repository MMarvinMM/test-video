package com.chinatower.stdu.common.dao;

import com.chinatower.video.api.entity.VideoBaseReq;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: VideoCommonDAO
 * @Description: 视频控制公共DAO
 * @Author: zhujun
 * @Date: 2022-01-13
 **/
@Mapper
public interface VideoCommonDAO {

    List<Map<String, String>> queryOrderDeviceByTableName(@Param("param") VideoBaseReq param, @Param ("tableName") String tableName);

    List<Map<String, String>> queryOrderDeviceByTableNameNoAuth(@Param("param") VideoBaseReq param, @Param ("tableName") String tableName);

    String getErrorMsg(@Param("dictValue") String dictValue);


}
