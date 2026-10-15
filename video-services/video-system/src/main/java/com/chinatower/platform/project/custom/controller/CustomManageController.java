package com.chinatower.platform.project.custom.controller;

import com.alibaba.fastjson.JSON;
import com.chinatower.common.entity.Result;
import com.chinatower.common.page.TableDataInfo;
import com.chinatower.controller.TowerBaseController;
import com.chinatower.device.entity.vo.PlatformInfoVo;
import com.chinatower.platform.constants.Constants;
import com.chinatower.platform.project.custom.entity.*;
import com.chinatower.platform.project.custom.service.ICustomManageService;
import com.chinatower.platform.project.system.domain.SysConfig;
import com.chinatower.platform.project.system.service.ISysConfigService;
import com.chinatower.system.api.model.AlarmTypeReqVo;
import com.chinatower.system.api.model.UpdateTenantReqVO;
import com.chinatower.system.api.request.tenant.TenantMapConfigInfoSubVO;
import com.chinatower.system.api.request.tenant.TenantMapConfigQueryVO;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import java.util.List;
import java.util.Map;


/**
 * video-system
 * @Author：liuxt
 * @Description：客户管理
 * @Date：2022/8/5 11:34
 * @requestID  中国铁塔_铁塔视联平台__运营管理-客户管理_需求
 */
@Slf4j
@RestController
@RequestMapping("/custom/manage")
public class CustomManageController extends TowerBaseController {
    @Autowired
    private ICustomManageService customCommonService;
    @Autowired
    private ISysConfigService sysConfigService;

    private static final String DEPENDENCE_MANAGE = "dependence_manage";

    /**
     * 查询客户树
     * @Author：liuxt
     * @Description：查询客户树
     * @Date：2022/8/5 11:34
     * @param tenantTreeReqVO
     * @return Result
     * @requestID  中国铁塔_铁塔视联平台__运营管理-客户管理_需求
     */
    @PostMapping(value = "/getTenantTree")
    public Result getTenantTree(@RequestBody TenantTreeReqVO tenantTreeReqVO){
        return customCommonService.getTenantTree(tenantTreeReqVO);
    }

    /**
     * 批量更新客户信息
     * @Author：liuxt
     * @Description：批量更新客户信息
     * @Date：2022/8/5 11:34
     * @param updateTenantReqVO
     * @return Result
     * @requestID  中国铁塔_铁塔视联平台__运营管理-客户管理_需求
     */
    @PostMapping(value = "/updateTenantInfoBatch")
    public Result<Integer> updateTenantInfoBatch(@RequestBody UpdateTenantReqVO updateTenantReqVO){
        return customCommonService.updateTenantInfoBatch(updateTenantReqVO);
    }

    /**
     * 批量运营研判同步上级节点入口
     * @param updateTenantReqVO
     * @return
     */
    @PostMapping(value = "/updateTenantInfoBatchSync")
    public Result<Integer> updateTenantInfoBatchSync(@RequestBody UpdateTenantReqVO updateTenantReqVO){
        return customCommonService.updateTenantInfoBatch(updateTenantReqVO);
    }


    /**
     * 获取订单客户列表数据
     * @Author：liuxt
     * @Description：获取订单客户列表数据
     * @Date：2022/8/5 11:34
     * @param tenantTreeReqVO
     * @return TableDataInfo
     * @requestID  中国铁塔_铁塔视联平台__运营管理-客户管理_需求
     */
    @PostMapping(value = "/getOrderTenantInfo")
    public TableDataInfo getOrderTenantInfo(@RequestBody TenantTreeReqVO tenantTreeReqVO){
        try {
            startPage();
            boolean isDependenceFlag =false;
            SysConfig dependenceManageConfig = sysConfigService.selectConfigByKey(DEPENDENCE_MANAGE);
            if (dependenceManageConfig != null) {
                if (dependenceManageConfig.getConfigValue().equals(Constants.ZERO)){
                    isDependenceFlag = true;
                }
                if (dependenceManageConfig.getConfigValue().equals(Constants.ONE)){
                    tenantTreeReqVO.setIsCenterNodeFlag(true);
                }
            }
            logger.info("getOrderTenantInfo dependenceManageConfig.getConfigValue() isDependenceFlag:{}",isDependenceFlag);
            if (isDependenceFlag) {
                //属地化铁塔视角同权
                return getDataTable(customCommonService.getOrderTenantInfoCommon(tenantTreeReqVO));
            } else {
                //云化走权限
                return getDataTable(customCommonService.getOrderTenantInfo(tenantTreeReqVO));
            }
        } catch (Exception e) {
            log.error("CustomManageController getOrderTenantInfo error:", e);
            return getDataTable(Lists.newArrayList());
        }
    }

