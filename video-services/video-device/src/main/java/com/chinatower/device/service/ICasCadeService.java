package com.chinatower.device.service;

import com.chinatower.device.dto.CasCadeDto;
import com.chinatower.platform.entity.device.*;

import java.util.Date;
import java.util.List;

public interface ICasCadeService {

    /**
     * 校验 获取开启级联的paltCode
     * @param reqList
     * @return
     */
    List<String> getEffPlatCode(List<String> reqList);

    /**
     * 批量插入级联平台信息
     *
     * @param reqList
     * @return
     */
    Integer batchInsert(List<SynCobtainVO> reqList);

    Integer batchUpdate(List<SynCobtainVO> reqList);

    List<SynCobtainVO> findCascadeByPlatCodeList(List<SynCobtainVO> reqList);

    /**
     * 批量删除
     *
     * @param reqList
     * @return
     */
    Integer deleteByIds(List<SynCobtainVO> reqList);

    List<String> getRelTenantIdByPlatCode(List<SynCobtainVO> reqList);

    List<String> getRelTenantIdByTenantIdList(List<String> tenantIdList);

    Integer clearCascadeTenant(List<String> tenantIdList);

    Integer delCascadeRel(List<SynCobtainVO> reqList);

    Integer delCascadeOrgRel(List<SynCobtainVO> reqList);

    List<String> qryOrderDevCodeByTenantId(String tenantId);

    List<CasCadeDto> qryCasCadeByTenantId(String tenantId);

    List<CasCadeDto> qryCasCadeByPlatCode(String platCode);

    List<CasCadeBindDeviceInfoTreeVO> queryCascadeOrgInfoByTenantId(String tenantId);

    List<CasCadeBindDeviceInfoTreeVO> queryCascadeOrgInfoByPlatCode(String platCode);

    CasCadeBindVO queryTenantInfoById(String tenantId);

    List<String> qryCasCadePlatCodeByTenantId(String tenantId);

    List<SynCobtainVO> findCascadeList(String platformCode);

    Integer updateCascadeBinding(CasCadeBindVO cadeBindVO);

    Integer updateCascadeBindingBatch(List<String> tenantIdList, String cascadeStatus, String cascadeModifier,
                                      String cascadeModifierName, Date modifyTime);

    List<CasCadeDto> selectCascadeRelByPlatCode(String platCode, String notIncludeTenantId);

    Integer insertCascadeDeviceRelList(List<CasCadeDeviceBindVO> list);

    Integer insertCascadeOrgRelList(List<CasCadeOrgBindVO> list);

    Integer deleteCascadeDeviceRelList(String tenantId, List<String> platCode);

    Integer deleteCascadeOrgRelList(String tenantId, List<String> platCode);

    Integer deleteCascadeDeviceRelListByPlatCode(String platCode);

    Integer deleteCascadeOrgRelListByPlatCode(String platCode);

    Integer deleteCascadePushRel(String platCode);

    Integer insertCascadePushRelBatch(List<String> tenantIdList, String platCode, String userName);

    List<String> queryCascadeRelPlatCodeByTenantId(String tenantId);

    List<CasCadeDto> queryCascadeRelInfoByPlatCodeList(List<String> platCodeList);

    Integer deleteCascadeRelByTenantId(String tenantId);

    Integer deleteCascadeDeviceRel(String tenantId, List<String> deviceCodeList);

    List<String> queryCascadeBindingTenantId(String platCode);
}
