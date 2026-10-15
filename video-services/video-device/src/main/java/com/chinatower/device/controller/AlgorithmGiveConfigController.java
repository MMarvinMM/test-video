package com.chinatower.device.controller;

import cn.hutool.core.collection.ListUtil;
import com.chinatower.common.entity.Result;
import com.chinatower.common.page.TableDataInfo;
import com.chinatower.controller.TowerBaseController;
import com.chinatower.device.service.IAlgorithmGiveConfigService;
import com.chinatower.platform.constants.Constants;
import com.chinatower.platform.entity.device.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * video-device
 *
 * 前端算法配置控制器
 * @Author hued
 * @Date 2022年12月12日09:32:19
 * @version: 1.5.50
 * @requestID R-ZBZB-202211040970489 配置管理-算法配置优化
 */
@RestController
@Api(value = "前端算法配置模块",tags = "前端算法配置模块")
@Slf4j
@RequestMapping("/algorithm/give/")
public class AlgorithmGiveConfigController extends TowerBaseController {

    @Resource
    private IAlgorithmGiveConfigService algorithmGiveConfigService;

    /**
     * 前端算法配置详情查询
     *
     * @param id 自增主键
     * @author: hued
     * @date: 2022年12月12日09:32:19
     * @return com.chinatower.common.entity.Result
     * @version: 1.5.50
     * @requestID R-ZBZB-202211040970489 配置管理-算法配置优化
     */
    @ApiOperation("前端算法配置详情查询")
    @GetMapping("/queryById/{id}")
    public Result<AlgorithmGiveConfigSaveVO> queryById(@PathVariable(name = "id") int id) {
        return algorithmGiveConfigService.queryById(id);
    }

    /**
     * 前端算法配置新增
     *
     * @param algorithmGiveConfigSaveVO
     * @author: hued
     * @date: 2022年12月12日09:32:19
     * @return com.chinatower.common.entity.Result
     * @version: 1.5.50
     * @requestID R-ZBZB-202211040970489 配置管理-算法配置优化
     */
    @ApiOperation("前端算法配置新增")
    @PostMapping("/save")
    public Result<Integer> add(@Validated @RequestBody AlgorithmGiveConfigSaveVO algorithmGiveConfigSaveVO) {
        return  algorithmGiveConfigService.add(algorithmGiveConfigSaveVO);
    }

    /**
     * 前端算法配置编辑
     *
     * @param algorithmGiveConfigUpdateVO
     * @author: hued
     * @date: 2022年12月12日09:32:19
     * @return com.chinatower.common.entity.Result
     * @version: 1.5.50
     * @requestID R-ZBZB-202211040970489 配置管理-算法配置优化
     */
    @PostMapping("/update")
    @ApiOperation("前端算法配置编辑")
    public Result<String> edit(@Validated @RequestBody AlgorithmGiveConfigUpdateVO algorithmGiveConfigUpdateVO) {
        return algorithmGiveConfigService.edit(algorithmGiveConfigUpdateVO);
    }

    /**
     * 前端算法配置启用/停用
     *
     * @param algorithmGiveConfigStatusVO
     * @author: hued
     * @date: 2022年12月12日09:32:19
     * @return com.chinatower.common.entity.Result
     * @version: 1.5.50
     * @requestID R-ZBZB-202211040970489 配置管理-算法配置优化
     */
    @PostMapping("/status")
    @ApiOperation("前端算法配置启用")
    public Result<String> status(@Validated @RequestBody AlgorithmGiveConfigStatusVO algorithmGiveConfigStatusVO) {
        return algorithmGiveConfigService.status(algorithmGiveConfigStatusVO);
    }

    /**
     * 前端算法配置删除
     *
     * @param id
     * @author: hued
     * @date: 2022年12月12日09:32:19
     * @return com.chinatower.common.entity.Result
     * @version: 1.5.50
     * @requestID R-ZBZB-202211040970489 配置管理-算法配置优化
     */
    @GetMapping("/delete/{id}")
    @ApiOperation("前端算法配置删除")
    public Result<String> delete(@PathVariable(name = "id") int id) {
        return algorithmGiveConfigService.delete(id);
    }

    /**
     * 赠送告警类型查询
     *
     * @param
     * @author: hued
     * @date: 2022年12月12日09:32:19
     * @return com.chinatower.common.entity.Result
     * @version: 1.5.50
     * @requestID R-ZBZB-202211040970489 配置管理-算法配置优化
     */
    @GetMapping("/queryAlarmType")
    @ApiOperation("赠送告警类型查询")
    public Result<List<AlarmTypeInfoVO>> queryAlarmType(AlarmTypeReq alarmTypeReq) {
        return Result.data(algorithmGiveConfigService.queryAlarmType(alarmTypeReq));
    }

    /**
     * 校验告警类型是否已配置算法
     *
     * @param
     * @author: hued
     * @date: 2022年12月12日09:32:19
     * @return com.chinatower.common.entity.Result
     * @version: 1.5.50
     * @requestID R-ZBZB-202211040970489 配置管理-算法配置优化
     */
    @PostMapping("/checkAlarmType")
    @ApiOperation("校验告警类型是否已配置算法")
    public Result<String> checkAlarmType(@RequestBody AlgorithmGiveConfigSaveVO algorithmGiveConfigSaveVO) {
        return algorithmGiveConfigService.checkAlarmType(algorithmGiveConfigSaveVO);
    }