    @PostMapping(value = "/queryTenantInfoByPlatCode")
    public TableDataInfo queryTenantInfoByPlatCode(@RequestBody TenantTreeReqVO tenantTreeReqVO){
        try {
            startPage();
            boolean isDependenceFlag =false;
            SysConfig dependenceManageConfig = sysConfigService.selectConfigByKey(DEPENDENCE_MANAGE);
            if (dependenceManageConfig != null) {
                if (dependenceManageConfig.getConfigValue().equals(Constants.ZERO)){
                    isDependenceFlag = true;
                }
                if (dependenceManageConfig.getConfigValue().equals(Constants.ONE)){
                    tenantTreeReqVO.setIsCenterNodeFlag(true);
                }
            }
            logger.info("queryTenantInfoByPlatCode dependenceManageConfig.getConfigValue() isDependenceFlag:{}",isDependenceFlag);
            if (isDependenceFlag) {
                //属地化铁塔视角同权
                return getDataTable(customCommonService.queryTenantInfoByPlatCodeCommon(tenantTreeReqVO));
            } else {
                //云化走权限
                return getDataTable(customCommonService.queryTenantInfoByPlatCode(tenantTreeReqVO));
            }
        } catch (Exception e) {
            log.error("CustomManageController queryTenantInfoByPlatCode error:", e);
            return getDataTable(Lists.newArrayList());
        }
    }


    /**
     * 关键字查询租户信息列表(铁塔视角)
     * @Author：liuxt
     * @Description：关键字查询租户信息列表(铁塔视角)
     * @Date：2022/8/5 11:34
     * @param tenantVO
     * @return Result
     * @requestID  中国铁塔_铁塔视联平台__运营管理-客户管理_需求
     */
    @PostMapping(value = "/queryTenantInfo")
    public Result queryTenantInfo(@RequestBody TenantVO tenantVO) {
        boolean isDependenceFlag =false;
        SysConfig dependenceManageConfig = sysConfigService.selectConfigByKey(DEPENDENCE_MANAGE);
        if (dependenceManageConfig != null) {
            if (dependenceManageConfig.getConfigValue().equals(Constants.ZERO)){
                isDependenceFlag = true;
            }
        }
        logger.info("queryTenantInfo dependenceManageConfig.getConfigValue() isDependenceFlag:{}", isDependenceFlag);
        if (isDependenceFlag) {
            //属地化铁塔视角同权
            return customCommonService.queryTenantInfoCommon(tenantVO);
        } else {
            //云化走权限
            return customCommonService.queryTenantInfo(tenantVO);
        }

    }


    /**
     * 获取权限配置
     * @Author：liuxt
     * @Description：获取权限配置
     * @Date：2022/8/5 11:34
     * @param vo
     * @return Result
     * @requestID  中国铁塔_铁塔视联平台__运营管理-客户管理_需求
     */
    @PostMapping(value = "/getAuthConfig")
    public Result getAuthConfig(@RequestBody AuthRegisterApiVO vo) {

        return customCommonService.getAuthConfig(vo);
    }


