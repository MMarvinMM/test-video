package com.chinatower.device.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.chinatower.common.entity.Result;
import com.chinatower.common.page.TableDataInfo;
import com.chinatower.common.utils.StringUtils;
import com.chinatower.controller.TowerBaseController;
import com.chinatower.device.dataobject.AlgorithmConfigDO;
import com.chinatower.device.dto.AlgorithmAppDTO;
import com.chinatower.device.dto.AlgorithmRankVo;
import com.chinatower.device.mapper.AlgorithmConfigMapper;
import com.chinatower.device.service.IAlgorithmConfigInfoService;
import com.chinatower.platform.annotation.DataScope;
import com.chinatower.platform.constants.Constants;
import com.chinatower.platform.constants.DeviceConstants;
import com.chinatower.platform.entity.device.*;
import com.chinatower.platform.entity.devicemgmt.vo.DeviceInfoVO;
import com.chinatower.platform.entity.order.CustomerDTO;
import com.chinatower.platform.entity.order.OrderAlgorithmsVO;
import com.github.pagehelper.PageHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * video-device
 * @ClassName AlgorithmConfigController
 * @Description 算法配置前端控制器
 * @Author Chensw
 * @Date 2022/1/19
 */
@RestController
@Api(value = "算法配置模块",tags = "算法配置模块")
@Slf4j
public class AlgorithmConfigController extends TowerBaseController {

    @Resource
    private IAlgorithmConfigInfoService algorithmConfigInfoService;
    @Resource
    AlgorithmConfigMapper algorithmConfigMapper;


    /**
     * 列表分页查询算法配置
     *
     * @param algorithmConfigDTO
     * @return TableDataInfo
     * @author chensw
     * @date 2022/1/19
     */
    @ApiOperation("列表分页查询算法配置")
    @PostMapping("/video/algorithm/list")
    public TableDataInfo list(@RequestBody AlgorithmConfigDTO algorithmConfigDTO) {
        List<AlgorithmListVO> list = algorithmConfigInfoService.getAlgorithmConfigList(algorithmConfigDTO);
        return getDataTable(list);
    }

    /**
     * 列表分页查询算法配置2
     *
     * @param algorithmConfigDTO
     * @return TableDataInfo
     * @author chensw
     * @date 2022/3/8
     */
    @ApiOperation("列表分页查询算法配置")
    @PostMapping("/video/algorithm/list2")
    public TableDataInfo list2(@RequestBody AlgorithmConfigDTO algorithmConfigDTO) {
        log.info("列表分页查询算法配置请求报文:"+ JSONObject.toJSONString(algorithmConfigDTO));
        List<AlgorithmListVO> list = algorithmConfigInfoService.getAlgorithmConfigList2(algorithmConfigDTO);
        log.info("列表分页查询算法配置返回报文:"+ JSONObject.toJSONString(list));
        return getDataTable(list);
    }

    /**
     * 新增算法配置
     * @param algorithmListVO
     * @author: chenshaowu
     * @date: 2022/1/27
     * @return
     */
    @ApiOperation("算法配置新增")
    @PostMapping("/video/algorithm/save")
    public Result save(@RequestBody AlgorithmListVO algorithmListVO) {
        return algorithmConfigInfoService.save(algorithmListVO);
    }

    /**
     * 算法配置查看
     * @param algorithmConfigDTO
     * @author: chenshaowu
     * @date: 2022/2/11
     * @return
     */
    @PostMapping("/video/algorithm/queryById")
    @ApiOperation("算法配置查看")
    public Result<List<AlgorithmConfigVO>> queryById(@RequestBody AlgorithmConfigDTO algorithmConfigDTO) {
        List<AlgorithmConfigVO> list = algorithmConfigInfoService.queryById(algorithmConfigDTO);
        return Result.data(list);
    }

    /**
     * 算法配置编辑
     * @param algorithmConfigDTO
     * @return
     */
    @PostMapping("/video/algorithm/update")
    @ApiOperation("算法配置编辑")
    public Result<String> update(@RequestBody AlgorithmConfigDTO algorithmConfigDTO) {
        algorithmConfigInfoService.update(algorithmConfigDTO);
        return Result.success("算法配置编辑成功!");
    }

    /**
     * 算法配置启用
     * @param: algorithmConfigDTO
     * @return:
     * @auther: zhaozj
     * @date: 2022/12/14
     * @version: release_v1.5.50
     * @requestID: 配置管理_算法配置
     */
    @PostMapping("/video/algorithm/enabledStatus")
    @ApiOperation("算法配置启用")
    public Result<String> enabledStatus(@RequestBody AlgorithmConfigDTO algorithmConfigDTO) {
        algorithmConfigDTO.setStatus(AlgorithmAreaVO.ALGORITHMCLOSESTATUS);
        algorithmConfigInfoService.uptAlgorithmStatus(algorithmConfigDTO);
        return Result.success("算法配置启用成功!");
    }

