package com.chinatower.event.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.chinatower.common.entity.Result;
import com.chinatower.common.entity.ResultCode;
import com.chinatower.common.page.TableDataInfo;
import com.chinatower.controller.TowerBaseController;
import com.chinatower.event.constants.Constants;
import com.chinatower.event.constant.TableConstant;
import com.chinatower.event.dataobject.AlarmTypeDO;
import com.chinatower.common.core.dto.AlarmTypeClassDTO;
import com.chinatower.event.dataobject.AlarmTypeLevelDO;
import com.chinatower.event.dto.AlarmTypeDataReq;
import com.chinatower.event.dto.AlarmTypeReq;
import com.chinatower.event.dto.ScreenDataConfigParticipationDTO;
import com.chinatower.event.entity.dto.AlarmSourceSubTypeDTO;
import com.chinatower.event.entity.dto.AlarmTypeDTO;
import com.chinatower.event.entity.dto.AlarmTypeTagInfoDTO;
import com.chinatower.event.entity.vo.AlarmSourceSubTypeVO;
import com.chinatower.event.entity.vo.AlarmTypeTagInfoVO;
import com.chinatower.event.repository.IAlarmTypeRepository;
import com.chinatower.event.vo.AlarmLevelVO;
import com.chinatower.event.vo.AlarmTypeClassVO;
import com.chinatower.industry.dto.AlgorithmInfoDO;
import com.chinatower.log.annotation.TowerLog;
import com.chinatower.log.enums.BusinessType;
import com.chinatower.platform.annotation.DataScope;
import com.chinatower.platform.common.exception.CustomException;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import com.chinatower.redis.enums.RedisDBType;
import com.chinatower.redis.util.RedisUtil;
import com.github.pagehelper.PageInfo;
import io.seata.common.util.NumberUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

/**
 * video-event
 * Copyright: Copyright(c) 2019 iwhalecloud
 * <p>
 * 类说明：告警类型查询
 * <p>
 * 类名称: AlarmTypeController.java
 *
 * @author su.tingning
 * @version v1.0.0
 * @date 2022/1/26 10:48
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ------------------------------------------------------------
 */
@RestController
@Slf4j
@RequestMapping(value = "/alarm/type")
@DS(EbikeDataBaseConstant.BASE)
public class AlarmTypeController extends TowerBaseController {

    @Autowired
    private IAlarmTypeRepository alarmTypeRepository;


    /**
     * 功能描述：查询订购关联的告警类型
     *
     * @param body
     * @return Result
     * @author zhen.yan
     * @date 2022/1/26 10:48
     * @version:
     */
    @ApiOperation(value = "查询订购关联的告警类型", notes = "查询订购关联的告警类型")
    @PostMapping("/findOrderAlarm")
    @DataScope(dbType = EbikeDataBaseConstant.BASE, typeValueColumn = "sat.type_value")
    public Result findOrderAlarm(@RequestBody AlarmTypeDTO body) {
        int pageNum = NumberUtils.toInt(String.valueOf(body.getPageNum()), 0);
        int pageSize = NumberUtils.toInt(String.valueOf(body.getPageSize()), 0);
        List<AlarmTypeDO> list = alarmTypeRepository.findOrderAlarm(body);
        if (pageNum == 0 && pageSize == 0) {
            return Result.data(list, "");
        }
        else {
            PageInfo<Map<String, Object>> pageInfo = new PageInfo(list);
            return Result.data(pageInfo, "");
        }
    }

