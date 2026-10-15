package com.chinatower.algorithm.controller;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.chinatower.algorithm.dataobject.OrderAlgorithmDTO;
import com.chinatower.algorithm.mapper.SysSceneAlarmMapper;
import com.chinatower.algorithm.repository.IAlgorithmInfoRepository;
import com.chinatower.algorithm.repository.IFunctionTypeRepository;
import com.chinatower.algorithm.repository.ISysSceneAlarmRepository;
import com.chinatower.common.entity.Result;
import com.chinatower.device.entity.dto.ExtendJson;
import com.chinatower.device.entity.vo.FunctionTypeVO;
import com.chinatower.file.deal.FileUrlEncryptUtil;
import com.chinatower.industry.dto.AlgorithmInfoDO;
import com.chinatower.platform.constants.Constants;
import com.chinatower.platform.constants.DeviceConstants;
import com.chinatower.platform.constants.DeviceTableConstants;
import com.chinatower.platform.utils.SecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * video-device
 *
 * @author AutoGen
 * @since 2022-02-16
 */
@RestController
@Slf4j
@Api(value = "算法基本信息模块", tags = "算法基本信息模块")
public class AlgorithmInfoController {

    @Resource
    IAlgorithmInfoRepository algorithmInfoRepository;
    @Resource
    IFunctionTypeRepository functionTypeRepository;

    @Resource
    private FileUrlEncryptUtil fileUrlEncryptUtil;

    @Resource
    private SysSceneAlarmMapper sysSceneAlarmMapper;

    @Resource
    private ISysSceneAlarmRepository sysSceneAlarmRepository;

    /**
     * 获取功能信息
     *
     * @param: tenantId
     * @return: com.chinatower.common.entity.Result
     * @auther: chenshaowu
     * @date: 2022/3/3
     * @version: 1.0
     */
    @PostMapping("/video/algorithm/getFunction")
    @ApiOperation("获取功能信息")
    public Result<Map> getFunction(@RequestBody Map map) {
        String tenantId = "";
        tenantId = map.get("tenantId") == null ? SecurityUtils.getLoginUser().getUser().getTenantId() : String.valueOf(map.get("tenantId"));
        return Result.data(functionTypeRepository.getFunction(tenantId));
    }


    /**
     * 获取算法详情信息
     *
     * 根据算法CODE获取算法详情信息
     *
     * @param: algorithmInfoDO
     * @return: com.chinatower.common.entity.Result
     * @auther: zhaozj
     * @date: 2022/08/23
     * @version: release_v1.5
     */
    @PostMapping("/video/algorithm/getAlgorithmInfo")
    @ApiOperation("根据算法CODE获取算法详情信息")
    @Deprecated
    public Result<AlgorithmInfoDO> getAlgorithmInfo(@RequestBody AlgorithmInfoDO algorithmInfoDO) {
        log.info("废弃接口调用");
        if (ObjectUtils.isEmpty(algorithmInfoDO.getAlgorithmCode())) {
            return Result.fail("algorithmCode不允许为空");
        }
        return Result.data(algorithmInfoRepository.getAlgorithmInfo(algorithmInfoDO));
    }

    /**
     * 批量获取算法详情信息
     *
     * 根据算法CODE批量获取算法详情信息
     * @param: algorithmInfoDO
     * @return: com.chinatower.common.entity.Result
     * @auther: ljw
     * @date: 2022/12/12
     * @version: release_v1.5
     */
    @PostMapping("/video/algorithm/getAlgorithmInfoBatch")
    @ApiOperation("根据算法CODE列表批量获取算法详情信息")
    public Result<List<AlgorithmInfoDO>> getAlgorithmInfoBatch(@RequestBody List<String> list) {
        if (ObjectUtils.isEmpty(list)) {
            return Result.fail("algorithmCode不允许为空");
        }
        QueryWrapper<AlgorithmInfoDO> AlgorithmInfoQuery = new QueryWrapper<>();
        AlgorithmInfoQuery.in("algorithm_code", list);
        AlgorithmInfoQuery.eq(DeviceTableConstants.FIELD_DELETE_FLAG, DeviceConstants.DELETE_FLAG_NORMAL);
        List<AlgorithmInfoDO> algorithmInfo = algorithmInfoRepository.list(AlgorithmInfoQuery);

        //解析扩展字段
        algorithmInfo.forEach(item -> {
            String supportFrameColor = Constants.ZERO;
            String supportDoubleImgAnalysis = Constants.ZERO;
            Integer supportAlarmInterval =  Constants.ZERO_NUM;
            String defaultAnalysisPeriod = null;
            String algDescribeFile = null;
            String algDescribeFileName = null;
            String extendJson = item.getExtendJson();
            if (!StringUtils.isEmpty(extendJson)) {
                ExtendJson ej = JSONObject.parseObject(extendJson,ExtendJson.class);
                if(!ObjectUtils.isEmpty(ej)){
                    String o1 = ej.getSupportFrameColor();
                    if(!ObjectUtils.isEmpty(o1)){
                        supportFrameColor = o1;
                    }

                    String o2 = ej.getSupportDoubleImgAnalysis();
                    if(!ObjectUtils.isEmpty(o2)){
                        supportDoubleImgAnalysis = o2;
                    }
                    Integer o3 = ej.getSupportAlarmInterval();
                    if(o3 != null){
                        supportAlarmInterval = o3;
                    }
                    defaultAnalysisPeriod = ej.getDefaultAnalysisPeriod();
                    algDescribeFile = ej.getAlgDescribeFile();
                    algDescribeFileName = ej.getAlgDescribeFileName();
                }
            }
            item.setSupportFrameColor(supportFrameColor);
            item.setSupportDoubleImgAnalysis(supportDoubleImgAnalysis);
            item.setDefaultAnalysisPeriod(defaultAnalysisPeriod);
            item.setAlgDescribeFile(algDescribeFile);
            item.setAlgDescribeFileName(algDescribeFileName);
            item.setSupportAlarmInterval(supportAlarmInterval);
        });

//        Set<String> fileUrls = algorithmInfo.stream().map(AlgorithmInfoDO::getAlgDescribeFile).collect(Collectors.toSet());
//        if(!CollectionUtils.isEmpty(fileUrls)){
//            Map<String, String> result = fileUrlEncryptUtil.dealObsFileUrlBatch(new ArrayList(fileUrls));
//            algorithmInfo.forEach(alg -> {
//                if (!StringUtils.isEmpty(alg.getAlgDescribeFile())) {
//                    alg.setAlgDescribeFile(result.get(alg.getAlgDescribeFile()));
//                }
//            });
//        }
        return Result.data(algorithmInfo);
    }

