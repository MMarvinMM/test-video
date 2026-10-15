package com.chinatower.platform.deal;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.platform.entity.admin.OrganStreetVO;
import com.chinatower.platform.entity.admin.SysDept;
import com.chinatower.platform.entity.admin.SysUser;
import com.chinatower.platform.mapper.AuthBaseMapper;
import com.chinatower.platform.model.vo.ScreenDataConfigCountVO;
import com.chinatower.platform.model.vo.ScreenDataConfigParticipationVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

/**
 * @ClassName DataTypeDeal
 * @Deacription 动态数据源切换操作
 * @Author chenmf
 * @Date 2022/3/11 18:25
 * @Version 1.0
 **/
@Component
public class DataTypeDeal {

    @Autowired(required = false)
    private AuthBaseMapper authBaseMapper;

    @DS("#dbType")
    public List<String> getUserIdsByDeptId(String dbType,String deptId){
        return authBaseMapper.getUserIdsByDeptId(deptId);
    }

    @DS("#dbType")
    public List<OrganStreetVO> getOrgnStreetVO(String dbType, String industryCode,List<String> deptIds){
        return authBaseMapper.getOrgnStreetVO(industryCode,deptIds);
    }

    @DS("#dbType")
    public List<OrganStreetVO> getStreetByRadarVO(String dbType, String industryCode,List<String> deptIds){
        return authBaseMapper.getStreetByRadarVO(industryCode,deptIds);
    }

    @DS("#dbType")
    public List<String> selectTenantChildDept(String dbType, SysUser user){
        return authBaseMapper.selectTenantChildDept(user.getDeptId());
    }

    @DS("#dbType")
    public List<String> selectAllTanentDept(String dbType, SysUser user){
        return authBaseMapper.selectAllTanentDept(user);
    }

    @DS("#dbType")
    public List<String> selectCustomTanentDept(String dbType, SysUser user){
        return authBaseMapper.selectCustomTanentDept(user);
    }

    @DS("#dbType")
    public List<String> getOrgDevicesByType(String dbType, Set<String> deptIdStr, String alarmSource, String industryCode){
        return authBaseMapper.getOrgDevices(deptIdStr,alarmSource,industryCode);
    }

    @DS("#dbType")
    public List<String> getTowerUserAreasByType(String dbType, String userId) {
        return authBaseMapper.getTowerUserAreas(userId);
    }

    @DS("#dbType")
    public List<String> getTenantByTowerDeptId(String dbType,List<SysDept> deptId) {
        return authBaseMapper.getTenantByTowerDeptId(deptId);
    }

    @DS("#dbType")
    public List<SysDept> getDeptByCurrentDeptId(String dbType,SysDept deptId) {
        return authBaseMapper.getDeptByCurrentDeptId(deptId);
    }

    @DS("#dbType")
    public SysDept getDeptById(String dbType, String deptId) {
        return authBaseMapper.getDeptById(deptId);
    }

    @DS("#dbType")
    public List<String> getTowerByAreaLevel(String dbType, String userId,Integer areaLevel) {
        return authBaseMapper.getTowerByAreaLevel(userId,areaLevel);
    }

    @DS("#dbType")
    public List<String> getTowerUserAreaPathByType(String dbType, List<String> areaProCity) {
        return authBaseMapper.getTowerUserAreaPath(areaProCity);
    }

    @DS("#dbType")
    public List<String> getTowerUserMgmtAreasByType(String dbType, List<String> areaPaths) {
        return authBaseMapper.getTowerUserMgmtAreas(areaPaths);
    }

    @DS("#dbType")
    public String getParentDeptId(String dbType, String tenantId){
        return authBaseMapper.getParentDeptId(tenantId);
    }

    @DS("#dbType")
    public ScreenDataConfigCountVO selectScreenDataConfigCount(String dbType, String tenantId, String industryCode, String appVerCode, String screenId, String screenType) {
        return authBaseMapper.selectScreenDataConfigCount(tenantId, industryCode, appVerCode, screenId, screenType);
    }

    @DS("#dbType")
    public List<String> selectScreenDataConfigCameraByConfigId(String dbType, Long id) {
        return authBaseMapper.selectScreenDataConfigCameraByConfigId(id);
    }