    /**
     * @Description: 大屏查询一键告警功能下的告警类型
     * @Author: wangyt
     * @Date: 2025/1/22
     * @param body
     * @return com.chinatower.common.entity.Result
     **/
    @ApiOperation(value = "大屏查询一键告警功能下的告警类型", notes = "大屏查询一键告警功能下的告警类型")
    @PostMapping("/findScreenOrderAlarm")
    public Result findScreenOrderAlarm(@RequestBody AlarmTypeDTO body) {
        Set<AlarmTypeDO> resultSet = new HashSet<>();
        List<AlarmTypeDO> list = alarmTypeRepository.findOrderAlarm(body);
        if(!StringUtils.isEmpty(body.getScreenId()) && !StringUtils.isEmpty(body.getScreenType())){
            // 先查询是否开启
            Long screenDataConfigId = alarmTypeRepository.selectScreenDataConfigCount(body.getScreenId(), body.getScreenType());
            // 如果开启就根据配置显示，没开启就显示全部
            if(ObjectUtils.isNotEmpty(screenDataConfigId)){
                List<ScreenDataConfigParticipationDTO> participationList = alarmTypeRepository.selectScreenDataConfig(screenDataConfigId);
                if(!CollectionUtils.isEmpty(participationList) && !CollectionUtils.isEmpty(list) ){
                    for (ScreenDataConfigParticipationDTO participation : participationList) {
                        for (AlarmTypeDO alarmType : list) {
                            if(participation.getCode().equals(alarmType.getTypeValue())){
                                resultSet.add(alarmType);
                                break;
                            }
                        }
                    }
                }
            }else{
                return Result.data(list, "");
            }
        }
        return Result.data(resultSet, "");
    }

    /**
     * 功能描述：查询订购关联的告警类型
     * @param body
     * @return
     */
    @ApiOperation(value = "查询订购关联的告警类型", notes = "查询订购关联的告警类型")
    @PostMapping("/findOrderAlarmBySource")
    @DataScope(dbType = EbikeDataBaseConstant.BASE, tenantColumn="t1.tenant_id", industryCode = "t1.industry_code")
    public Result<PageInfo<AlarmTypeDO>> findOrderAlarmBySource(@RequestBody AlarmTypeDTO body) {
        if (body == null || CollectionUtils.isEmpty(body.getAlarmSources())) {
            throw new CustomException("告警来源参数不能为空");
        }
        if (body.getPageNum() == null) {
            body.setPageNum(Constants.PAGE_NUM);
        }
        if (body.getPageSize() == null) {
            body.setPageSize(Constants.PAGE_SIZE);
        }
        return alarmTypeRepository.findOrderAlarmBySource(body);
    }

    /**
     * 功能描述：查询订购关联的告警类型
     *
     * @param body
     * @return Result
     * @author zhen.yan
     * @date 2022/1/26 10:48
     * @version:
     */
    @ApiOperation(value = "查询订购关联的所有告警类型", notes = "查询订购关联的所有告警类型")
    @PostMapping("/findOrderAlarmAll")
    @DataScope(dbType = EbikeDataBaseConstant.BASE, tenantColumn="t1.tenant_id", industryCode = "t1.industry_code", typeValueColumn = "sat.alarm_type_id")
    public Result findOrderAlarmAll(@RequestBody AlarmTypeDTO body) {
        int pageNum = NumberUtils.toInt(String.valueOf(body.getPageNum()), 0);
        int pageSize = NumberUtils.toInt(String.valueOf(body.getPageSize()), 0);
        List<AlarmTypeDO> list = alarmTypeRepository.findOrderAlarmAll(body);
        if (pageNum == 0 && pageSize == 0) {
            return Result.data(list, "");
        }
        else {
            PageInfo<Map<String, Object>> pageInfo = new PageInfo(list);
            return Result.data(pageInfo, "");
        }
    }

    /**
     * 功能描述：查询订购关联的告警类型（摄像机和AI告警）
     * @author: yongqi.han
     * @date: 2022/8/27
     * @param body
     * @return: Result
     */
    @ApiOperation(value = "查询订购关联的告警类型", notes = "查询订购关联的告警类型")
    @PostMapping("/findAlgorithmAlarm")
    @DataScope(dbType = EbikeDataBaseConstant.BASE, tenantColumn="t1.tenant_id", industryCode = "t1.industry_code")
    public Result findAlgorithmAlarm(@RequestBody AlarmTypeDTO body) {
        int pageNum = NumberUtils.toInt(String.valueOf(body.getPageNum()), 0);
        int pageSize = NumberUtils.toInt(String.valueOf(body.getPageSize()), 0);
        List<AlarmTypeDO> list = alarmTypeRepository.findAlgorithmAlarm(body);
        if (pageNum == 0 && pageSize == 0) {
            return Result.data(list, "");
        }
        else {
            PageInfo<Map<String, Object>> pageInfo = new PageInfo(list);
            return Result.data(pageInfo, "");
        }
    }

