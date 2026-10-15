package com.chinatower.device.mapper;

import com.chinatower.device.dto.CasCadeDto;
import com.chinatower.platform.entity.device.*;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface CasCadeMapper {

    List<String> getEffPlatCode(@Param("reqList") List<String> reqList);

    Integer batchInsert(@Param("reqList") List<SynCobtainVO> reqList);

    Integer batchUpdate(@Param("reqList") List<SynCobtainVO> reqList);

    Integer deleteByIds(@Param("reqList") List<SynCobtainVO> reqList);

    List<String> getRelTenantIdByPlatCode(@Param("reqList") List<SynCobtainVO> reqList);

    List<String> getRelTenantIdByTenantIdList(@Param("tenantIdList")List<String> tenantIdList);

    Integer clearCascadeTenant(@Param("reqList") List<String> reqList);

    Integer delCascadeRel(@Param("reqList") List<SynCobtainVO> reqList);

    Integer delCascadeOrgRel(@Param("reqList") List<SynCobtainVO> reqList);

    List<String> qryOrderDevCodeByTenantId(@Param("tenantId") String tenantId);

    List<CasCadeDto> qryCasCadeByTenantId(@Param("tenantId") String tenantId);

    List<CasCadeDto> qryCasCadeByPlatCode(@Param("platCode") String platCode);

    List<CasCadeBindDeviceInfoTreeVO> queryCascadeOrgInfoByTenantId(@Param("tenantId")String tenantId);

    List<CasCadeBindDeviceInfoTreeVO> queryCascadeOrgInfoByPlatCode(@Param("platCode") String platCode);

    CasCadeBindVO queryTenantInfoById(@Param("tenantId") String tenantId);

    List<String> qryCasCadePlatCodeByTenantId(@Param("tenantId") String tenantId);

    List<SynCobtainVO> findCascadeList(@Param("platformCode")String platformCode);

    List<SynCobtainVO> findCascadeByPlatCodeList(@Param("reqList") List<SynCobtainVO> reqList);

    Integer updateCascadeBinding(CasCadeBindVO cadeBindVO);

    Integer updateCascadeBindingBatch(@Param("tenantIdList") List<String> tenantIdList,
                                      @Param("cascadeStatus") String cascadeStatus,
                                      @Param("cascadeModifier") String cascadeModifier,
                                      @Param("cascadeModifierName") String cascadeModifierName,
                                      @Param("modifyTime") Date modifyTime);

    List<CasCadeDto> selectCascadeRelByPlatCode(@Param("platCode") String platCode, @Param("notIncludeTenantId")String notIncludeTenantId);

    Integer insertCascadeDeviceRelList(@Param("list")List<CasCadeDeviceBindVO> list);

    Integer insertCascadeOrgRelList(@Param("list")List<CasCadeOrgBindVO> list);

    Integer deleteCascadeDeviceRelList(@Param("tenantId")String tenantId, @Param("platCodeList")List<String> platCode);

    Integer deleteCascadeOrgRelList(@Param("tenantId")String tenantId, @Param("platCodeList")List<String> platCode);

    Integer deleteCascadeDeviceRelListByPlatCode(@Param("platCode")String platCode);

    Integer deleteCascadeOrgRelListByPlatCode(@Param("platCode")String platCode);

    Integer deleteCascadePushRel(@Param("platCode")String platCode);

    Integer insertCascadePushRelBatch(@Param("tenantIdList")List<String> tenantIdList, @Param("platCode")String platCode, @Param("userName")String userName);

    List<String> queryCascadeRelPlatCodeByTenantId(@Param("tenantId")String tenantId);

    List<CasCadeDto> queryCascadeRelPlatCodeByPlatCodeList(@Param("platCodeList") List<String> platCodeList);

    Integer deleteCascadeRelByTenantId(@Param("tenantId")String tenantId);

    Integer deleteCascadeDeviceRel(@Param("tenantId")String tenantId, @Param("deviceCodeList")List<String> deviceCodeList);

    List<String> queryCascadeBindingTenantId(@Param("platCode")String platCode);
}
