package com.chinatower.airline.mapper;

import com.chinatower.airline.dto.request.AirUavInfo;
import com.chinatower.airline.dto.request.PointsCompareTask;
import com.chinatower.airline.dto.request.UavCamera;
import com.chinatower.airline.dto.response.AirLineAlgorithms;
import com.chinatower.device.entity.dto.AirEditDto;
import com.chinatower.device.entity.dto.AirLineOperateDto;
import com.chinatower.device.entity.dto.AirTaskOperateReq;
import com.chinatower.device.entity.dto.SyncAlgorithmsReq;
import com.chinatower.device.entity.vo.AirLineVo;
import com.chinatower.device.entity.vo.TenantInfoRes;
import com.chinatower.platform.annotation.DataScope;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @Author: songxinfeng
 * @Date: 2024/11/4 10:26
 */
@Mapper
public interface AirLineMapper {
    /**
     * 新增航线信息
     *
     * @param airEditDto
     */
    void addAirLine(AirEditDto airEditDto);

    /**
     * 编辑航线信息
     *
     * @param airEditDto
     */
    void editAirLine(AirEditDto airEditDto);

    /**
     * 航线详情接口
     *
     * @param airEditDto
     * @return
     */
    AirLineVo getAirLineInfo(AirEditDto airEditDto);

    /**
     * 航线列表
     *
     * @param airEditDto
     * @return
     */
    List<AirLineVo> getAirLineList(AirEditDto airEditDto);


    /**
     * 查询租户行业下航线下拉列表sdk
     *
     * @param airEditDto
     * @return
     */
    List<AirLineVo> getAirLinesList(AirEditDto airEditDto);



    /**
     * 添加航线收藏
     *
     * @param airEditDto
     * @param userId
     */
    void addAirLineCollect(@Param("airEditDto") AirEditDto airEditDto, @Param("userId") String userId, @Param("industryCode") String industryCode);

    /**
     * 查询航线收藏列表
     *
     * @param airEditDto
     * @return
     */
    List<AirLineVo> queryAirLineCollectList(@Param("airEditDto") AirEditDto airEditDto, @Param("userId") String userId);

    /**
     * 取消航线收藏
     *
     * @param airEditDto
     * @param userId
     */
    void deleteAirLineCollect(@Param("airEditDto") AirEditDto airEditDto, String userId);

    /**
     * 删除航线
     *
     * @param airEditDto
     */
    void delAirLine(AirEditDto airEditDto);

    /**
     * 校验航线名称唯一性
     *
     * @param airEditDto
     * @return
     */
    int checkAirLineName(AirEditDto airEditDto);

    /**
     * 根据航线id查询该航线下任务是否有上传过媒体
     *
     * @param flightRouteId
     * @return
     */
    int getMediumStatusByFlightRouteId(@Param("flightRouteId") String flightRouteId);

    /**
     * 查询默认航线名称
     *
     * @param airEditDto
     * @return
     */
    String queryDefultAirLineName(AirEditDto airEditDto);

    /**
     * 校验航线是否被关联
     *
     * @param airEditDto
     * @return
     */
    List<String> checkAirLineBind(AirEditDto airEditDto);

    /**
     * 查询航线下关联算法
     *
     * @param airEditDto
     * @return
     */
    List<SyncAlgorithmsReq> getAirAlgorithm(AirEditDto airEditDto);

    /**
     * 编辑航线名称
     * @param airEditDto
     */
    void editAirLineName(AirEditDto airEditDto);

    /**
     * 删除无人机算法记录
     * @param param
     */
    void delAirAlgorithm(@Param("param") AirLineOperateDto param);

    /**
     * 删除无人机算法记录
     * @param param
     */
    void delAirAlgorithmByType(@Param("param") AirLineOperateDto param);

    /**
     * 修改无人机算法记录
     * @param param
     */
    void updateAirAlgorithm(@Param("param") AirLineOperateDto param);

    /**
     * 查询无人机设备详情
     * @param deviceCode
     * @return
     */
    String queryUavInfo(String deviceCode);

    /**
     *
     * @param airEditDto
     * @return
     */
    List<String> checkAirLineBindIsEdit(AirEditDto airEditDto);