    /**
     * 功能描述：事件告警类型推送
     * @param: req
     * @return: Result
     * @author: zheng.yan
     * @date: 2022/2/18
     * @version:
     */
    @PostMapping("/pushWarningType")
    public Result pushWarningType(@RequestBody AlarmTypeReq req) {
        log.info("事件告警类型推送：" + JSON.toJSONString(req));
        try {
            List<AlarmTypeDO> insertList = new ArrayList<>(req.getData().size());
            List<AlarmTypeDO> updateList = new ArrayList<>(req.getData().size());
            List<Long> deleteList = new ArrayList<>(req.getData().size());

            List<String> alarmTypeCodes = req.getData().stream().map(AlarmTypeDataReq::getAlarmTypeCode).collect(Collectors.toList());
            QueryWrapper<AlarmTypeDO> alarmTypeQuery = new QueryWrapper<>();
            alarmTypeQuery.in("type_value", alarmTypeCodes);
            alarmTypeQuery.eq(TableConstant.FIELD_DELETE_FLAG, Constants.VALID_STATE);
            List<AlarmTypeDO> existingAlarmTypes = alarmTypeRepository.list(alarmTypeQuery);
            Date now = new Date();
            for (AlarmTypeDataReq alarmTypeData : req.getData()) {
                AlarmTypeDO alarmType = this.getAlarmTypeByAlarmTypeCode(existingAlarmTypes, alarmTypeData.getAlarmTypeCode());
                if (alarmTypeData.getOptType() == 1 || alarmTypeData.getOptType() == 2) {
                    AlarmTypeDO newAlarmTypeInfo = this.buildAlarmTypeByReq(alarmTypeData);
                    // 新增情况下
                    if (alarmType == null) {
                        newAlarmTypeInfo.setCreateTime(now);
                        insertList.add(newAlarmTypeInfo);
                    }
                    else {
                        newAlarmTypeInfo.setAlarmTypeId(alarmType.getAlarmTypeId());
                        newAlarmTypeInfo.setUpdateTime(now);
                        updateList.add(newAlarmTypeInfo);
                    }
                }
                else if (alarmTypeData.getOptType() == 3) {
                    if (alarmType != null) {
                        deleteList.add(alarmType.getAlarmTypeId());
                    }
                }
            }
            if (!insertList.isEmpty()) {
                alarmTypeRepository.saveBatch(insertList);
            }
            if (!updateList.isEmpty()) {
                alarmTypeRepository.updateBatchById(updateList);
            }
            if (!deleteList.isEmpty()) {
                //物理删除修改为逻辑删除 add by liuxt 20230314
                alarmTypeRepository.delAlarmTypeBatch(deleteList);
            }
        }
        catch (Exception e) {
            log.error("pushWarningType error:", e);
            return Result.fail(e.getMessage());
        }
        return Result.success("");
    }

    /**
     * 功能描述：告警类型列表查询
     *
     * @param dto
     * @return TableDataInfo
     * @author sutn
     * @date 15:24 2022/2/24
     * @version:
     */
    @ApiOperation(value = "告警类型列表查询", notes = "告警类型列表查询")
    @PostMapping("/findList")
    @DataScope(dbType = EbikeDataBaseConstant.BASE, typeValueColumn = "sat.type_value")
    public TableDataInfo findList(@RequestBody AlarmTypeClassDTO dto) {
        startPage();
        List<AlarmTypeClassVO> list = alarmTypeRepository.findList(dto);
        return getDataTable(list);
    }

    @ApiOperation(value = "获取告警等级", notes = "获取告警等级")
    @PostMapping("/getAlarmLevelByTypeValue")
    public AlarmLevelVO getAlarmLevelByTypeValue(@RequestBody AlarmTypeLevelDO body) {
        return alarmTypeRepository.getAlarmLevelByTypeValue(body);
    }

