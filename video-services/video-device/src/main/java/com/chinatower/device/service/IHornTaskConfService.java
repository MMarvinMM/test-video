package com.chinatower.device.service;

import com.chinatower.common.entity.Result;
import com.chinatower.device.entity.vo.HornTaskConfVO;
import com.chinatower.platform.entity.admin.SysUser;

import java.util.List;

/**
 * 广播任务管理接口类
 */
public interface IHornTaskConfService {


    /**
     * 分页查询广播任务列表
     *
     * @param hornTaskConfVO 广播任务配置VO对象
     * @return 返回广播任务列表
     */
    List<HornTaskConfVO> getListByConditionPage(HornTaskConfVO hornTaskConfVO);

    /**
     * 新增广播任务
     *
     * @param hornTaskConfVO 广播任务配置信息
     * @return 返回结果
     */
    Result<String> addHornTask(HornTaskConfVO hornTaskConfVO, SysUser currentUser);



    /**
     * 添加广播任务并立即播放
     *
     * @param hornTaskConfVO HornTask任务的配置信息
     * @return Result<String> 返回操作结果，包含操作信息
     */
    Result<String> addAndStartHornTask(HornTaskConfVO hornTaskConfVO);

    /**
     * 添加并广播任务并立即开启
     *
     * @param hornTaskConfVO HornTask任务的配置信息
     * @return Result<String> 返回操作结果，包含操作信息
     */
    Result<String> addHornTaskV2(HornTaskConfVO hornTaskConfVO);

    /**
     * 修改广播任务
     *
     * @param hornTaskConfVO 广播任务配置信息
     * @return 返回操作结果，成功返回操作成功信息，失败返回操作失败信息
     */
    Result<String> modifyHornTask(HornTaskConfVO hornTaskConfVO);

    /**
     * 删除广播任务
     *
     * @param hornTaskConfVO 角任务配置信息
     * @return 操作结果，包含操作信息
     */
    Result<String> deleteHornTask(HornTaskConfVO hornTaskConfVO);

    /**
     * 删除角任务V2的函数
     *
     * @param hornTaskConfVO 角任务配置对象
     * @return 返回一个Result对象，包含操作结果和结果信息
     */
    Result<String> deleteHornTaskV2(HornTaskConfVO hornTaskConfVO);

    /**
     * 停止广播任务
     *
     * @param hornTaskConfVO horn任务配置信息
     * @return 返回结果
     */
    Result<String> stopHornTask(HornTaskConfVO hornTaskConfVO);

    /**
     * 获取任务详情
     *
     * @param hornTaskConfVO 任务配置信息
     * @return 返回任务详情结果
     */
    Result<HornTaskConfVO> taskDetail(HornTaskConfVO hornTaskConfVO);

    /**
     * 立即播放
     *
     * @param hornTaskConfVO 喇叭任务配置信息
     * @return 返回结果
     */
    Result<String> playRightNow(HornTaskConfVO hornTaskConfVO, SysUser currentUser);

    /**
     * 开始任务
     *
     * @param hornTaskConfVO 任务配置信息
     * @return 返回任务ID
     */
    Result<String> startTask(HornTaskConfVO hornTaskConfVO);

    /**
     * 执行文本立即播放
     * @param fileIdList 文本id列表
     * @return 执行结果
     */
    Result<String> callHornTaskPlay(List<String> fileIdList);

    /**
     * 删除指定文件的任务播放参数。
     *
     * @param fileIdList 待删除任务的播放参数的文件ID列表
     * @return 操作结果
     */
    Result<String> deleteTaskPlayParam(List<String> fileIdList);

    void syncTaskInfoToAlgorithmMongo(Long taskId, String txtFileUrl, String txtFileId);

    Result<String> syncOldTaskToMongo(HornTaskConfVO hornTaskConfVO);

}
