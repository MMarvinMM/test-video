package com.chinatower.device.controller;

import com.chinatower.common.entity.Result;
import com.chinatower.common.page.TableDataInfo;
import com.chinatower.controller.TowerBaseController;
import com.chinatower.device.entity.vo.HornTaskConfVO;
import com.chinatower.device.service.IHornTaskConfService;
import com.chinatower.platform.constants.Constants;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.model.dto.LoginUser;
import com.chinatower.platform.utils.SecurityUtils;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

/**
 * video-device
 */
@RestController
@Api(value = "广播任务控制层")
@RequestMapping("/hornTask/device")
@Slf4j
public class HornTaskController extends TowerBaseController {

    @Autowired
    private IHornTaskConfService hornTaskConfService;

    /**
     * 查询任务列表数据--带分页
     *
     * @param hornTaskConfVO 查询条件
     * @return 返回查询结果
     */
    @PostMapping("/list")
    public TableDataInfo getList(@RequestBody HornTaskConfVO hornTaskConfVO) {
        List<HornTaskConfVO> hornTaskConfVOList = hornTaskConfService.getListByConditionPage(hornTaskConfVO);
        return getDataTable(hornTaskConfVOList);
    }

    /**
     * 新增广播任务
     *
     * @param hornTaskConfVO 广播任务配置信息
     * @return 返回结果
     */
    @PostMapping("/addHornTask")
    public Result<String> addHornTask(@RequestBody HornTaskConfVO hornTaskConfVO) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (Objects.isNull(loginUser)) {
            return Result.fail(Constants.NO_USER_INFO);
        }
        return hornTaskConfService.addHornTask(hornTaskConfVO, loginUser.getUser());
    }


    /**
     * 新增&立即播放广播任务 - 给乡村行业定时任务使用，传入客户和行业编码
     *
     * @param hornTaskConfVO 包含定时任务信息的HornTaskConfVO对象
     * @return 返回Result<String>对象，包含操作结果信息
     */
    @PostMapping("/addAndStartHornTask")
    public Result<String> addAndStartHornTask(@RequestBody HornTaskConfVO hornTaskConfVO) {
        return hornTaskConfService.addAndStartHornTask(hornTaskConfVO);
    }

    /**
     * 新增广播任务 - 给交通行业定时任务使用，传入客户和行业编码
     *
     * @param hornTaskConfVO 包含定时任务信息的HornTaskConfVO对象
     * @return 返回Result<String>对象，包含操作结果信息
     */
    @PostMapping("/addHornTaskV2")
    public Result<String> addHornTaskV2(@RequestBody HornTaskConfVO hornTaskConfVO) {
        return hornTaskConfService.addHornTaskV2(hornTaskConfVO);
    }


    /**
     * 删除定时任务接口（V2） - 给乡村行业定时任务使用，传入客户和行业编码
     *
     * @param hornTaskConfVO 定时任务配置信息对象
     * @return 返回删除结果
     */
    @PostMapping("/deleteHornTask/v2")
    public Result<String> deleteHornTaskV2(@RequestBody HornTaskConfVO hornTaskConfVO) {
        return hornTaskConfService.deleteHornTaskV2(hornTaskConfVO);
    }


    /**
     * 修改广播任务
     *
     * @param hornTaskConfVO 广播任务配置信息
     * @return 返回修改结果
     */
    @PostMapping("/modifyHornTask")
    public Result<String> modifyHornTask(@RequestBody HornTaskConfVO hornTaskConfVO) {
        return hornTaskConfService.modifyHornTask(hornTaskConfVO);
    }

    /**
     * 删除广播任务
     *
     * @param hornTaskConfVO 需要删除得id集合
     * @return 返回修改结果
     */
    @PostMapping("/deleteHornTask")
    public Result<String> deleteHornTask(@RequestBody HornTaskConfVO hornTaskConfVO) {
        return hornTaskConfService.deleteHornTask(hornTaskConfVO);
    }


    /**
     * 结束广播任务
     *
     * @param hornTaskConfVO 广播任务配置信息
     * @return 返回操作结果
     */
    @PostMapping("/stopHornTask")
    public Result<String> stopHornTask(@RequestBody HornTaskConfVO hornTaskConfVO) {
        return hornTaskConfService.stopHornTask(hornTaskConfVO);
    }


    /**
     * 查询任务详情数据
     *
     * @param hornTaskConfVO 任务配置信息
     * @return 返回任务详情数据，类型为Result<HornTaskConfVO>
     */
    @PostMapping("/taskDetail")
    public Result<HornTaskConfVO> taskDetail(@RequestBody HornTaskConfVO hornTaskConfVO) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (Objects.isNull(loginUser)) {
            return Result.fail(Constants.NO_USER_INFO);
        }
        return hornTaskConfService.taskDetail(hornTaskConfVO);
    }


    /**
     * 立即播放 对应pc任务页面上的播放按钮
     *
     * @param hornTaskConfVO 播放任务的配置信息
     * @return Result<HornTaskConfVO> 返回播放任务的结果
     */
    @PostMapping("/playRightNow")
    public Result<String> playRightNow(@RequestBody HornTaskConfVO hornTaskConfVO) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (Objects.isNull(loginUser)) {
            return Result.fail(Constants.NO_USER_INFO);
        }
        SysUser currentUser = loginUser.getUser();
        return hornTaskConfService.playRightNow(hornTaskConfVO, currentUser);
    }

    /**
     * 开始任务 对应pc任务页面上的开始按钮
     *
     * @param hornTaskConfVO 任务配置信息
     * @return Result<String> 返回结果
     */
    @PostMapping("/startHornTask")
    public Result<String> startTask(@RequestBody HornTaskConfVO hornTaskConfVO) {
        return hornTaskConfService.startTask(hornTaskConfVO);
    }

    /**
     * 文转语异步回调启动喇叭播放
     */
    @PostMapping("/callHornTaskPlay")
    public Result<String> callHornTaskPlay(@RequestBody List<String> fileIdList) {
        return hornTaskConfService.callHornTaskPlay(fileIdList);
    }

    /**
     * 通过文件id删除待回调文转语播放任务
     */
    @PostMapping("/deleteTaskPlayParam")
    public Result<String> deleteTaskPlayParam(@RequestBody List<String> fileIdList) {
        return hornTaskConfService.deleteTaskPlayParam(fileIdList);
    }

    /**
     * 同步广播样本数据给算法仓
     */
    @PostMapping("/syncTaskInfoToAlgorithmMongo")
    public Result<String> syncTaskInfoToAlgorithmMongo(@RequestBody Long taskId) {
        hornTaskConfService.syncTaskInfoToAlgorithmMongo(taskId, null, null);
        return Result.success("执行成功");
    }

    /**
     * 同步历史广播样本数据给算法仓
     */
    @PostMapping("/syncOldTaskToMongo")
    public Result<String> syncOldTaskToMongo(@RequestBody HornTaskConfVO hornTaskConfVO) {
        return hornTaskConfService.syncOldTaskToMongo(hornTaskConfVO);
    }
}