    /**
     * 查询航线算法
     * @param airEditDto
     * @return
     */
    List<AirLineAlgorithms> queryAirLineAlgorithm(AirEditDto airEditDto);

    /**
     * 查询航线
     * @param airPointToFlyReq
     * @return
     */
    List<AirLineVo> queryFlightRoutes(AirTaskOperateReq airPointToFlyReq);

    /**
     * 根据设备编号查询无人机编号
     * @param deviceCode
     * @return
     */
    String getUavCodeByDeviceCode(String deviceCode);

    /**
     * 根据设备编号查询航线
     * @param uavCodeList
     * @return
     */
    @DataScope(tenantColumn = "t.tenant_id", industryCode = "t.industry_code")
    List<AirLineVo> getAirLineByDeviceCodes(@Param("param") AirEditDto uavCodeList);

    /**
     *
     * @param airEditDto
     * @return
     */
    AirLineVo queryAirLineInfoByFlightRouteId(AirEditDto airEditDto);

    /**
     * 查询租户下的有权限的设备
     * @param airEditDto
     * @return
     */
    @DataScope(tenantColumn = "t.tenant_id", industryCode = "t.industry_code")
    List<AirUavInfo> getDeviceCodeByTenantId(AirEditDto airEditDto);

    /**
     * 查询租户下的有权限的用户
     * @param tenantId
     * @return
     */
    List<String> queryUserIdByTenantId(@Param("tenantId")String tenantId);

    /**
     * 添加航点对比任务
     * @param tasks
     */
    void addAirPhotoCompare(List<PointsCompareTask> tasks);

    /**
     * 删除航点对比任务
     * @param id
     */
    void delAirPhotoCompare(String id);

    /**
     *
     * @param carmeCode
     * @return
     */
    UavCamera queryUavCamera(@Param("carmeCode") String carmeCode);

    /**
     *
     * @param airEditDto
     * @return
     */
    AirLineVo getAirLineInfoByTaskId(AirEditDto airEditDto);

    /**
     * 根据任务ID查询航线信息
     *
     * @param taskId 任务主键ID
     * @return 航线信息
     */
    AirLineVo queryAirLineByTaskId(@Param("taskId") String taskId);

    /**
     *
     * @param deviceCode
     * @return
     */
    AirUavInfo getDeviceInfo(String deviceCode);

    /**
     * 获取机巢通道信息
     * @param deviceCode 机巢设备编码
     * @return
     */
    List<Map<String, Object>> queryChannelInfo(@Param("deviceCode") String deviceCode);

    /**
     *
     * @param deviceCode
     * @return
     */
    List<TenantInfoRes> queryUserDeviceOrder(String deviceCode);

    /**
     * 根据航线id删除所有计划
     * @param airEditDto
     */
    void delAirPlanByFlightRoutId(AirEditDto airEditDto);

    /**
     * 根据航线id假删除所有任务
     * @param airEditDto
     */
    void delAirTaskByFlightRoutId(AirEditDto airEditDto);

    /**
     *
     * @param pointAlarmCodesList
     * @param flightRouteId
     */
    void addAirPointInfo(@Param("pointAlarmCodesList") List<AirEditDto.PointAlarmCodes> pointAlarmCodesList,
                         @Param("flightRouteId") String flightRouteId);

    /**
     *
     * @param id
     */
    void delPointInfo(String id);

    /**
     * 根据航线id查询算法信息
     * @param flightRouteId
     * @return
     */
    List<Map<String, Object>> getAlgorithmInfoByLineId(String flightRouteId);


    List<AirLineVo> getAirLinesListNologin(AirEditDto airEditDto);

    /**
     * 编辑算法任务
     * @param syncAlgorithmsReq
     */
    void editAlgorithms(SyncAlgorithmsReq syncAlgorithmsReq);

    /**
     * 校验航线是否存在
     * @param airEditDto
     */
    Boolean checkAirLineExist(AirEditDto airEditDto);

    /**
     * 根据航线ID查询航线大类
     * 0-正常航线 1-指点飞行 2-一键起飞
     *
     * @param flightRouteId 航线ID
     * @return 航线大类
     */
    Integer queryFlightMainTypeByFlightRouteId(@Param("flightRouteId") String flightRouteId);
}