    /**
     * 算法配置停用
     * @param: algorithmConfigDTO
     * @return:
     * @auther: zhaozj
     * @date: 2022/12/14
     * @version: release_v1.5.50
     * @requestID: 配置管理_算法配置
     */
    @PostMapping("/video/algorithm/disabledStatus")
    @ApiOperation("算法配置停用")
    public Result<String> disabledStatus(@RequestBody AlgorithmConfigDTO algorithmConfigDTO) {
        algorithmConfigDTO.setStatus(AlgorithmAreaVO.ALGORITHMOPENSTATUS);
        algorithmConfigInfoService.uptAlgorithmStatus(algorithmConfigDTO);
        return Result.success("算法配置停用成功!");
    }

    /**
     * 算法配置删除
     * @param algorithmConfigDTO
     * @return
     */
    @PostMapping("/video/algorithm/delete")
    @ApiOperation("算法配置删除")
    public Result<String> delete(@RequestBody AlgorithmConfigDTO algorithmConfigDTO) {
        return algorithmConfigInfoService.delete(algorithmConfigDTO);
    }

    /**
     * 单查通道下算法配置
     * @param algorithmConfigDTO
     * @author: chenshaowu
     * @date: 2022/3/7
     * @return
     */
    @PostMapping("/video/algorithm/queryAlgorithm")
    @ApiOperation("单查通道下算法配置")
    public TableDataInfo queryAlgorithm(@RequestBody AlgorithmConfigDTO algorithmConfigDTO){
        return getDataTable(algorithmConfigInfoService.queryAlgorithm(algorithmConfigDTO));
    }

    /**
     * 批量查询通道下的算法配置
     * @param algorithmConfigDTO
     * @author: hued
     * @date: 2024/12/30
     * @return
     */
    @PostMapping("/video/algorithm/queryAlgorithmBatch")
    @ApiOperation("批量查询通道下的算法配置")
    public TableDataInfo queryAlgorithmBatch(@RequestBody AlgorithmConfigDTO algorithmConfigDTO){
        return getDataTable(algorithmConfigInfoService.queryAlgorithmBatch(algorithmConfigDTO));
    }


    /**
     * 单查通道下算法配置
     * @param algorithmConfigDTO
     * @return
     */
    @PostMapping("/video/algorithm/queryAlgorithmApp")
    @ApiOperation("单查通道下算法配置")
    public Result<AlgorithmAppDTO> queryAlgorithmApp(@RequestBody AlgorithmConfigDTO algorithmConfigDTO){
        return algorithmConfigInfoService.queryAlgorithmApp(algorithmConfigDTO);
    }



    /**
     * 查询算法状态
     * @author: chenshaowu
     * @date: 2022/3/8
     * @return
     */
    @PostMapping("/video/algorithm/synchAlgorithmConfigStatus")
    @ApiOperation("查询算法状态")
    Result synchAlgorithmConfigStatus(@RequestBody AlgorithmAreaVO algorithmAreaVO){
        return algorithmConfigInfoService.synchAlgorithmConfigStatus(algorithmAreaVO.getTaskId());
    }

    /**
     * 查询算法数量
     * @param presetDTO
     * @return
     */
    @PostMapping("/video/algorithm/getAlgorithmConfigCount")
    int getAlgorithmConfigCount(@RequestBody DelPresetReq presetDTO){
        return algorithmConfigMapper.selectCount(new LambdaQueryWrapper<AlgorithmConfigDO>()
                .eq(AlgorithmConfigDO::getPresetId, presetDTO.getPresetId())
                .eq(AlgorithmConfigDO::getDeleteFlag, DeviceConstants.DELETE_FLAG_NORMAL_INT)
                .eq(AlgorithmConfigDO::getDeviceCode, presetDTO.getDeviceCode()));
    }

    /**
     * 查询摄像机编码
     * @param presetDTO
     * @return
     */
    @PostMapping("/video/algorithm/getDeviceCode")
    String getDeviceCode(@RequestBody PresetDTO presetDTO){
        return algorithmConfigMapper.getDeviceCode(presetDTO.getChannelCode());
    }


