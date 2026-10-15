package com.chinatower.device.service.impl;

import com.chinatower.device.dto.CasCadeDto;
import com.chinatower.device.mapper.CasCadeMapper;
import com.chinatower.device.service.ICasCadeService;
import com.chinatower.platform.entity.device.*;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 级联平台信息业务处理类
 */
@Service
public class CasCadeServiceImpl implements ICasCadeService {

    @Resource
    private CasCadeMapper casCadeMapper;

    @Override
    public List<String> getEffPlatCode(List<String> reqList) {
        return casCadeMapper.getEffPlatCode(reqList);
    }

    /**
     * 同步-批量插入级联平台信息
     *
     * @param reqList
     * @return
     */
    @Override
    public Integer batchInsert(List<SynCobtainVO> reqList) {
        return casCadeMapper.batchInsert(reqList);
    }

    /**
     * 同步-批量修改
     * @param reqList
     * @return
     */
    @Override
    public Integer batchUpdate(List<SynCobtainVO> reqList) {
        return casCadeMapper.batchUpdate(reqList);
    }

    @Override
    public List<SynCobtainVO> findCascadeByPlatCodeList(List<SynCobtainVO> reqList) {
        return casCadeMapper.findCascadeByPlatCodeList(reqList);
    }

    /**
     * 同步-批量删除
     *
     * @param reqList
     * @return
     */
    @Override
    public Integer deleteByIds(List<SynCobtainVO> reqList) {
        return casCadeMapper.deleteByIds(reqList);
    }

    /**
     * 根据platcode查询sys_tenant_cascade_device_rel表的tenantId,再根据tenantId清理sys_tenant_info的级联信息
     * @param tenantIdList
     * @return
     */
    @Override
    public Integer clearCascadeTenant(List<String> tenantIdList) {
        if (CollectionUtils.isEmpty(tenantIdList)) {
            return 0;
        }
        return casCadeMapper.clearCascadeTenant(tenantIdList);
    }

    @Override
    public List<String> getRelTenantIdByPlatCode(List<SynCobtainVO> reqList) {
        return casCadeMapper.getRelTenantIdByPlatCode(reqList);
    }

    @Override
    public List<String> getRelTenantIdByTenantIdList(List<String> tenantIdList) {
        return casCadeMapper.getRelTenantIdByTenantIdList(tenantIdList);
    }

    /**
     * 根据platcode删除sys_tenant_cascade_device_rel表
     * @param reqList
     * @return
     */
    @Override
    public Integer delCascadeRel(List<SynCobtainVO> reqList) {
        return casCadeMapper.delCascadeRel(reqList);
    }

    @Override
    public Integer delCascadeOrgRel(List<SynCobtainVO> reqList) {
        return casCadeMapper.delCascadeOrgRel(reqList);
    }

    /**
     * 根据tenantId查询order_devices的deviceCode
     * @param tenantId
     * @return
     */
    @Override
    public List<String> qryOrderDevCodeByTenantId(String tenantId) {
        return casCadeMapper.qryOrderDevCodeByTenantId(tenantId);
    }

    /**
     * 根据tenantId查询sys_tenant_cascade_device_rel表
     * @param tenantId
     * @return
     */
    @Override
    public List<CasCadeDto> qryCasCadeByTenantId(String tenantId) {
        return casCadeMapper.qryCasCadeByTenantId(tenantId);
    }

    @Override
    public List<CasCadeDto> qryCasCadeByPlatCode(String platCode) {
        return casCadeMapper.qryCasCadeByPlatCode(platCode);
    }

    @Override
    public List<CasCadeBindDeviceInfoTreeVO> queryCascadeOrgInfoByTenantId(String tenantId) {
        return casCadeMapper.queryCascadeOrgInfoByTenantId(tenantId);
    }

