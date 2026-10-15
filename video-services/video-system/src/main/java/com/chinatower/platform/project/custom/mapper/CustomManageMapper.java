package com.chinatower.platform.project.custom.mapper;


import com.chinatower.device.entity.vo.FunctionTypeVO;
import com.chinatower.device.entity.vo.PlatformInfoVo;
import com.chinatower.platform.project.custom.entity.*;
import com.chinatower.system.api.model.AlarmTypeReqVo;
import com.chinatower.system.api.model.TenantInfoVO;
import com.chinatower.system.api.model.UpdateTenantReqVO;
import com.chinatower.system.api.request.tenant.TenantMapConfigQueryVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @Author：liuxt
 * @Description：客户管理
 * @Date：2022/8/5 11:34
 * @requestID  中国铁塔_铁塔视联平台__运营管理-客户管理_需求
 */
public interface CustomManageMapper {
    /**
     * @Author：liuxt
     * @Description：查询行政区域列表
     * @Date：2022/8/5 11:34
     * @param tenantTreeReqVO
     * @return Result
     * @requestID  中国铁塔_铁塔视联平台__运营管理-客户管理_需求
     */
    List<TenantTreeResultVO> getTenantTree(@Param("param") TenantTreeReqVO tenantTreeReqVO);

    /**
     * @Author：liuxt
     * @Description：查询api信息
     * @Date：2022/8/5 11:34
     * @param apiIntfInfoVO
     * @return Result
     * @requestID  中国铁塔_铁塔视联平台__运营管理-客户管理_需求
     */
    List<ApiIntfInfoVO> getApiIntfInfo(ApiIntfInfoVO apiIntfInfoVO);

    /**
     * @Author：liuxt
     * @Description：获取订单客户列表数据
     * @Date：2022/8/5 11:34
     * @param tenantTreeReqVO
     * @return Result
     * @requestID  中国铁塔_铁塔视联平台__运营管理-客户管理_需求
     */
    List<TenantMangeResultVO> getOrderTenantInfo(TenantTreeReqVO tenantTreeReqVO);

    List<TenantMangeResultVO> selectTenantInfoList(TenantTreeReqVO tenantTreeReqVO);

    List<String> queryTenantIdListByPlatCode(@Param("platCode")String platCode);

    /**
     * @Author：liuxt
     * @Description：获取订单数
     * @Date：2022/8/5 11:34
     * @param list
     * @return List<Map>
     * @requestID  中国铁塔_铁塔视联平台__运营管理-客户管理_需求
     */
    List<Map> getOrderNum(List<String> list);

    /**
     * @Author：liuxt
     * @Description：获取有效订单数
     * @Date：2022/8/5 11:34
     * @param list
     * @return List<Map>
     * @requestID  中国铁塔_铁塔视联平台__运营管理-客户管理_需求
     */
    List<Map> getValidOrderNum(List<String> list);

    /**
     * @Author：liuxt
     * @Description：获取设备数
     * @Date：2022/8/5 11:34
     * @param list
     * @return List<Map>
     * @requestID  中国铁塔_铁塔视联平台__运营管理-客户管理_需求
     */
    List<Map> getDevNum(List<String> list);

    /**
     * @Author：liuxt
     * @Description：批量更新客户信息
     * @Date：2022/8/5 11:34
     * @param updateTenantReqVO
     * @return int
     * @requestID  中国铁塔_铁塔视联平台__运营管理-客户管理_需求
     */
    int updateTenantInfoBatch(UpdateTenantReqVO updateTenantReqVO);

    /**
     * @Description：批量更新租户运营信息
     * @param tenantInfoList
     * @return
     */
    int updateTenantOperationList(@Param("list") List<TenantInfoVO> tenantInfoList);

//    /**
//     * @Description：批量插入租户运营信息
//     * @param  alarmTypeReqVoList
//     * @return
//     */
//    int insertTenantOperationList(@Param("alarmTypeReqVoList") List<AlarmTypeReqVo> alarmTypeReqVoList);

    /**
     * @Author：liuxt
     * @Description：查询客户信息
     * @Date：2022/8/5 11:34
     * @param tenantVO
     * @return List<TenantVO>
     * @requestID  中国铁塔_铁塔视联平台__运营管理-客户管理_需求
     */
    List<TenantVO> queryTenantInfo(@Param("param") TenantVO tenantVO);

    /**
     * @Author：liuxt
     * @Description：查询api配置
     * @Date：2022/8/5 11:34
     * @param vo
     * @return AuthRegisterApiVO
     * @requestID  中国铁塔_铁塔视联平台__运营管理-客户管理_需求
     */
    AuthRegisterApiVO getAuthConfig(AuthRegisterApiVO vo);