    /**
     * 通道配置的算法数量，展示通道TOP5
     * 根据通道已启用的算法数量，展示通道TOP5；字段展示：通道名称、通道已启用的算法数量；
     * @param: algorithmRankVo
     * @return: com.chinatower.common.entity.Result
     * @auther: yiyao
     * @date: 2022/11/18
     * @version: 1.5.30
     */
    @DataScope(tenantColumn="t1.tenant_id", acceptColumn="t1.accept_node", industryCode="t1.industry_code",deviceColumn = "t1.device_code",alarmSourceVal = "4")
    @PostMapping("/video/algorithm/rankByStatus")
    public TableDataInfo getRank(@RequestBody AlgorithmRankVo algorithmRankVo){
        Integer pageNum = algorithmRankVo.getPageNum();
        Integer pageSize = algorithmRankVo.getPageSize();
        if (StringUtils.isNotNull(pageNum) && StringUtils.isNotNull(pageSize)) {
            PageHelper.startPage(pageNum, pageSize);
        }
        return getDataTable(algorithmConfigInfoService.getRank(algorithmRankVo));
    }

    /**
     * 算法配置查询算法数量详细信息
     * @param algorithmConfigDTO
     * @return TableDataInfo
     * @auther: zhaozj
     * @date: 2022/12/08
     * @version: release_v1.5.50
     * @requestID: 配置管理_算法配置
     */
    @ApiOperation("查询算法数量详细信息")
    @PostMapping("/video/algorithm/getAlgoCountInfo")
    public TableDataInfo getAlgoCountInfo(@RequestBody AlgorithmConfigDTO algorithmConfigDTO) {
        List<OrderAlgorithmsVO> list = algorithmConfigInfoService.getAlgoCountInfo(algorithmConfigDTO);
        return getDataTable(list);
    }

    /**
     * 查询订购算法列表
     * @author chensw
     * @date 2022/2/11
     * @return
     */
    @PostMapping("/video/algorithm/queryAlgorithmList")
    public Result<List<OrderAlgorithmsVO>> queryAlgorithmList(@RequestBody CustomerDTO customerDTO) {
        log.info("查询订购算法列表请求报文:"+ JSONObject.toJSONString(customerDTO));
        try {
            List<OrderAlgorithmsVO> list = algorithmConfigInfoService.queryAlgorithmList(customerDTO);
            if(customerDTO.getIsHasBigModel() == Constants.ZERO_NUM){
                // 核心逻辑：过滤掉algorithmCode在algorithmCodeList中的元素
                List<String> finalAlgorithmCodeList = algorithmConfigInfoService.getBigModelAlgList();;
                list = list.stream()
                        // 保留条件：算法编码不在目标列表中
                        .filter(orderAlgorithmsVO -> !finalAlgorithmCodeList.contains(orderAlgorithmsVO.getAlgorithmCode()))
                        // 收集为新的List（保持原列表类型，如ArrayList）
                        .collect(Collectors.toList());
            }
            log.info("查询订购算法列表返回报文:"+ JSONObject.toJSONString(list));
            return Result.data(list);
        } catch (Exception e) {
            log.error("查询订购算法列表失败, e:", e);
            return Result.fail("查询订购算法列表失败");
        }
    }

    /**
     * 查询基准图对比算法
     * @date 2025年7月15日10:54:24
     * @return
     */
    @PostMapping("/video/algorithm/queryBaselineAlgorithmList")
    public Result<List<OrderAlgorithmsVO>> queryBaselineAlgorithmList(@RequestBody CustomerDTO customerDTO) {
        log.info("查询基准图对比算法:"+ JSONObject.toJSONString(customerDTO));
        try {
            List<OrderAlgorithmsVO> list = algorithmConfigInfoService.queryBaselineAlgorithmList(customerDTO);
            log.info("查询基准图对比算法返回报文:"+ JSONObject.toJSONString(list));
            return Result.data(list);
        } catch (Exception e) {
            log.error("查询基准图对比算法失败, e:", e);
            return Result.fail("查询基准图对比算法失败");
        }
    }

    /**
     * 批量查询基准图对比算法
     * @date 2025年7月15日10:54:24
     * @return
     */
    @PostMapping("/video/algorithm/queryBaselineAlgorithmListBatch")
    public Result<Map<String, List<OrderAlgorithmsVO>>> queryBaselineAlgorithmListBatch(@RequestBody List<String> deviceCodeList) {
        log.info("批量查询基准图对比算法:"+ JSONObject.toJSONString(deviceCodeList));
        try {
            Map<String, List<OrderAlgorithmsVO>> list = algorithmConfigInfoService.queryBaselineAlgorithmListBatch(deviceCodeList);
            log.info("批量查询基准图对比算法返回报文:"+ JSONObject.toJSONString(list));
            return Result.data(list);
        } catch (Exception e) {
            log.error("批量查询基准图对比算法失败, e:", e);
            return Result.fail("批量查询基准图对比算法失败");
        }
    }

