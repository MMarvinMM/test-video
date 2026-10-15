package com.chinatower.gis.modules.gis.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.gis.api.entity.layer.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @desription: 深林资源图层
 * @data 创建时间：2023/12/4 17:24
 * @author chuan hao
 * @version 1.0
 *
 */
@Mapper
public interface SlzyCeshitableMapper extends PgBaseMapper{
    /**
     *
     * @return 创建表
     */
    @DS("postgres")
    int createTable(@Param("tableName")String tableName);
    /**
     *
     * @param tableName
     * @return 创建视图
     */
    @DS("postgres")
    int createTableView(@Param("tableName")String tableName);
    /**
     * 新增
     * @author  ch
     * @date 2023/08/11
     **/
    @DS("postgres")
    int saveBatch(@Param("list") List<Map<String, Object>> list, @Param("tableName")String tableName, @Param("layerType")String layerType);
    /**
     *
     * @param tableName 表名
     * @return 获得图层总面积
     */
    @DS("postgres")
    Double getLayerArea(@Param("tableName")String tableName);
    /**
     *
     * @param req
     * @return 巡检路线查询
     */
    @DS("postgres")
    String inspectionRoute(@Param("req") GetXjxlFeaturesByIdsReq req, @Param("tableName")String tableName);
    /**
     *
     * @param req 初心参数
     * @param tableName 表名
     * @return 巡检线路数量
     */
    @DS("postgres")
    Integer inspectionRouteCount(@Param("req") GetXjxlFeaturesByIdsReq req, @Param("tableName")String tableName);

    /**
     *
     * @param tableName
     * @return 计算面积
     */
    @DS("postgres")
    int updateCalarea(@Param("tableName")String tableName);
    /**
     *
     * @param tableName 表名
     * @return 删除错误数据
     */
    @DS("postgres")
    Integer deleteBadData(@Param("tableName")String tableName);

    /**
     *
     * @return 小班列表
     */
    @DS("postgres")
    List<Map<String,Object>> forestLayerXmList(@Param("tableName")String tableName,
                                             @Param("fields") String fields,
                                             @Param("offset")Integer offset,
                                             @Param("size")Integer size,
                                             @Param("whereSql")String whereSql);

    @DS("postgres")
    Long forestLayerXmListCount(@Param("tableName")String tableName,@Param("whereSql")String whereSql);

    /**
     *
     * @param tableName 表
     * @param fieldName 统计字段
     * @return 图例统计
     */
    @DS("postgres")
    List<ForestLayerLegendRes> forestLayerLegendRes(@Param("tableName")String tableName,@Param("fieldName") String fieldName);

    /**
     *
     * @param tableName 表名
     * @param gid ID
     * @return 小班详情
     */
    @DS("postgres")
    Map<String,Object> forestLayerXmInfo(@Param("tableName")String tableName,
                                         @Param("gid") Integer gid,
                                         @Param("bsm") String bsm,
                                         @Param("allFields")String allFields);
}
