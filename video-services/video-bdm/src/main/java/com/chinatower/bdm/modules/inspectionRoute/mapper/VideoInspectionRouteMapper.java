package com.chinatower.bdm.modules.inspectionRoute.mapper;

import com.chinatower.bdm.api.entity.*;
import com.chinatower.bdm.modules.inspectionRoute.entity.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface VideoInspectionRouteMapper {

    List<Long> queryInspectionRelIdList(@Param("param")VideoInspectionDTO param);

    int updateInspectionStatusWithOriginalStatus(@Param("param")InspectionStatusUpdateDTO param);

    int updateInspectionRelTimeAndStatusWithOriginalStatus(@Param("param") InspectionRelStatusUpdateDTO param);

    VideoInspectionRelVO queryFirstTaskInfo(@Param("param") VideoInspectionRouteProcessParam param);

    int updateOtherRouteRelStatus(@Param("param") VideoInspectionRouteProcessParam param,
                                  @Param("routeStatus") int routeStatus);

    int finishInspectionInfo(@Param("param") VideoInspectionRouteProcessParam param);

    int finishInspectionRelInfo(@Param("param") VideoInspectionRouteProcessParam param);

    int recoverInspectionRelStatus(@Param("param") VideoInspectionRouteProcessParam param);

    int resetInspectionRelStatus(@Param("param") VideoInspectionRouteProcessParam param);

    VideoInspectionRelVO selectInspectionRelById(@Param("id")Long id);

    VideoInspectionRelVO selectInspectionRelByIdWithPermission(@Param("param")VideoInspectionRouteProcessParam param);

    VideoInspectionRelBriefInfoVO selectInspectionRelBriefInfoById(@Param("id")Long id);

    VideoInspectionRelBriefInfoVO selectWorkRelBriefInfoByRouteId(@Param("id")Long id);

    VideoInspectionRelVO selectInspectionRelByRouteIdAndStatus(@Param("inspectionRouteId")Long inspectionRouteId, @Param("routeStatus") Integer routeStatus);

    List<VideoInspectionVO> queryList(@Param("param") VideoInspectionDTO param);

    List<VideoInspectionRelVO> queryDurationInfoByRouteIdList(@Param("inspectionRouteIdList")List<Long> inspectionRouteIdList);

    int selectInspectionByName(@Param("param")VideoInspectionDTO param);

    void insertInspectionRoute(@Param("param")VideoInspectionVO param);

    void insertInspectionRelList(@Param("list")List<VideoInspectionRelVO> list);

    /**
     * 查询巡检路线信息
     * @param param
     * @return
     */
    VideoInspectionVO selectRouteById(@Param("param")VideoInspectionRouteProcessParam param);

    /**
     * 根据巡查路线id查询该巡查路线下巡检点信息
     * @param param
     * @return
     */
    VideoInspectionRelVO findNext(@Param("param")VideoInspectionRelVO param);

    /**
     * 查询巡检点下地块信息
     * @param param
     * @return
     */
    List<VideoInspectionRelVO> queryRelList(@Param("param") VideoInspectionRelDTO param);

    void deleteInspectionRoute(@Param("param")VideoInspectionDTO param);

    void deleteInspectionRelInfo(@Param("param")VideoInspectionDTO param);

    String createInspectionName(@Param("tenantId")String tenantId, @Param("industryCode")String industryCode);

    VideoInspectionVO selectInspectionById(@Param("id")Long id, @Param("tenantId")String tenantId, @Param("industryCode")String industryCode);

    void updateInspectionInfo(@Param("param") VideoInspectionVO param);

    void deleteInspectionRelById(@Param("list")List<Long> list);

    void updateInspectionRelBatch(@Param("list")List<VideoInspectionRelVO> list);

    int selectValidRouteCount(@Param("tenantId")String tenantId, @Param("industryCode")String industryCode);

    int initRouteRecord(@Param("param") VideoInspectionRouteRecordDTO param);

    VideoInspectionRouteRecordDTO getLatestRouteInfoByStatus(@Param("routeId") Long routeId, @Param("routeStatus") Integer routeStatus);

    int updateRouteRecord(@Param("param") VideoInspectionRouteRecordDTO param);

    VideoInspectionRouteRecordVO queryInspectionCountInfo(@Param("tenantId")String tenantId,
                                                     @Param("industryCode")String industryCode,
                                                     @Param("startTime") String startTime,
                                                     @Param("endTime")String createTimeEnd);
    VideoInspectionRouteRecordVO queryInspectionRecordInfo(@Param("tenantId")String tenantId,
                                                      @Param("industryCode")String industryCode,
                                                      @Param("startTime")String startTime,
                                                      @Param("endTime")String endTime);

    List<VideoInspectionVO> queryInspectionList(@Param("param")VideoInspectionDTO param);
}
