package com.chinatower.airline.mapper;

import com.chinatower.airline.dto.request.*;
import com.chinatower.airline.dto.request.AirResultTaskReq;
import com.chinatower.airline.dto.response.*;
import com.chinatower.device.entity.dto.*;
import com.chinatower.device.entity.vo.AirLineVo;
import com.chinatower.platform.annotation.DataScope;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 *
 */
@Mapper
public interface AirResultMapper {

    /**
     * @param airResultTaskReq
     * @return
     */
    List<AirResultTaskRep> queryTaskList(AirResultTaskReq airResultTaskReq);

    /**
     * 无人机树默认选中有最新执行任务的设备
     *
     * @return
     */
    String queryDefultUav(AirUavDeviceReq airUavDeviceReq);

    /**
     * @param taskRecordId
     * @return
     */
    AirTaskVideoRep queryInfo(String taskRecordId);

    /**
     * @return
     */
    @DataScope(tenantColumn = "t.tenant_id", industryCode = "t.industry_code", deviceColumn = "t.uav_code")
    List<String> queryFlightIds(AirResultTaskReq airResultTaskReq);

    /**
     *
     */
    String queryUavInfoByTaskId(String taskRecordId);

    /**
     * 新增视频抽帧信息
     *
     * @param videoExtractRep
     */
    void addVideoExtraction(AirVideoExtractRep videoExtractRep);

    /**
     * 查询抽帧列表
     *
     * @param videoExtractReq
     * @return
     */
    List<AirVideoExtractRep> queryVideoExtractionList(AirVideoExtractReq videoExtractReq);

    /**
     * 根据抽帧任务删除视频抽帧
     *
     * @param taskReq
     */
    void delVideoExtraction(AirVideoExtractTaskReq taskReq);

    /**
     * 取消抽帧
     *
     * @param taskReq
     */
    void cancelExtractTask(AirVideoExtractTaskReq taskReq);

    /**
     * @param planTaskId
     * @return
     */
    String queryTaskNode(String planTaskId);

    /**
     * 插入采集结果
     *
     * @param airResultReq
     * @return
     */
    int addCollectResult(List<AirResultCollectReq> airResultReq);

    /**
     * 删除采集结果
     *
     * @param
     * @return
     */
    int delCollectResult(List<String> airResultReq, @Param("userId") String userId);

    /**
     * 查询采集结果列表
     *
     * @return
     */
    List<AirTaskPhotoRep> queryCollectResultList(AirResultReq airResultReq);

    /**
     * 查询采集结果状态
     *
     * @param mappedList
     * @return
     */
    List<AirResultCollectReq> queryCollectStatus(List<String> mappedList, String userId);

    /**
     * 更新采集结果名称
     *
     * @param airResultRenameReq
     */
    void updateCollectName(AirResultRenameReq airResultRenameReq);

    /**
     * 查询任务信息
     *
     * @param taskRecordId
     * @return
     */
    String queryUavInfo(String taskRecordId);

    /**
     * @return
     */
    List<AirResultTaskReq> queryTaskReFly();

    /**
     * @return
     */
    List<AirResultTaskReq> queryTaskNoReFly();

    /**
     * @return
     */
    @DataScope(tenantColumn = "t.tenant_id", industryCode = "t.industry_code", deviceColumn = "t.uav_code")
    List<AirResultTaskReq> queryReqTaskRecordIds(AirResultTaskReq airResultTaskReq);

    /**
     * 查询任务信息
     *
     * @param taskRecordId
     * @return
     */
    List<AirTaskRes> queryReFlightTask(String taskRecordId);

    /**
     * @param idList
     * @return
     */
    List<String> queryReFlyStatus(List<String> idList);

    /**
     * @param
     * @return
     */
    List<String> queryReFlyTask(String photoId);

    /**
     * @param taskRecordId
     * @return
     */
    String queryReOrgFlyTask(String taskRecordId);

    /**
     * 查询无人机详情
     *
     * @param uavFlyReq
     */
    AirUavInfo queryUavInfoByDeviceCode(UavFlyReq uavFlyReq);

    /**
     * 根据任务id查询复飞源任务id
     * @param mappedList
     * @return
     */
    List<AirTaskRes> queryReFlyTaskIds(List<String> mappedList);

    /**
     * 查询任务信息
     *
     * @param airResultReq
     */
    List<AirTaskRes> queryTaskPhotoVideoInfo(QueryTaskPhotoVideoReq airResultReq);

    /**
     * 查询任务信息
     * @param taskRecordId
     * @return
     */
    List<String> queryReFlyTaskByOrgTaskId(String taskRecordId);

    /**
     *
     * @param
     */
    void updateCollectStatus(List<String> ids,String dataType);

    /**
     * 查询无人机
     * @param taskRecordId
     */
    AirUavInfo queryUavByDeviceCode(String taskRecordId);

    /**
     * 航线任务
     * @param airResultTaskReq
     * @return
     */
    @DataScope(tenantColumn = "t.tenant_id", industryCode = "t.industry_code", deviceColumn = "t.uav_code")
    List<String> queryTaskIds(AirResultTaskReq airResultTaskReq);

    /**
     * 航线任务信息查询
     */
    @DataScope(tenantColumn = "t.tenant_id", industryCode = "t.industry_code", deviceColumn = "lt.uav_code")
    List<AirResultTaskListRep> queryTaskInfoList(AirResultReq airResultReq);

    /**
     * 查询对照点的照片信息
     * @param correspondPointReq
     * @return
     */
    @DataScope(tenantColumn = "t.tenant_id", industryCode = "t.industry_code")
    List<AirCorrespondPhotoRep> queryCorrespondPhotoList(CorrespondPointReq correspondPointReq);

    /**
     *
     * @param photoIds
     * @return
     */
    List<AirCollectPhotoRep> queryCollectPhotos(List<String> photoIds,String userId,String industryCode);

    /**
     *
     * @param queryPhotosTypesReq
     * @return
     */
    List<QueryPhotosTypesRep> queryCollectPhotoByUserId(QueryPhotosTypesReq queryPhotosTypesReq);

    /**
     * 查询航线信息
     * @param flightRouteIds
     * @return
     */
    List<AirTaskRes> queryLineRouteIds(List<String> flightRouteIds);

    /**
     *
     * @param taskRecordId
     * @return
     */
    List<UavInfo> queryUavInfoByTaskRecordId(List<String> taskRecordId);

    /**
     * 根据登陆用户信息查询收藏的成果物的任务id集合
     * @param queryClollectReq
     * @return
     */
    List<String> queryCollectTaskIds(QueryCollectReq queryClollectReq);
}