    @DS("#dbType")
    public List<String> selectScreenDataConfigIotByConfigId(String dbType, Long id) {
        return authBaseMapper.selectScreenDataConfigIotByConfigId(id);
    }

    @DS("#dbType")
    public List<String> selectScreenDataConfigRadarByConfigId(String dbType, Long id) {
        return authBaseMapper.selectScreenDataConfigRadarByConfigId(id);
    }

    @DS("#dbType")
    public List<String> selectScreenDataConfigSpeakerByConfigId(String dbType, Long id) {
        return authBaseMapper.selectScreenDataConfigSpeakerByConfigId(id);
    }

    @DS("#dbType")
    public List<String> selectScreenDataConfigUavByConfigId(String dbType, Long id) {
        return authBaseMapper.selectScreenDataConfigUavByConfigId(id);
    }

    @DS("#dbType")
    public List<String> selectScreenDataConfigUavNestByConfigId(String dbType, SysUser user) {
        return authBaseMapper.selectScreenDataConfigUavNestByConfigId(user);
    }

    @DS("#dbType")
    public List<String> selectScreenDataConfigRecorderByConfigId(String dbType, Long id) {
        return authBaseMapper.selectScreenDataConfigRecorderByConfigId(id);
    }

    @DS("#dbType")
    public List<ScreenDataConfigParticipationVO> selectScreenDataConfigAlarmTypeByConfigId(String dbType, Long screenId) {
        return authBaseMapper.selectScreenDataConfigAlarmTypeByConfigId(screenId);
    }

    @DS("#dbType")
    public List<ScreenDataConfigParticipationVO> findAlarmTypeByOrder(String dbType, ScreenDataConfigCountVO body) {
        return authBaseMapper.findAlarmTypeByOrder(body);
    }
    @DS("#dbType")
    public List<ScreenDataConfigParticipationVO> findAlarmTypeBySource(String dbType, ScreenDataConfigCountVO body) {
        return authBaseMapper.findAlarmTypeBySource(body);
    }
    @DS("#dbType")
    public List<ScreenDataConfigParticipationVO> getThirdPartyAlarmTypes(String dbType, ScreenDataConfigCountVO body) {
        return authBaseMapper.getThirdPartyAlarmTypes(body);
    }

    @DS("#dbType")
    public List<String> selectScreenDataConfigGridByConfigId(String dbType, Long screenId) {
        return authBaseMapper.selectScreenDataConfigGridByConfigId(screenId);
    }

    @DS("#dbType")
    public List<String> selectScreenDataConfigGridOperatorByConfigId(String dbType, Long screenId) {
        return authBaseMapper.selectScreenDataConfigGridOperatorByConfigId(screenId);
    }

    @DS("#dbType")
    public List<String> selectScreenDataConfigResourceByConfigId(String dbType, Long screenId) {
        return authBaseMapper.selectScreenDataConfigResourceByConfigId(screenId);
    }

    @DS("#dbType")
    public Integer devByTenantIdCount(String dbType, ScreenDataConfigCountVO body) {
        return authBaseMapper.devByTenantIdCount(body);
    }

    @DS("#dbType")
    public Integer uavByTenantIdCount(String dbType, ScreenDataConfigCountVO body) {
        return authBaseMapper.uavByTenantIdCount(body);
    }

    @DS("#dbType")
    public Integer iotDevByTenantIdCount(String dbType, ScreenDataConfigCountVO body) {
        return authBaseMapper.iotDevByTenantIdCount(body);
    }

    @DS("#dbType")
    public List<ScreenDataConfigParticipationVO> selectDeptAlarmTypeConfigList(String dbType, String tenantId, String industryCode, Set<String> deptIdSet) {
        return authBaseMapper.selectDeptAlarmTypeConfigList(tenantId, industryCode, deptIdSet);
    }

    @DS("#dbType")
    public List<String> getOrgLayersByType(String dbType, Set<String> deptIdStr,String industryCode) {
        return authBaseMapper.getOrgLayers(deptIdStr,industryCode);
    }
}
