package com.chinatower.device.service;

import com.chinatower.common.entity.Result;
import com.chinatower.device.dto.CasCadeSyncReq;
import com.chinatower.device.dto.CascadeBindingImportVO;
import com.chinatower.platform.entity.ImportParams;
import com.chinatower.platform.entity.SysImportDetail;
import com.chinatower.platform.entity.device.*;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface ICasCadeSyncService {

    /**
     * 6.51.	删除访问配置权限
     *
     * @param req
     * @return
     */
    Result<String> deleteCascade(CasCadeDeleteVO req);


    /**
     * 6.52.	下级平台配置访问权限下发
     *
     * @param cadeSyncReq
     * @return
     */
    Result<String> allocation(CasCadeSyncReq request);

    /**
     * 基础平台同步接口
     *
     * @param reqList
     * @return
     */
    Result<String> synCascadePlatform(List<SynCobtainVO> reqList);

    /**
     * 订购变更后需要重新同步级联平台信息
     * 根据tennantId查询0，1，2状态的deciceCode，与sys_tenant_cascade_device_rel比较，减少的device，调用6.52下发
     */
    Result<String> synDelCascadeByOrder(CasCadeSyncVO cadeSyncReq);

    /**
     * 获取未被选择的级联平台信息
     */
    Result<List<SynCobtainVO>> findCascadeList(CasCadeSyncVO cadeSyncReq);

    /**
     * 级联平台信息绑定下发
     */
    Result<String> cascadeBinding(CasCadeBindPlatInfoVO param);

    Result<List<String>> queryCascadeBindingTenantId(String platCode);

    /**
     * 查询客户级联详情信息
     * @param casCadeSyncVO 参数
     * @return 客户级联详情信息
     */
    Result<CasCadeBindPlatInfoVO> queryCascadeInfoList(CasCadeSyncVO casCadeSyncVO);


    Result<String> generateCascadeOrgCode(CasCadeGenerateOrgCodeVO param);

    List<SysImportDetail> importData(ImportParams<CascadeBindingImportVO> params, String platCode, String tenantId);

    Result<CasCadeBindPlatInfoVO> queryCascadeBindingImportList(String importId);
}