    /**
     * 校验算法配置新任务
     * @author hued
     * @date 2022年12月23日10:31:54
     * @return
     */
    @PostMapping("/video/algorithm/checkAlgorithmConfig")
    public Result<String> checkAlgorithmConfig(@RequestBody CheckAlgorithmConfigVO checkAlgorithmConfigVO) {
        return algorithmConfigInfoService.checkAlgorithmConfig(checkAlgorithmConfigVO);
    }

    /**
     * 查询选中摄像机订购的算法交集
     * @author yy
     * @return
     */
    @PostMapping("/video/algorithm/intersection")
    public Result<Set<OrderAlgorithmsVO>> intersection(@RequestBody @Validated AlgorithmIntersectionVO intersection) {
        return Result.data(algorithmConfigInfoService.intersection(intersection));
    }

    /**
     * 后端算法批量新增保存
     * @author yy
     * @return
     */
    @PostMapping("/video/algorithm/batchAddOrUpdate")
    public Result<String> batchAddOrUpdate(@RequestBody @Validated List<AlgorithmListVO> list) {
        return algorithmConfigInfoService.batchAddOrUpdate(list);
    }


    /**
     * 批量处理算法历史数据
     */
    @PostMapping("/video/algorithm/dealHisAlgorithm")
    public Result<String> dealHisAlgorithm() {
        return algorithmConfigInfoService.dealHisAlgorithm();
    }

    /**
     * 联动算法列表查询
     * @return
     */
    @PostMapping("/video/algorithm/queryLinkageAlgorithmList")
    public Result<List<LinkageAlgorithmMappingVO>> queryLinkageAlgorithmList(@RequestBody LinkageAlgorithmMappingVO linkageAlgorithmMappingVO) {
        return Result.data(algorithmConfigInfoService.queryLinkageAlgorithmList(linkageAlgorithmMappingVO));
    }

    /**
     * 智能生成新配置场景，新增后端算法任务
     * @param algorithmConfigAdd
     * @return
     */
    @PostMapping("/video/algorithm/insertAlgoConfigMCP")
    public Result<List<AlgorithmConfigVO>> insertAlgoConfigMCP(@RequestBody List<AlgorithmConfigAdd> algorithmConfigAdd) {
        return algorithmConfigInfoService.insertAlgoConfigMCP(algorithmConfigAdd);
    }

    /**
     * 单查通道下算法配置
     * @param algorithmConfigDTO
     * @return
     */
    @PostMapping("/video/algorithm/queryAlgorithmMCP")
    @ApiOperation("单查通道下算法配置")
    public Result<List<AlgorithmAreaVO>> queryAlgorithmMCP(@RequestBody AlgorithmConfigDTO algorithmConfigDTO){
        return Result.data(algorithmConfigInfoService.queryAlgorithm(algorithmConfigDTO));
    }

    /**
     * 校验当前通道是否是可见光通道
     * @param deviceInfoVO
     * @return
     */
    @PostMapping("/video/algorithm/checkChannelAttr")
    public Result<Boolean> checkChannelAttr(@RequestBody DeviceInfoVO deviceInfoVO){
        return Result.data(algorithmConfigInfoService.checkChannelAttr(deviceInfoVO));
    }
    /**
     * 根据设备、通道查询已配置的算法/场景列表
     * @param algAndSceVO
     * @return
     */
    @PostMapping("/video/algorithm/getAlgAndSceList")
    public Result<List<AlgAndSceResultVO>> getAlgAndSceList(@RequestBody AlgAndSceVO algAndSceVO){
        return Result.data(algorithmConfigInfoService.getAlgAndSceList(algAndSceVO));
    }
    /**
     * 修复历史数据
     * @return
     */
    @PostMapping("/video/algorithm/updateAlgorithmHis")
    public Result<String> updateAlgorithmHis(@RequestBody AlgorithmConfigDTO vo){
        return Result.success(algorithmConfigInfoService.updateAlgorithmHis(vo));
    }

    /**
     * 查询大模型算法高参省份
     * @return
     */
    @PostMapping("/video/algorithm/getProvinceByPlatform")
    public Result<String> getProvinceByPlatform(){
        return Result.data(algorithmConfigInfoService.getProvinceByPlatform());
    }

    /**
     * 修复大模型算法model_config中缺少monitorAreaId的问题
     * @return
     */
    @PostMapping("/video/algorithm/fixModelConfig")
    @ApiOperation("修复大模型算法model_config")
    public Result<String> fixModelConfig() {
        return algorithmConfigInfoService.fixModelConfig();
    }
}
