package com.chinatower.airline.service;

import com.chinatower.airline.dto.request.AirPointToFlyReq;
import com.chinatower.airline.dto.request.UavRealTimeAlgorithmReq;
import com.chinatower.airline.dto.response.AirDeviceInfoRep;
import com.chinatower.common.entity.Result;
import com.chinatower.device.entity.dto.*;
import com.chinatower.device.entity.vo.AirLineVo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @Author: songxinfeng
 * @Date: 2024/11/4 10:26
 */
public interface AirLineService {

    /**
     * 获取无人机算法配置
     *
     * @param alarmTypeDTO
     * @return
     */
    Result getAirAlarmType(AirAlarmTypeReq alarmTypeDTO);

    /**
     * 新增/编辑航线（包含上传文件）
     *
     * @param airEditDto
     * @return
     */
    Result editAirLine(AirEditDto airEditDto, MultipartFile kmzFile);

    /**
     * 航线详情接口
     *
     * @param airEditDto
     * @return
     */
    Result getAirLineInfo(AirEditDto airEditDto);

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
     * @param airEditDto
     * @return
     */
    Result addAirLineCollect(AirEditDto airEditDto);

    /**
     * @param airEditDto
     * @return
     */
    List<AirLineVo> queryAirLineCollectList(AirEditDto airEditDto);

    /**
     * @param airEditDto
     * @return
     */
    Result deleteAirLineCollect(AirEditDto airEditDto);

    /**
     * 修改航线名称
     *
     * @param airEditDto
     * @return
     */
    Result editAirLineName(AirEditDto airEditDto);

    /**
     * 删除航线
     *
     * @param airEditDto
     * @return
     */
    Result delAirLine(AirEditDto airEditDto);

    /**
     * 新增航线临时算法
     *
     * @param airEditDto
     * @return
     */
    Result addTempAlgorithm(AirEditDto airEditDto);

    /**
     * 校验航线名称唯一性
     *
     * @param airEditDto
     * @return
     */
    Result checkAirLineName(AirEditDto airEditDto);

    /**
     * 获取设备详细信息
     *
     * @param airDeviceInfoDto
     * @return
     */
    AirDeviceInfoRep getAirDeviceInfo(AirDeviceInfoReq airDeviceInfoDto);

    /**
     * 查询默认航线名称
     *
     * @return
     */
    Result queryDefultAirLineName(AirEditDto airEditDto);

    /**
     * 校验航线是否被关联
     *
     * @param airEditDto
     * @return
     */
    Result checkAirLineBind(AirEditDto airEditDto);

    /**
     * 复制航线
     *
     * @param airEditDto
     * @return
     */
    Result copyAirLine(AirEditDto airEditDto);

    /**
     * 获取航点航线详细信息(航线规划使用)
     *
     * @param airEditDto
     * @return
     */
    Result getPointInfo(AirEditDto airEditDto);

    /**
     * 上传KMZ文件
     * @param kmzFile
     * @return
     */
    Result uploadKmzFile(MultipartFile kmzFile);

    /**
     * 点飞
     * @param airPointToFlyReq
     * @return
     */
    Result pointToFly(AirPointToFlyReq airPointToFlyReq);

    /**
     * 一键返航
     * @param airPointToFlyReq
     * @return
     */
    Result flyReturn(AirPointToFlyReq airPointToFlyReq);

    /**
     *
     * @param airEditDto
     * @return
     */
    Result queryAirLineAlgorithm(AirEditDto airEditDto);

    /**
     *
     * @param airPointToFlyReq
     * @return
     */
    Result immediatelyFly(AirPointToFlyReq airPointToFlyReq);

    /**
     * 退订设备后 关闭设备下的航线任务和算法任务
     * @param airPointToFlyReq
     * @return
     */
    Result closeAirTaskAndAlgorithms(AirTaskOperateReq airPointToFlyReq);

    /**
     * 续订设备后 重新开启设备下的航线任务和算法任务
     * @param airPointToFlyReq
     * @return
     */
    Result openAirTaskAndAlgorithms(AirTaskOperateReq airPointToFlyReq);

    /**
     * 点飞批量
     * @param airPointToFlyReq
     * @return
     */
    Result pointToFlyBatch(List<AirPointToFlyReq> airPointToFlyReq);

    /**
     * 查询设备下的航线
     * @param airEditDto
     * @return
     */
    Result getAirLineByDeviceCodes(AirEditDto airEditDto);

    /**
     *
     * @param airEditDto
     * @return
     */
    Result reFly(AirEditDto airEditDto,MultipartFile kmzFile);

    /**
     * 一键返航批量
     * @param airPointToFlyReq
     * @return
     */
    Result flyReturnBatch(List<AirPointToFlyReq> airPointToFlyReq);

    /**
     * 查询航线节点
     * @param airEditDto
     * @return
     */
    Result getAirLineInfoByFlightRouteId(AirEditDto airEditDto);

    /**
     *
     * @param telemetryReq
     * @return
     */
    Result queryUserDeviceOrder(TelemetryReq telemetryReq);

    /**
     * 新指点飞行
     * @param airPointToFlyReq
     * @return
     */
    Result pointToFlyNew(AirPointToFlyReq airPointToFlyReq,MultipartFile kmzFile);

    /**
     * 根据任务ID查询航线信息
     *
     * @param airTaskReq 任务查询请求对象
     * @return 航线信息结果
     */
    Result<AirLineVo> queryAirLineByTaskId(AirTaskReq airTaskReq);

    /**
     * 根据航线ID查询算法信息
     *
     * @param flightRouteId 航线ID
     * @return 算法信息结果
     */
    Result getAlgorithmInfoByLineId(String flightRouteId);

    /**
     * 查询航线列表
     * @param airEditDto
     * @return
     */
    List<AirLineVo> getAirLinesListNologin(AirEditDto airEditDto);


    /**
     * 无人机实时航线算法任务下发
     * @param uavRealTimeAlgorithmReq
     * @return
     */
    Result uavRealTimeAlgorithm(UavRealTimeAlgorithmReq uavRealTimeAlgorithmReq);
}
