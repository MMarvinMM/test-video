package com.chinatower.gis.modules.gis.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.gis.api.entity.layer.GetXjxlFeatureByIdsRes;
import com.chinatower.gis.api.entity.layer.GetXjxlFeaturesByIdsReq;
import com.chinatower.gis.api.entity.layer.GetXlsjFeatureRes;
import com.chinatower.gis.api.entity.layer.GetXlsjFeaturesReq;
import com.chinatower.gis.modules.gis.entity.XztbCeshitable;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @desription: 土地利用现状 模板表
 * @data 创建时间：2023/8/11 10:52
 * @author chuan hao
 * @version 1.0
 *
 */
@Mapper
public interface XztbCeshitableMapper extends PgBaseMapper{

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
    int saveBatch(@Param("list") List<Map<String, Object>> list, @Param("tableName")String tableName,@Param("layerType")String layerType);
    /**
     *
     * @param tableName
     * @return 计算面积
     */
    @DS("postgres")
    int updateCalarea(@Param("tableName")String tableName);

    /**
     * 查询 分页查询 count
     * @author ch
     * @date 2023/08/11
     **/
    @DS("postgres")
    int pageListCount(int offset,int pagesize);
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
     * @param tableName 表名
     * @return 获得图层总面积
     */
    @DS("postgres")
    Double getLayerArea(@Param("tableName")String tableName);
    /**
     *
     * @param req 请求参数
     * @param tableName 表名
     * @return 查询巡检下拉数据
     */
    @DS("postgres")
    String getXlsjFeatures(@Param("req") GetXlsjFeaturesReq req,
                                            @Param("offset") Integer offset,
                                            @Param("size") Integer size,
                                            @Param("tableName")String tableName);
    /**
     *
     * @param req 请求参数
     * @param tableName 表名
     * @return 查询巡检下拉数据总数量
     */
    @DS("postgres")
    Integer getXlsjFeaturesCount(@Param("req")GetXlsjFeaturesReq req,
                                                 @Param("tableName")String tableName);

    /**
     *
     * @param tableName 表名
     * @return 删除错误数据
     */
    @DS("postgres")
    Integer deleteBadData(@Param("tableName")String tableName);
}