    /**
     * @Author：liuxt
     * @Description：查询api配置
     * @Date：2022/8/5 11:34
     * @param authRegisterFuncApiVO
     * @return List<AuthRegisterFuncApiVO>
     * @requestID  中国铁塔_铁塔视联平台__运营管理-客户管理_需求
     */
    List<AuthRegisterFuncApiVO> getAuthConfigFunc(AuthRegisterFuncApiVO authRegisterFuncApiVO);


    /**
     * @Author：liuxt
     * @Description：查询api配置信息和注册功能（主副表关联查询）
     * @Date：2022/8/5 11:34
     * @param authRegisterFuncApiVO
     * @return List<AuthRegisterFuncApiVO>
     * @requestID  中国铁塔_铁塔视联平台__运营管理-客户管理_需求
     */
    List<AuthRegisterFuncApiVO> getApiAndFuncInfo(AuthRegisterFuncApiVO authRegisterFuncApiVO);

    /**
     * @Author：liuxt
     * @Description：插入api配置
     * @Date：2022/8/5 11:34
     * @param vo
     * @return List<AuthRegisterFuncApiVO>
     * @requestID  中国铁塔_铁塔视联平台__运营管理-客户管理_需求
     */
    int insertAuthRegister(AuthRegisterApiVO vo);

    /**
     * @Author：liuxt
     * @Description：批量插入api配置
     * @Date：2022/8/5 11:34
     * @param list
     * @return List<AuthRegisterFuncApiVO>
     * @requestID  中国铁塔_铁塔视联平台__运营管理-客户管理_需求
     */
    int insertAuthRegisterFunc(List<AuthRegisterFuncApiVO> list);

    /**
     * @Author：liuxt
     * @Description：更新api配置
     * @Date：2022/8/5 11:34
     * @param vo
     * @return int
     * @requestID  中国铁塔_铁塔视联平台__运营管理-客户管理_需求
     */
    int updateAuthRegisterNew(AuthRegisterApiVO vo);

    /**
     * @Author：liuxt
     * @Description：更新api配置
     * @Date：2022/8/5 11:34
     * @param vo
     * @return int
     * @requestID  中国铁塔_铁塔视联平台__运营管理-客户管理_需求
     */
    int updateAuthRegisterFunc(AuthRegisterApiVO vo);

    /**
     * @Author：liuxt
     * @Description：更新api配置
     * @Date：2022/8/5 11:34
     * @param vo
     * @return int
     * @requestID  中国铁塔_铁塔视联平台__运营管理-客户管理_需求
     */
    int updateAuthRegisterFuncStatus(AuthRegisterFuncApiVO vo);

    /**
     * @Author：liuxt
     * @Description：getEnumPaths
     * @Date：2022/8/5 11:34
     * @param map
     * @return int
     * @requestID  中国铁塔_铁塔视联平台__运营管理-客户管理_需求
     */
    List<String> getEnumPaths(Map map);

    /**
     * @Author：liuxt
     * @Description：getAuthRegister
     * @Date：2022/8/5 11:34
     * @param map
     * @return Map
     * @requestID  中国铁塔_铁塔视联平台__运营管理-客户管理_需求
     */
    Map getAuthRegister(Map map);


    /**
     * @Author：liuxt
     * @Description：新增调用日志
     * @Date：2022/8/5 11:34
     * @param map
     * @return Map
     * @requestID  中国铁塔_铁塔视联平台__运营管理-客户管理_需求
     */
    int insertLogs(Map map);

    /**
     * @Author：liuxt
     * @Description：更新调用日志
     * @Date：2022/8/5 11:34
     * @param map
     * @return Map
     * @requestID  中国铁塔_铁塔视联平台__运营管理-客户管理_需求
     */
    int updateTenantInfo(Map map);

    /**
     * @Author：lishihao
     * @Description：更新调用日志
     * @Date：2022/12/8
     * @param alarmTypeReqVo
     * @return int
     * @requestID  中国铁塔_铁塔视联平台__运营管理-客户管理_需求
     */
    int updateTenantInfoNew(AlarmTypeReqVo alarmTypeReqVo);

    /**
     * lishihao
     * @Description：更新租户运营配置表
     * @param alarmTypeReqVo
     * @return
     */
    int updateTenantOperation(@Param("param") AlarmTypeReqVo alarmTypeReqVo);

    /**
     * 租户运营表新增数据
     * @param alarmTypeReqVoList
     * @return
     */
    int insertTenantOperation(@Param("alarmTypeReqVoList") List<AlarmTypeReqVo> alarmTypeReqVoList);