    @ApiOperation(value = "修改告警等级", notes = "修改告警等级")
    @PostMapping("/updateAlarmLevelByTypeValue")
    public Result<String> updateAlarmLevelByTypeValue(@RequestBody AlarmTypeLevelDO dto) {
        return alarmTypeRepository.updateAlarmLevelByTypeValue(dto);
    }

    private AlarmTypeDO getAlarmTypeByAlarmTypeCode(List<AlarmTypeDO> alarmTypes, String alarmTypeCode) {
        AlarmTypeDO funcType = null;
        for (AlarmTypeDO alarmType : alarmTypes) {
            if (alarmTypeCode.equals(alarmType.getTypeValue())) {
                funcType = alarmType;
                break;
            }
        }
        return funcType;
    }

    private AlarmTypeDO buildAlarmTypeByReq(AlarmTypeDataReq alarmTypeReq) {
        AlarmTypeDO alarmType = new AlarmTypeDO();
        //add by liuxt 修改告警来源alarm_source为空的bug start
//        String typeValue = alarmTypeReq.getAlarmTypeCode();
//        if (!StringUtils.isEmpty(typeValue)) {
//            if(typeValue.startsWith(Constants.EIGHTEEN)){//如果type_value是以18开头 则告警来源置为4(前端告警)
//                alarmType.setAlarmSource(Constants.FOUR);
//            }else{//否则告警来源置为1(AI告警)
//                alarmType.setAlarmSource(Constants.YES_FLAG);
//            }
//        }
        //add by liuxt 修改告警来源alarm_source为空的bug end
        alarmType.setAlarmSource(String.valueOf(alarmTypeReq.getSourceType()));
        alarmType.setAlarmTypeClassId(String.valueOf(alarmTypeReq.getSourceType()));
        alarmType.setTypeValue(alarmTypeReq.getAlarmTypeCode());
        alarmType.setTypeName(alarmTypeReq.getAlarmTypeName());
        alarmType.setSource(Constants.VALID_STATE);
        alarmType.setIfFree(Constants.VALID_STATE);
        alarmType.setDeleteFlag(Integer.valueOf(Constants.VALID_STATE));
        return alarmType;
    }

    /**
     * 功能描述：缓存清理
     *
     * @param cacheKey 缓存key
     * @return Result
     * @author zhen.yan
     * @date 2022/6/15 11:25
     * @version:
     */
    @GetMapping("/clearCache/{cacheKey}")
    public Result clearCache(@PathVariable(value = "cacheKey") String cacheKey) {
        if (StringUtils.isNotEmpty(cacheKey)) {
            RedisUtil.delete(cacheKey, RedisDBType.PRIMARY);
        }
        return Result.success(ResultCode.SUCCESS);
    }

    /**
     * 功能描述：获取缓存
     *
     * @param cacheKey 缓存key
     * @return Result
     * @author zhen.yan
     * @date 2022/6/15 11:25
     * @version:
     */
    @GetMapping("/getCache/{cacheKey}")
    public Result getCache(@PathVariable(value = "cacheKey") String cacheKey) {
        return Result.data(RedisUtil.get(cacheKey, RedisDBType.PRIMARY));
    }

    /**
     * 功能描述：查询客户的告警来源及告警类型
     * @param body
     * @return
     */
    @ApiOperation(value = "查询客户的告警来源及告警类型", notes = "查询客户的告警来源及告警类型")
    @PostMapping("/findAlarmSourceSubType")
    public Result<List<AlarmSourceSubTypeVO>> findAlarmSourceSubType(@RequestBody AlarmSourceSubTypeDTO body) {
        if (StringUtils.isEmpty(body.getTenantId())) {
            return Result.fail("客户id不能为空");
        }
        if (StringUtils.isEmpty(body.getIndustryCode())) {
            return Result.fail("行业编码不能为空");
        }
        return Result.data(alarmTypeRepository.findAlarmSourceSubType(body));
    }

