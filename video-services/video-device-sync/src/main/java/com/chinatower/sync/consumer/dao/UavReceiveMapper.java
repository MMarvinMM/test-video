package com.chinatower.sync.consumer.dao;

import com.chinatower.device.entity.dto.AirLinePointTaskReq;
import com.chinatower.platform.entity.admin.UserInfoDto;
import com.chinatower.sync.consumer.entity.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author WLF
 * @description
 * @date 2024/11/10 16:27
 */
@Mapper
public interface UavReceiveMapper {
    /**
     * @param uavStatusRequest
     */
    void receiveAirLineTaskStatus(UavStatusRequest uavStatusRequest);

    /**
     *
     * @param request
     */
    void editAirTaskStatus(UavAlgorithmRequest request);

    /**
     * 查询有效临时算法
     * @param floutId
     * @return
     */
    List<TempAlgorithmResp> selectTempAlgorithm(@Param("floutId") String floutId);

    /**
     * 查询有效正常待恢复算法
     * @param floutId
     * @return
     */
    List<TempAlgorithmResp> selectOneAlgorithm(@Param("floutId") String floutId);

    /**
     * 插入算发表数据
     * @param dto
     */
    void insertAirAlgorithm(SyncAlgorithmsDto dto);

    /**
     * 删除算法表数据
     * @param floutId
     */
    void delAirAlgorithm(@Param("floutId") String floutId);

    /**
     * 查询航线id根据任务id
     * @param taskRecordId
     * @return
     */
    String queryAirLineByTaskId(@Param("taskRecordId")String taskRecordId);

    /**
     * 正常待恢复算法
     * @param floutId
     */
    void updateAirAlgorithmNormal(String floutId);

    /**
     * 查询uavId根据设备编号
     * @param deviceCode
     * @return
     */
    AirUavStatus queryUavByDeviceCode(String deviceCode);

    /**
     * 查询无人机告警策略信息
     * @param deviceCodeList 无人机编码列表
     * @return 无人机告警策略信息
     */
    List<UavAlarmStrategyVO> selectUavAlarmStrategyInfo(@Param("deviceCodeList") List<String> deviceCodeList);

    /**
     * 查询用户信息
     * @param list
     * @return
     */
    List<UserInfoDto> queryUserInfo(List<String> list);

    /**
     * 插入无人机失联告警信息
     * @param uavAlarmInfo
     */
    void insertUavAlarmInfo(List<UavAlarmInfo> uavAlarmInfo);

    /**
     * 删除无人机失联告警信息
     * @param deviceCode
     */
    int deleteUavAlarmInfo(String deviceCode);

    /**
     * 查询无人机失联告警信息
     * @param deviceCode
     * @return
     */
    List<UavAlarmInfo> queryUavAlarmInfo(String deviceCode);

    /**
     * 查询无人机信息
     * @param deviceCode
     * @return
     */
    AirUavStatus queryUavByUavCode(String deviceCode);

    /**
     *
     * @param taskRecordId
     */
    AirUavStatus queryTaskById(String taskRecordId);

    /**
     *
     * @param originalTaskRecordIds
     */
    void editTaskType(String originalTaskRecordIds);

    /**
     * 查询照片任务信息
     * @param taskRecordId
     * @return
     */
    List<PointsCompareTask> queryPhotoTaskInfo(String taskRecordId);

    /**
     * 插入照片任务信息
     * @param list
     */
    void insertAirLinePhotoTask(List<AirLinePhotoTask> list);

    /**
     * 查询任务信息
     * @param airLinePhotoTask
     * @return
     */
    AirUavStatus queryTask(PointsCompareTask airLinePhotoTask);

    /**
     * 查询任务信息
     * @param flightRouteId
     * @param todayStr
     * @return
     */
    List<String> queryTaskByAirLineId(@Param("flightRouteId") String flightRouteId,@Param("todayStr") String todayStr);

    /**
     * 查询航点算法配置任务
     *
     * @param taskRecordId
     * @return
     */
    List<AirLinePointTaskReq> queryPointInfo(String taskRecordId);
}