    /**
     * 订购信息整合
     *
     * 1、订购新增：前端告警配置不做初始化
     * 2、订购变更：
     *       变更新增：不做初始化
     *       -- 变更减少：对前端配置做停用操作，调用南向重新整合下发 --
     * 3、订购停用：
     *       -- 对前端配置进行停用  --
     * 4、订购启用：
     *       对前端配置不做启用处理
     * 5、订购退订：
     *      -- 对前端配置做停用、不做删除 --
     *
     * 订购对前端告警的配置，不做任何的删除操作
     * @param
     * @author: hued
     * @date: 2022年12月12日09:32:19
     * @return
     * @version: 1.5.50
     * @requestID R-ZBZB-202211040970489 配置管理-算法配置优化
     */
    @PostMapping("/algorithmGiveMergeOrder")
    public Result<String> algorithmGiveMergeOrder(@Validated @RequestBody AlgorithmGiveMergeOrderVO algorithmGiveMergeOrderVO){
        algorithmGiveConfigService.algorithmGiveMergeOrder(algorithmGiveMergeOrderVO);
        return Result.success("订单调用前端算法整合成功");
    }

    /**
     * 前端算法手动整合
     *
     * @param
     * @author: hued
     * @date: 2022年12月12日09:32:19
     * @return
     * @version: 1.5.50
     * @requestID R-ZBZB-202211040970489 配置管理-算法配置优化
     */
    @PostMapping("/algorithmGiveConfigMerge")
    public Result<String> algorithmGiveConfigMerge(@RequestBody List<DeviceChannelVO> deviceChannelList){
        algorithmGiveConfigService.algorithmGiveConfigMerge(deviceChannelList);
        return Result.success("整合成功");
    }


    /**
     * 定时整合算法
     * @return
     */
    @PostMapping("/algorithmGiveConfigMergeHandle")
    public Result<String> algorithmGiveConfigMergeHandle(){
        algorithmGiveConfigService.algorithmGiveConfigMergeHandle(null);
        return Result.success("定时整合成功");
    }

    /**
     * 前端算法整合定时任务查询
     * @author hued
     * @date 2022年12月23日10:31:54
     * @return
     */
    @PostMapping("/getBlackWhiteList")
    public TableDataInfo getBlackWhiteList(@RequestBody BlackWhiteReq blackWhiteReq) {
        Integer pageNum = blackWhiteReq.getPageNum();
        Integer pageSize = blackWhiteReq.getPageSize();
        //不使用pageHelper 分页 删除实体类中的pageNum 和pageSize
        blackWhiteReq.setPageNum(null);
        blackWhiteReq.setPageSize(null);
        List<BlackWhiteVO> list = algorithmGiveConfigService.getBlackWhiteList(blackWhiteReq);
        int pageNumNew = pageHandle(list.size(),pageSize,pageNum);
        //操作逻辑中需要删除元素，需要进行手动分页
        List<BlackWhiteVO> result = ListUtil.page(pageNumNew - 1, pageSize, list);
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(200);
        rspData.setRows(result);
        rspData.setTotal(list.size());
        return rspData;
    }

    /**
     * 前端算法批量新增或修改
     *
     * @param algorithmGiveBatchSave list
     * @author: yy
     */
    @ApiOperation("前端算法批量新增或修改")
    @PostMapping("/batchAddOrUpdate")
    public Result<String> batchAddOrUpdate(@Validated @RequestBody AlgorithmGiveBatchSaveVO algorithmGiveBatchSave) {
        return  algorithmGiveConfigService.batchAddOrUpdate(algorithmGiveBatchSave);
    }

    private int pageHandle(int total,int pageSize,int pageNum){
        int pageNumNew;
        int pages;
        if (pageSize > Constants.ZERO_NUM) {
            pages = total / pageSize + ((total % pageSize == Constants.ZERO_NUM) ? Constants.ZERO_NUM :  Constants.ONE_NUM);
        }else{
            pages = Constants.ZERO_NUM;
        }
        if (pageNum > pages ){
            pageNum = pages;
        }
        pageNumNew = pageNum;
        return pageNumNew;
    }

    /**
     * 根据告警类型名称模糊查询前端告警类型
     * @param alarmTypeReq
     * @return
     */
    @PostMapping("/getAlarmTypeMcp")
    public Result<List<AlarmTypeInfoVO>> getAlarmTypeMcp(@RequestBody AlarmTypeReq alarmTypeReq) {
        return Result.data(algorithmGiveConfigService.queryAlarmType(alarmTypeReq));
    }


    /**
     * 根据摄像机编码、通道编码、算法编码，查询前端算法任务列表
     * @param algorithmInfoDO
     * @return
     */
    @PostMapping("/getAlgorithmGiveDetailMCP")
    public Result<List<AlgorithmGiveConfigSaveVO>> getAlgorithmGiveDetailMCP(@RequestBody AlgorithmGiveConfigSaveVO algorithmInfoDO) {
        if (ObjectUtils.isEmpty(algorithmInfoDO.getAlarmType()) ||
                ObjectUtils.isEmpty(algorithmInfoDO.getChannelCode()) ||
                ObjectUtils.isEmpty(algorithmInfoDO.getDeviceCode())) {
            return Result.fail("参数不允许为空");
        }
        return Result.data(algorithmGiveConfigService.getAlgorithmGiveDetailMCP(algorithmInfoDO));
    }

    /**
     * 智能生成新配置场景，新增前端算法任务
     * @param algorithmGiveConfigAdd
     * @return
     */
    @PostMapping("/insertGivenAlgoConfigMCP")
    public Result<List<AlgorithmGiveConfigSaveVO>> insertGivenAlgoConfig(@RequestBody List<AlgorithmGiveConfigAdd> algorithmGiveConfigAdd) {
        return algorithmGiveConfigService.insertGivenAlgoConfig(algorithmGiveConfigAdd);
    }
}
