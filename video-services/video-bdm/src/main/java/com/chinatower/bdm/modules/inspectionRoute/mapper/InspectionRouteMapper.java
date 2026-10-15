package com.chinatower.bdm.modules.inspectionRoute.mapper;

import com.chinatower.bdm.api.entity.*;
import com.chinatower.bdm.modules.inspectionRoute.entity.*;
import com.chinatower.common.core.dto.bdm.LayerQueryDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface InspectionRouteMapper {

    List<InspectionVO> queryInspectionList(@Param("param")InspectionDTO inspectionDTO);

    List<InspectionRelVO> queryInspectionRelList(@Param("param")InspectionDTO inspectionDTO);

    List<Long> queryInspectionRelIdList(@Param("param")InspectionDTO param);

    List<LayerTreeVO> queryLayerList(@Param("param")LayerQueryDTO layerQueryDTO);

    int updateInspectionStatusWithOriginalStatus(@Param("param")InspectionStatusUpdateDTO param);

    int updateInspectionRelTimeAndStatusWithOriginalStatus(@Param("param") InspectionRelStatusUpdateDTO param);

    InspectionRelInfoVO queryFirstTaskInfo(@Param("param") InspectionRouteProcessParam param);

    int updateOtherRouteRelStatus(@Param("param") InspectionRouteProcessParam param,
                                  @Param("routeStatus") int routeStatus);

    int finishInspectionInfo(@Param("param") InspectionRouteProcessParam param);

    int finishInspectionRelInfo(@Param("param") InspectionRouteProcessParam param);

    int recoverInspectionRelStatus(@Param("param") InspectionRouteProcessParam param);

    int resetInspectionRelStatus(@Param("param") InspectionRouteProcessParam param);

    InspectionRelInfoVO selectInspectionRelById(@Param("id")Long id);

    InspectionRelBriefInfoVO selectInspectionRelBriefInfoById(@Param("id")Long id);

    InspectionRelBriefInfoVO selectWorkRelBriefInfoByRouteId(@Param("id")Long id);

    InspectionRelInfoVO selectInspectionRelByIdWithPermission(@Param("param") InspectionRouteProcessParam param);

    InspectionRelInfoVO selectInspectionRelByRouteIdAndStatus(@Param("inspectionRouteId")Long inspectionRouteId, @Param("routeStatus") Integer routeStatus);

    List<InspectionRoutePage4SdkVO> queryInspectionRoutePage4Sdk(@Param("param") InspectionRoutePage4SdkParam param);

    List<InspectionFeaturePlotVO> queryFeaturePlotInfo(@Param("list") List<Long> idList, @Param("dataPermissions")String dataPermissions);

    List<PolygonPatchConfigVO> queryPolygonPathInfo(@Param("param") PolygonPatchConfigDTO polygonPatchConfigDTO);

    int selectInspectionByName(@Param("param")InspectionDTO inspectionDTO);

    void insertInspectionRoute(@Param("param")InspectionVO inspectionVO);

    void insertPolygonPatchBatch(@Param("list") List<PolygonPatchConfigVO> patchConfigVOList);

    void insertInspectionRelList(@Param("list")List<InspectionRelVO> inspectionRelVOList);

    int selectMaxPolygonNo(@Param("param")PolygonPatchConfigVO polygonPatchConfigVO);


    /**
     * 查询巡检路线信息
     * @param param
     * @return
     */
    InspectionVO selectRouteById(@Param("param")InspectionRouteProcessParam param);

    /**
     * 根据巡查路线id查询该巡查路线下巡检点信息
     * @param param
     * @return
     */
    InspectionRelInfoVO findNext(@Param("param")InspectionRelInfoVO param);

    /**
     * 查询巡检点下地块信息
     * @param param
     * @return
     */
    List<InspectionRouteRelLayerSdkVO> queryInspectionLayer(@Param("param") InspectionRouteRelLayerPageSdkParam param);

    void deleteInspectionRoute(@Param("param")InspectionDTO inspectionDTO);

    void deleteInspectionRelInfo(@Param("param")InspectionDTO inspectionDTO);

    String createInspectionName(String tenantId,String industryCode);

    InspectionVO selectInspectionById(Long id,String tenantId,String industryCode);

    void updateInspectionInfo(@Param("param") InspectionVO inspectionVO);

    void deleteInspectionRelById(@Param("list")List<Long> relIdList);

    void updateInspectionRelBatch(@Param("list")List<InspectionRelVO> inspectionRelVOList);

    int queryInspectionByPolygonId(@Param("param")InspectionRelVO inspectionRelVO);

    List<PolygonPatchConfigVO> queryPolygonList(@Param("param") PolygonPatchConfigDTO dto);

    int getInspectionRouteNum(@Param("tenantId")String tenantId, @Param("industryCode")String industryCode,
                              @Param("createTimeBegin")String createTimeBegin,
                              @Param("createTimeEnd")String createTimeEnd);

    int getInspectionPolygonNum(@Param("tenantId")String tenantId, @Param("industryCode")String industryCode,
                                @Param("createTimeBegin")String createTimeBegin,
                                @Param("createTimeEnd")String createTimeEnd);

    int initRouteRecord(@Param("param") InspectionRouteRecordDTO param);

    InspectionRouteRecordDTO getLatestRouteInfoByStatus(@Param("routeId") Long routeId, @Param("routeStatus") Integer routeStatus);

    int updateRouteRecord(@Param("param") InspectionRouteRecordDTO param);

    InspectionRouteRecordVO queryInspectionCountInfo(@Param("tenantId")String tenantId,
                                                     @Param("industryCode")String industryCode,
                                                     @Param("startTime") String startTime,
                                                     @Param("endTime")String createTimeEnd);
    InspectionRouteRecordVO queryInspectionRecordInfo(@Param("tenantId")String tenantId,
                                                      @Param("industryCode")String industryCode,
                                                      @Param("startTime")String startTime,
                                                      @Param("endTime")String endTime);

    int selectValidRouteCount(@Param("tenantId")String tenantId, @Param("industryCode")String industryCode);

    List<InspectionRelVO> queryDurationInfoByRouteIdList(@Param("inspectionRouteIdList")List<Long> inspectionRouteIdList);
}