    /**
     * 保存api配置
     * @Author：liuxt
     * @Description：保存api配置
     * @Date：2022/8/5 11:34
     * @param vo
     * @return Result
     * @requestID  中国铁塔_铁塔视联平台__运营管理-客户管理_需求
     */
    @PostMapping(value = "/createAuthConfig")
    public Result createAuthConfig(@RequestBody AuthRegisterApiVO vo) {
        return customCommonService.createAuthConfig(vo);
    }

    /**
     * 更新租户信息(新)
     * @Author：liuxt
     * @Description 更新租户信息(新)
     * @Date：2022/8/5 11:34
     * @param alarmTypeReqVo
     * @return Result
     * @requestID  中国铁塔_铁塔视联平台__运营管理-客户管理_需求
     */
    @PostMapping(value = "/updateTenantInfo")
    public Result<String> updateTenantInfo(@RequestBody AlarmTypeReqVo alarmTypeReqVo){
        return customCommonService.updateTenantInfoNew(alarmTypeReqVo);
    }

    /**
     * 运营研判操作同步上级节点入口
     * @param alarmTypeReqVo
     * @return
     */
    @PostMapping(value = "/updateTenantInfoSync")
    public Result<String> updateTenantInfoSync(@RequestBody AlarmTypeReqVo alarmTypeReqVo){
        try {
            Result<String> result = customCommonService.updateTenantInfoNew(alarmTypeReqVo);
            log.info("CustomManageController updateTenantInfoSync param:{}, result:{}",
                    JSON.toJSONString(alarmTypeReqVo), (result != null ? JSON.toJSONString(result) : null));
            return result;
        } catch (Exception e) {
            log.error("CustomManageController updateTenantInfoSync error, e:", e);
            return Result.fail(e.getMessage());
        }
    }

    /**
     * 查询当前节点以及所有上级节点信息
     * @param platformInfoReqVo 入参
     * @return Result<List<PlatformInfoReqVO>>
     */
    @PostMapping("/queryPlatformList")
    public Result<List<PlatformInfoVo>> queryPlatformList(@RequestBody PlatformInfoVo platformInfoReqVo){
        return customCommonService.queryAllLocationCode(platformInfoReqVo);
    }

    /**
     * 批量研判-查询客户列表
     * @param reqVO
     * @return
     */
    @PostMapping("/queryTenantList")
    public Result<PageInfo<TenantLocationRelVO>> queryTenantList(@RequestBody TenantLocationReqVO reqVO){
        return customCommonService.queryTenantList(reqVO);
    }

    /**
     * 运营研判推送列表查询
     * @param operationPushRelVO
     * @return
     */
    @PostMapping("/queryOperationPushList")
    public TableDataInfo queryOperationPushList(@RequestBody OperationPushRelVO operationPushRelVO){
        startPage();
        return getDataTable(customCommonService.queryOperationPushList(operationPushRelVO));
    }

    /**
     * 手动推送接口
     * @param operationPushRelVO
     * @return
     */
    @PostMapping("/pushOperationInfo")
    public Result<String> pushOperationInfo(@RequestBody OperationPushRelVO operationPushRelVO){
        return customCommonService.pushOperationInfo(operationPushRelVO);
    }


    /**
     * 第三方告警配置
     * @param thirdAlarmInfoReq
     * @return
     */
    @PostMapping("/updateThirdAlarm")
    public Result updateThirdAlarmInfo(@RequestBody ThirdAlarmInfoVO thirdAlarmInfoReq){
        return customCommonService.updateThirdAlarm(thirdAlarmInfoReq);
    }

    /**
     * 查询第三方告警信息
     * @param thirdAlarmInfoReq
     * @return
     */
    @PostMapping("/queryThirdAlarm")
    public Result<ThirdAlarmInfoVO> queryThirdAlarmInfo(@RequestBody ThirdAlarmInfoVO thirdAlarmInfoReq){
        return customCommonService.queryThirdAlarm(thirdAlarmInfoReq);
    }


