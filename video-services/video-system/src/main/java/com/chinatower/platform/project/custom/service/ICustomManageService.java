package com.chinatower.platform.project.custom.service;

import com.chinatower.common.entity.Result;
import com.chinatower.device.entity.vo.PlatformInfoVo;
import com.chinatower.platform.project.custom.entity.*;
import com.chinatower.system.api.model.AlarmTypeReqVo;
import com.chinatower.system.api.model.UpdateTenantReqVO;
import com.chinatower.system.api.request.tenant.TenantMapConfigInfoSubVO;
import com.chinatower.system.api.request.tenant.TenantMapConfigQueryVO;
import com.github.pagehelper.PageInfo;

import java.util.List;
import java.util.Map;

/**
 * @Author：liuxt
 * @Description：客户管理
 * @Date：2022/8/5 11:34
 * @requestID  中国铁塔_铁塔视联平台__运营管理-客户管理_需求
 */
public interface ICustomManageService {

    /**
     * @Author：liuxt
     * @Description：查询行政区域列表
     * @Date：2022/8/5 11:34
     * @param tenantTreeReqVO
     * @return Result
     * @requestID  中国铁塔_铁塔视联平台__运营管理-客户管理_需求
     */
    Result getTenantTree(TenantTreeReqVO tenantTreeReqVO);

    /**
     * @Author：liuxt
     * @Description：查询API配置信息
     * @Date：2022/8/5 11:34
     * @param apiIntfInfoVO
     * @return Result
     * @requestID  中国铁塔_铁塔视联平台__运营管理-客户管理_需求
     */
    Result getApiIntfInfo(ApiIntfInfoVO apiIntfInfoVO);

    /**
     * @Author：liuxt
     * @Description：批量更新客户信息
     * @Date：2022/8/5 11:34
     * @param updateTenantReqVO
     * @return Result
     * @requestID  中国铁塔_铁塔视联平台__运营管理-客户管理_需求
     */
    Result<Integer> updateTenantInfoBatch(UpdateTenantReqVO updateTenantReqVO);

    /**
     * @Author：liuxt
     * @Description：获取订单客户列表数据
     * @Date：2022/8/5 11:34
     * @param tenantTreeReqVO
     * @return List<TenantMangeResultVO>
     * @requestID  中国铁塔_铁塔视联平台__运营管理-客户管理_需求
     */
    List<TenantMangeResultVO> getOrderTenantInfo(TenantTreeReqVO tenantTreeReqVO);

    List<TenantMangeResultVO>  getOrderTenantInfoCommon(TenantTreeReqVO tenantTreeReqVO);

    List<TenantMangeResultVO> queryTenantInfoByPlatCode(TenantTreeReqVO tenantTreeReqVO);

    List<TenantMangeResultVO>  queryTenantInfoByPlatCodeCommon(TenantTreeReqVO tenantTreeReqVO);

    /**
     * @Author：liuxt
     * @Description：关键字查询租户信息列表(铁塔视角)
     * @Date：2022/8/5 11:34
     * @param tenantVO
     * @return Result
     * @requestID  中国铁塔_铁塔视联平台__运营管理-客户管理_需求
     */
    Result queryTenantInfo(TenantVO tenantVO);

    Result queryTenantInfoCommon(TenantVO tenantVO);

    /**
     * @Author：liuxt
     * @Description：保存api配置
     * @Date：2022/8/5 11:34
     * @param vo
     * @return Result
     * @requestID  中国铁塔_铁塔视联平台__运营管理-客户管理_需求
     */
    Result createAuthConfig(AuthRegisterApiVO vo);

    /**
     * @Author：liuxt
     * @Description：更新租户信息
     * @Date：2022/8/5 11:34
     * @param map
     * @return Result
     * @requestID  中国铁塔_铁塔视联平台__运营管理-客户管理_需求
     */
    Result  updateTenantInfo(Map map);

    /**
     * @Author：liuxt
     * @Description：更新租户信息(新)
     * @Date：2022/8/5 11:34
     * @param alarmTypeReqVo
     * @return Result
     * @requestID  中国铁塔_铁塔视联平台__运营管理-客户管理_需求
     */
    Result<String> updateTenantInfoNew(AlarmTypeReqVo alarmTypeReqVo);

    /**
     * 查询本节点以及上级所有节点
     */
    Result<List<PlatformInfoVo>> queryAllLocationCode(PlatformInfoVo platformInfoReqVo);

    Result<PageInfo<TenantLocationRelVO>> queryTenantList(TenantLocationReqVO reqVO);

    List<OperationPushRelVO> queryOperationPushList(OperationPushRelVO operationPushRelVO);

    Result<String> pushOperationInfo(OperationPushRelVO operationPushRelVO);

    Result updateThirdAlarm(ThirdAlarmInfoVO thirdAlarmReq);

    Result<ThirdAlarmInfoVO> queryThirdAlarm(ThirdAlarmInfoVO thirdAlarmInfoVO);
    /**
     * @Author：liuxt
     * @Description：获取权限配置
     * @Date：2022/8/5 11:34
     * @param vo
     * @return Result
     * @requestID  中国铁塔_铁塔视联平台__运营管理-客户管理_需求
     */
    Result getAuthConfig(AuthRegisterApiVO vo);

    /**
     * @Author：liuxt
     * @Description：获取订单信息
     * @Date：2022/8/5 11:34
     * @param tenantOrderVO
     * @return Result
     * @requestID  中国铁塔_铁塔视联平台__运营管理-客户管理_需求
     */
    Result queryTenantOrder(TenantOrderVO tenantOrderVO);



    /**
     * @Author：liuxt
     * @Description：保存api配置(暴露给订购失效调用)
     * @Date：2022/8/5 11:34
     * @param vo
     * @return Result
     * @requestID  中国铁塔_铁塔视联平台__运营管理-客户管理_需求
     */
    Result syncApiInfo(ApiVO vo);


    /**
     * @Author：zhaozj
     * @Description：根据客户编码判断是否开启API(外部系统调用)
     * @Date：2022/9/1 11:34
     * @param vo
     * @return Result
     * @requestID  中国铁塔_铁塔视联平台__运营管理-客户管理_需求
     */
    Result ifOpenApi(ApiVO vo);

    /**
     * @Author：liuxt
     * @Description：获取全部算法信息树接口
     * @Date：2022/8/5 11:34
     * @param
     * @return Result
     * @requestID  中国铁塔_铁塔视联平台_全部算法信息树接口
     */
    Map getAllAlgorithmInfoTree();

    Result<String> saveTenantMapConfig(TenantMapConfigInfoVO param);

    Result<List<TenantMapConfigInfoVO>> queryTenantMapConfigInfo(TenantMapConfigQueryVO param);

    Result<Map<String, List<TenantMapConfigInfoSubVO>>> queryTenantMapConfigInfo4Tenant(TenantMapConfigQueryVO param);

    Result<String> updateCustomerLoginType(TenantVO tenantVO);

}