    /**
     * 获取功能详情
     *
     * 根据功能ID获取功能详情
     * @param: id
     * @return: Result
     * @auther: chenshaowu
     * @date: 2022/3/14
     * @version: 1.0
     */
    @GetMapping("/video/algorithm/getFunctionDetail/{id}")
    @ApiOperation("获取功能详情")
    public Result<FunctionTypeVO> getFunctionDetail(@PathVariable("id") String id) {
        FunctionTypeVO functionDetail = functionTypeRepository.getFunctionDetail(id);
        return Result.data(functionDetail);
    }

    /**
     * 根据字典表中的model_config_list字典值，查询vm_order_algorithms表中符合条件的算法，
     * 并根据vm_algorithm_info_t表中的model_config生成sys_scene_config和sys_scene_alarm_rel表的数据
     *
     * @return 结果
     */
    @PostMapping("/video/algorithm/generateSceneAlarmFromOrderAlgorithms")
    @ApiOperation("根据订单算法信息生成场景告警配置")
    public Result<String> generateSceneAlarmFromOrderAlgorithms() {
        try {
            log.info("开始根据订单算法信息生成场景告警配置");

            // 1. 查询大模型算法
            List<String> algorithmCodeList = sysSceneAlarmMapper.getLargeModelAlgorithms();

            if (CollectionUtils.isEmpty(algorithmCodeList)) {
                log.warn("字典参数model_config_list为空，无法生成场景告警配置");
                return Result.fail("字典参数model_config_list为空");
            }

            log.info("从字典中获取到算法编码列表，数量：{}", algorithmCodeList.size());

            // 2. 查询vm_order_algorithms表，获取行业和版本数据
            // 条件：algorithm_code在字典值中，ord_status在0,1,2中，delete_flag=0
            List<OrderAlgorithmDTO> orderAlgorithmList = sysSceneAlarmMapper.selectOrderAlgorithmsByAlgorithmCodes(algorithmCodeList);

            if (CollectionUtils.isEmpty(orderAlgorithmList)) {
                log.warn("未找到符合条件的订单算法数据");
                return Result.fail("未找到符合条件的订单算法数据");
            }

            log.info("查询到订单算法数据，数量：{}", orderAlgorithmList.size());

            // 3. 对每个订单算法记录处理，根据model_config生成sys_scene_config和sys_scene_alarm_rel表的数据
            // 使用Set去重，避免重复处理相同的tenantId + industryCode + algorithmCode组合
            Set<String> processedKeys = new HashSet<>();
            int totalInsertCount = 0;
            int successCount = 0;
            int failCount = 0;

            for (OrderAlgorithmDTO orderAlgorithm : orderAlgorithmList) {
                String tenantId = orderAlgorithm.getTenantId();
                String industryCode = orderAlgorithm.getIndustryCode();
                String algorithmCode = orderAlgorithm.getAlgorithmCode();

                // 生成唯一key，用于去重
                String uniqueKey = tenantId + "|" + industryCode + "|" + algorithmCode;

                // 如果已经处理过相同的组合，跳过
                if (processedKeys.contains(uniqueKey)) {
                    log.debug("跳过已处理的组合，tenantId={}, industryCode={}, algorithmCode={}",
                            tenantId, industryCode, algorithmCode);
                    continue;
                }

                // 标记为已处理
                processedKeys.add(uniqueKey);

                log.info("开始处理，tenantId={}, industryCode={}, algorithmCode={}",
                        tenantId, industryCode, algorithmCode);

                try {
                    int insertCount = sysSceneAlarmRepository.generateSceneAlarmFromModelConfigWithParams(
                            tenantId,
                            industryCode,
                            algorithmCode
                    );

                    if (insertCount > 0) {
                        totalInsertCount += insertCount;
                        successCount++;
                        log.info("成功生成场景告警配置，tenantId={}, industryCode={}, algorithmCode={}, 插入数量={}",
                                tenantId, industryCode, algorithmCode, insertCount);
                    } else {
                        log.info("无需生成场景告警配置（已存在或数据为空），tenantId={}, industryCode={}, algorithmCode={}",
                                tenantId, industryCode, algorithmCode);
                    }
                } catch (Exception e) {
                    failCount++;
                    log.error("生成场景告警配置失败，tenantId={}, industryCode={}, algorithmCode={}",
                            tenantId, industryCode, algorithmCode, e);
                }
            }

            String message = String.format("处理完成，成功：%d条，失败：%d条，总插入数量：%d",
                    successCount, failCount, totalInsertCount);
            log.info(message);

            return Result.success(message);
        } catch (Exception e) {
            log.error("根据订单算法信息生成场景告警配置异常", e);
            return Result.fail("生成场景告警配置失败：" + e.getMessage());
        }
    }
}