    /**
     * 获取订单信息
     * @Author：liuxt
     * @Description：获取订单信息
     * @Date：2022/8/5 11:34
     * @param tenantOrderVO
     * @return Result
     * @requestID  中国铁塔_铁塔视联平台__运营管理-客户管理_需求
     */
    @PostMapping(value ="/queryTenantOrder")
    public Result queryTenantOrder(@RequestBody TenantOrderVO tenantOrderVO){
        return customCommonService.queryTenantOrder(tenantOrderVO);
    }


    /**
     * 查询API配置信息
     * @Author：liuxt
     * @Description：查询API配置信息
     * @Date：2022/8/5 11:34
     * @param apiIntfInfoVO
     * @return Result
     * @requestID  中国铁塔_铁塔视联平台__运营管理-客户管理_需求
     */
    @PostMapping(value = "/getApiIntfInfo")
    public Result getApiIntfInfo(@RequestBody ApiIntfInfoVO apiIntfInfoVO){
        return customCommonService.getApiIntfInfo(apiIntfInfoVO);
    }



    /**
     * 保存api配置(暴露给订购失效调用)
     * @Author：liuxt
     * @Description：保存api配置(暴露给订购失效调用)
     * @Date：2022/8/5 11:34
     * @param vo
     * @return Result
     * @requestID  中国铁塔_铁塔视联平台__运营管理-客户管理_需求
     */
    @PostMapping(value = "/syncApiInfo")
    public Result syncApiInfo(@RequestBody ApiVO vo) {
        return customCommonService.syncApiInfo(vo);
    }

    /**
     * 根据客户编码判断是否开启API(外部系统调用)
     * @Author：zhaozj
     * @Description：根据客户编码判断是否开启API(外部系统调用)
     * @Date：2022/9/1 11:34
     * @param vo
     * @return Result
     * @requestID  中国铁塔_铁塔视联平台__运营管理-客户管理_需求
     */
    @PostMapping(value = "/ifOpenApi")
    public Result ifOpenApi(@RequestBody ApiVO vo) {
        return customCommonService.ifOpenApi(vo);
    }



    /**
     * 获取全部算法信息树
     * @Author：liuxt
     * @Description：全部算法信息树
     * @Date：2023/2/6 11:34
     * @param
     * @return Result<Map>
     * @requestID  中国铁塔_铁塔视联平台_全部算法信息树接口
     */
    @PostMapping(value = "/getAllAlgorithmInfoTree")
    @ApiOperation("获取全部算法信息树接口")
    public Result<Map> getAllAlgorithmInfoTree(){
        return Result.data(customCommonService.getAllAlgorithmInfoTree());
    }

    /**
     * 铁塔视角-客户管理-保存客户地图配置
     * @param param 参数
     * @return 返回值
     */
    @PostMapping("/saveTenantMapConfig")
    public Result<String> saveTenantMapConfig(@RequestBody TenantMapConfigInfoVO param) {
        return customCommonService.saveTenantMapConfig(param);
    }

    /**
     * 铁塔视角-客户管理-查询客户地图配置
     * @param param 参数
     * @return 返回值
     */
    @PostMapping("/queryTenantMapConfigInfo")
    public Result<List<TenantMapConfigInfoVO>> queryTenantMapConfigInfo(@RequestBody TenantMapConfigQueryVO param) {
        return customCommonService.queryTenantMapConfigInfo(param);
    }

    /**
     * 客户视角-给地图查询使用
     * @param param 参数
     * @return 返回值
     */
    @PostMapping("/queryTenantMapConfigInfo4Tenant")
    public Result<Map<String, List<TenantMapConfigInfoSubVO>>> queryTenantMapConfigInfo4Tenant(@RequestBody TenantMapConfigQueryVO param) {
        return customCommonService.queryTenantMapConfigInfo4Tenant(param);
    }

    /**
     * 更新客户登录类型
     *
     * @return 操作结果，返回一个Result对象，其中包含操作状态和消息
     */
    @PostMapping(value = "/updateCustomerLoginType")
    public Result<String> updateCustomerLoginType(@RequestBody TenantVO tenantVO){
        return customCommonService.updateCustomerLoginType(tenantVO);
    }
}