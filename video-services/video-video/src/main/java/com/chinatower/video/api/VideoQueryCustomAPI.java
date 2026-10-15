package com.chinatower.video.api;

import com.chinatower.common.entity.Result;
import com.chinatower.video.api.entity.*;
import com.chinatower.video.modules.query.entity.HabitVO;
import com.chinatower.video.modules.query.vo.AlgoPresentVo;
import com.chinatower.video.modules.query.vo.QueryPresentPointVo;
import com.github.pagehelper.PageInfo;

import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @ClassName: VideoQueryCustomAPI
 * @Description: 查询功能相关接口
 * @Author: ldl
 * @Date: 2022/1/27
 **/
public interface VideoQueryCustomAPI {
    /**
     * 查询权限
     * @param queryRoleReq
     * @return
     */
    @PostMapping("/video/query/selectRoleById")
    Result selectRoleById(@RequestBody QueryRoleReq queryRoleReq);
    /**
     * 设置用户操作习惯
     * @param habitVO
     * @return
     */
    @PostMapping("/video/query/setUserHabit")
    Result setUserHabit(@RequestBody HabitVO habitVO);
    /**
     * 获取用户操作习惯
     * @param habitVO
     * @return
     */
    @PostMapping("/video/query/getUserHabit")
    Result getUserHabit(@RequestBody HabitVO habitVO);
    /**
     *查询通道录像计划
     * @param videoBaseReq
     * @return
     */
    @PostMapping("/video/query/queryRecordPlan")
    Result queryRecordPlan(@RequestBody VideoBaseReq videoBaseReq);
    /**
     *查询全部录像计划
     * @param channelVideoPlanVO
     * @return
     */
    @PostMapping("/video/query/queryRecordPlanForPage")
    Result queryRecordPlanForPage(@RequestBody VideoChannelVideoPlanVO channelVideoPlanVO);
    /**
     *6.19.查询PTZ位置
     * @param videoBaseReq
     * @return
     */
    @PostMapping("/video/query/queryPtzPosition")
    Result<PresetData> queryPtzPosition(@RequestBody VideoBaseReq videoBaseReq);
    /**
     *6.26.查询预置点列表
     * @param videoBaseReq
     * @return
     */
    @PostMapping("/video/query/queryPresetList")
    Result<List<QueryPresetListData>> queryPresetList(@RequestBody VideoBaseReq videoBaseReq);
    /**
     *6.28.查询看守位
     * @param videoBaseReq
     * @return
     */
    @PostMapping("/video/query/queryGuardList")
    Result<QueryGuardListData> queryGuardList(@RequestBody VideoBaseReq videoBaseReq);
    /**
     *6.34.查询巡航路径
     * @param videoQueryPatrol
     * @return
     */
    @PostMapping("/video/query/queryPatrol")
    Result<List<QueryPatrolData>> queryPatrol(@RequestBody VideoQueryPatrolData videoQueryPatrol);

    /**
     *6.34.分页查询巡航路径
     * @param videoQueryPatrol
     * @return
     */
    @PostMapping("/video/query/queryPatrolPage")
    Result<PageInfo<QueryPatrolData>> queryPatrolPage(@RequestBody VideoQueryPatrolData videoQueryPatrol);

    /**
     *获取视频格式字典表，用于切换
     * @param queryGetEnumInfoReq
     * @return
     */
    @PostMapping("/video/query/getEnumInfo")
    Result getEnumInfo(@RequestBody QueryGetEnumInfoReq queryGetEnumInfoReq);

    /**
     * 功能说明:  实时视频显示电子围栏 根据设备获取PTZ数据,再转换百分比坐标
     * @param queryPresentPointVo deviceCode  channelCode
     * @return 实时百分比坐标点
     * @author yy
     * @date 2022年10月10日16:29:42
     * @requestID  R-ZBZB-202210080955738 实时视频显示电子围栏
     */
    @PostMapping("/video/query/percentPoints")
    Result queryPercentPoints(@RequestBody QueryPresentPointVo queryPresentPointVo);


    /**
     * 历史数据处理：对于配置了摄像机厂家和型号的摄像机的预置位算法任务，系统升级时进行处理，将百分比坐标转换为PTZ坐标 并且存储入库
     * @return 统一返回
     */
    @GetMapping("/updateHistoryPoints")
    Result updateHistoryPoints();

    @PostMapping("/video/query/getAlgoPresent")
    Result<List<AlgoPresentVo>> getAlgoPresent(@RequestBody List<AlgoPresentVo> list);


    /**
     * 封装PTZ SDK
     * @param videoBaseReq
     * @return
     */
    @PostMapping("/video/query/queryPtzSdk")
    Result<PresetData> queryPtzSdk(@RequestBody VideoBaseReq videoBaseReq);

    @PostMapping("/video/query/queryPtzSdkTest")
    Result<PresetData> queryPtzSdkTest(@RequestBody VideoBaseReq videoBaseReq);

    @PostMapping("/video/query/checkDeviceIsAuth")
    Result checkDeviceIsAuth(@RequestBody VideoBaseReq videoBaseReq);

    @PostMapping("/video/query/getUserHabitBatch")
    Result getUserHabitBatch(@RequestBody HabitVO habitVO);

}