    /**
     * 功能描述：根据告警类型编码列表查询客户告警标签关联信息
     * @param param
     * @return
     */
    @ApiOperation(value = "根据告警类型编码列表查询客户告警标签关联信息")
    @PostMapping("/selectRelateTagInfoByAlarmType")
    public Result<List<AlarmTypeTagInfoVO>> selectRelateTagInfoByAlarmType(@RequestBody AlarmTypeTagInfoDTO param) {
        return Result.data(alarmTypeRepository.selectRelateTagInfoByAlarmType(param));
    }

    /**
     * 功能描述：根据客户信息查询告警类型标签信息
     * @return
     */
    @ApiOperation(value = "根据客户信息查询告警类型标签信息")
    @GetMapping("/selectTagInfoByTenant")
    public Result<List<AlarmTypeTagInfoVO>> selectTagInfoByTenant() {
        return Result.data(alarmTypeRepository.selectTagInfoByTenant());
    }

    /**
     * 功能描述：查询客户的告警来源及告警类型
     * @param body
     * @return
     */
    @ApiOperation(value = "查询客户的告警类型(AI,摄像机,卫星)", notes = "查询客户的告警类型(AI,摄像机,卫星)")
    @PostMapping("/findAlarmTypeByTenant")
    public TableDataInfo findAlarmTypeByTenant(@RequestBody AlarmSourceSubTypeDTO body) {
        return getDataTable(alarmTypeRepository.findAlarmTypeByTenant(body));
    }

    /**
     * 功能描述：修改告警类型标签是否可见
     * @param body
     * @return
     */
    @ApiOperation(value = "修改告警类型标签是否可见", notes = "修改告警类型标签是否可见")
    @PostMapping("/modifyIsVisible")
    @TowerLog(title = "修改告警类型标签是否可见", businessType = BusinessType.UPDATE)
    public Result modifyIsVisible(@RequestBody AlarmTypeClassDTO body) {
        return alarmTypeRepository.modifyIsVisible(body);
    }

    /**
     * 功能描述：新增告警类型
     * @param alarmTypeList
     * @return
     */
    @ApiOperation(value = "新增告警类型", notes = "新增告警类型")
    @PostMapping("/addAlarmType")
    public Result<String> addAlarmType(@RequestBody List<AlarmTypeDO> alarmTypeList) {
        return alarmTypeRepository.addAlarmType(alarmTypeList);
    }

    @ApiOperation(value = "校验告警类型名称", notes = "校验告警类型名称")
    @PostMapping("/checkAlarmTypeName")
    public Result<Integer> checkAlarmType(@RequestBody AlarmTypeDO param) {
        return alarmTypeRepository.checkAlarmType(param);
    }

    @ApiOperation(value = "查询大模型算法列表", notes = "查询大模型算法列表")
    @GetMapping("/queryModelAlgorithmInfoList")
    public Result<List<AlgorithmInfoDO>> queryModelAlgorithmInfoList() {
        return Result.data(alarmTypeRepository.queryModelAlgorithmInfoList());
    }

    /**
     * 客户下游任务下拉（仅订购大模型时有数据，支持 typeName 模糊）
     */
    @ApiOperation(value = "查询订购大模型的下游任务", notes = "客户视角独立「下游任务」筛选项；未订购大模型返回空列表")
    @PostMapping("/findOrderSubSceneAlarm")
    public Result findOrderSubSceneAlarm(@RequestBody AlarmTypeDTO body) {
        int pageNum = NumberUtils.toInt(String.valueOf(body.getPageNum()), 0);
        int pageSize = NumberUtils.toInt(String.valueOf(body.getPageSize()), 0);
        List<AlarmTypeDO> list = alarmTypeRepository.findOrderSubSceneAlarm(body);
        if (pageNum == 0 && pageSize == 0) {
            return Result.data(list, "");
        }
        PageInfo<Map<String, Object>> pageInfo = new PageInfo(list);
        return Result.data(pageInfo, "");
    }
}