    @Override
    public List<CasCadeBindDeviceInfoTreeVO> queryCascadeOrgInfoByPlatCode(String platCode) {
        return casCadeMapper.queryCascadeOrgInfoByPlatCode(platCode);
    }

    @Override
    public CasCadeBindVO queryTenantInfoById(String tenantId) {
        return casCadeMapper.queryTenantInfoById(tenantId);
    }

    @Override
    public List<String> qryCasCadePlatCodeByTenantId(String tenantId) {
        return casCadeMapper.qryCasCadePlatCodeByTenantId(tenantId);
    }

    @Override
    public List<SynCobtainVO> findCascadeList(String platformCode) {
        return casCadeMapper.findCascadeList(platformCode);
    }

    @Override
    public Integer updateCascadeBinding(CasCadeBindVO cadeBindVO) {
        return casCadeMapper.updateCascadeBinding(cadeBindVO);
    }

    @Override
    public Integer updateCascadeBindingBatch(List<String> tenantIdList, String cascadeStatus, String cascadeModifier,
                                             String cascadeModifierName, Date modifyTime) {
        return casCadeMapper.updateCascadeBindingBatch(tenantIdList, cascadeStatus, cascadeModifier, cascadeModifierName, modifyTime);
    }

    @Override
    public List<CasCadeDto> selectCascadeRelByPlatCode(String platCode, String notIncludeTenantId) {
        return casCadeMapper.selectCascadeRelByPlatCode(platCode, notIncludeTenantId);
    }

    @Override
    public Integer insertCascadeDeviceRelList(List<CasCadeDeviceBindVO> list) {
        return casCadeMapper.insertCascadeDeviceRelList(list);
    }

    @Override
    public Integer insertCascadeOrgRelList(List<CasCadeOrgBindVO> list) {
        return casCadeMapper.insertCascadeOrgRelList(list);
    }

    @Override
    public Integer deleteCascadeDeviceRelList(String tenantId, List<String> platCode) {
        return casCadeMapper.deleteCascadeDeviceRelList(tenantId, platCode);
    }

    @Override
    public Integer deleteCascadeOrgRelList(String tenantId, List<String> platCode) {
        return casCadeMapper.deleteCascadeOrgRelList(tenantId, platCode);
    }

    @Override
    public Integer deleteCascadeDeviceRelListByPlatCode(String platCode) {
        return casCadeMapper.deleteCascadeDeviceRelListByPlatCode(platCode);
    }

    @Override
    public Integer deleteCascadeOrgRelListByPlatCode(String platCode) {
        return casCadeMapper.deleteCascadeOrgRelListByPlatCode(platCode);
    }

    @Override
    public Integer deleteCascadePushRel(String platCode) {
        return casCadeMapper.deleteCascadePushRel(platCode);
    }

    @Override
    public Integer insertCascadePushRelBatch(List<String> tenantIdList, String platCode, String userName) {
        return casCadeMapper.insertCascadePushRelBatch(tenantIdList, platCode, userName);
    }

    @Override
    public List<String> queryCascadeRelPlatCodeByTenantId(String tenantId) {
        return casCadeMapper.queryCascadeRelPlatCodeByTenantId(tenantId);
    }

    @Override
    public List<CasCadeDto> queryCascadeRelInfoByPlatCodeList(List<String> platCodeList) {
        return casCadeMapper.queryCascadeRelPlatCodeByPlatCodeList(platCodeList);
    }

    @Override
    public Integer deleteCascadeRelByTenantId(String tenantId) {
        return casCadeMapper.deleteCascadeRelByTenantId(tenantId);
    }

    @Override
    public Integer deleteCascadeDeviceRel(String tenantId, List<String> deviceCodeList) {
        return casCadeMapper.deleteCascadeDeviceRel(tenantId, deviceCodeList);
    }

    @Override
    public List<String> queryCascadeBindingTenantId(String platCode) {
        return casCadeMapper.queryCascadeBindingTenantId(platCode);
    }
}