    String queryChildPlatformCode(@Param("locationCode")String locationCode);

    List<PlatformInfoVo> getParentLocationCodeInfo(@Param("locationCodeList")List<String> locationCodeList);

    /**
     * @Author：liuxt
     * @Description：查询租户信息
     * @Date：2022/8/5 11:34
     * @param tenantVO
     * @return TenantVO
     * @requestID  中国铁塔_铁塔视联平台__运营管理-客户管理_需求
     */
    public TenantVO queryTenantById(TenantVO tenantVO);

    /**
     * @Author：liuxt
     * @Description：查询租户订购信息
     * @Date：2022/8/5 11:34
     * @param tenantOrderVO
     * @return TenantVO
     * @requestID  中国铁塔_铁塔视联平台__运营管理-客户管理_需求
     */
    List<CustomOrderInfoVO> queryCustOrderInfo(TenantOrderVO tenantOrderVO);


    /**
     * @Author：liuxt
     * @Description：获取客户列表
     * @Date：2022/8/5 11:34
     * @param list
     * @return List<Map>
     * @requestID  中国铁塔_铁塔视联平台__运营管理-客户管理_需求
     */
    List<String> getOrderCustomerList(List<String> list);



    /**
     * @Author：liuxt
     * @Description：获取全部算法信息树接口
     * @Date：2022/8/5 11:34
     * @param
     * @return AuthRegisterApiVO
     * @requestID  中国铁塔_铁塔视联平台_全部算法信息树接口
     */
    List<FunctionTypeVO> getAllAlgorithmInfoTree();

    /**
     * 根据客户编码列表查询客户属地化关联信息
     * @param tenantTreeReqVO
     * @return 客户属地化关联信息
     */
    List<TenantLocationRelVO> getTenantLocationRelList(@Param("param") TenantTreeReqVO tenantTreeReqVO);

    List<TenantLocationRelVO> getCenterIndustryInfo(@Param("list") List<String> industryCodeList);

    List<TenantLocationRelVO> getLocationIndustryInfo(@Param("list") List<String> industryCodeList);

    List<OperationPushRelVO> queryOperationPushRecordList(@Param("param") OperationPushRelVO operationPushRelVO);

    List<AlarmTypeReqVo> queryOperationInfoList(@Param("param")OperationPushRelVO operationPushRelVO);

    /**
     * 根据客户编码列表查询客户属地化关联信息
     * @param tenantIdList 客户编码列表
     * @return 客户属地化关联信息
     */
    List<Map> getLocationValidOrderNum(@Param("tenantIdList") List<String> tenantIdList);
    /**
     * 根据客户编码列表查询客户属地化关联信息
     * @param tenantIdList 客户编码列表
     * @return 客户属地化关联信息
     */
    List<Map> getLocationValidDevNum(@Param("tenantIdList") List<String> tenantIdList);
    /**
     * 根据客户编码列表查询客户运营研判关联信息
     * @param tenantIdList 客户编码列表
     * @return 客户属地化关联信息
     */
    List<TenantLocationRelVO> getOperationInfo(@Param("tenantIdList") List<String> tenantIdList);

    List<TenantLocationRelVO> getCustomerValidOrderNum(@Param("param") AlarmTypeReqVo alarmTypeReqVo);


    int insertOperationPushRecordBatch(@Param("list") List<OperationPushRecordVO> operationPushRecordVOList);

    int updateOperationPushRecordBatch(@Param("list") List<OperationPushRecordVO> operationPushRecordVOList);

    ThirdAlarmInfoVO queryThirdAlarmInfo(@Param("param") ThirdAlarmInfoVO thirdAlarmInfoReq);

    int updateThirdAlarmInfo(@Param("param") ThirdAlarmInfoVO thirdAlarmReq);

    int closeThirdAlarm(@Param("param") ThirdAlarmInfoVO thirdAlarmReq);

    int insertThirdAlarmInfo(@Param("param") ThirdAlarmInfoVO thirdAlarmReq);

    int deleteTenantMapConfigByCondition(@Param("param") TenantMapConfigInfoVO param);

    int saveTenantMapConfig(@Param("list")List<TenantMapConfigVO> list);

    List<TenantMapConfigVO> queryTenantMapConfigInfo(@Param("param") TenantMapConfigQueryVO param);

    int updateLoginTypeAndUrlByTenantId(TenantVO tenantVO);

    TenantMangeResultVO selectSysTenantInfoByTenantId(@Param("tenantId") String tenantId);
}