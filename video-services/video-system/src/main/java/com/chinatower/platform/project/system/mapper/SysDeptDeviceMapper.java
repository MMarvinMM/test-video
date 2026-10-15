package com.chinatower.platform.project.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chinatower.platform.annotation.DataScope;
import com.chinatower.platform.entity.admin.SysDept;
import com.chinatower.platform.project.custom.entity.OrganAlarmTypeVO;
import com.chinatower.platform.project.system.domain.SysDeptDevice;
import com.chinatower.system.api.model.DeviceVO;
import com.chinatower.system.api.model.SysDeptDeviceVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SysDeptDeviceMapper extends BaseMapper<SysDeptDevice> {

    @Delete("delete from vm_organ_device_rel_t where dept_id = #{dept_id}")
    void deleteByOrgId(@Param("dept_id") String orgId);

    @DataScope(deptColumn = "d.DEPT_ID",tenantColumn = "d.TENANT_ID",industryCode = "d.industry_code",acceptColumn="od.accept_node")
    List<SysDeptDeviceVO> getCameras(SysDept dept);

    @DataScope(deptColumn = "d.DEPT_ID",tenantColumn = "d.TENANT_ID",industryCode = "d.industry_code",acceptColumn="od.accept_node")
    List<SysDeptDeviceVO> getRaders(SysDept dept);

    @DataScope(deptColumn = "d.DEPT_ID",tenantColumn = "d.TENANT_ID",industryCode = "d.industry_code",acceptColumn="od.accept_node")
    List<SysDeptDeviceVO> getSensors(SysDept dept);

    @DataScope(tenantColumn = "d.TENANT_ID",industryCode = "d.industry_code",acceptColumn="od.accept_node")
    List<SysDeptDeviceVO> getSatellites(SysDept dept);

    @DataScope(deptColumn = "d.DEPT_ID",tenantColumn = "d.TENANT_ID",industryCode = "d.industry_code",acceptColumn="od.accept_node")
    List<SysDeptDeviceVO> getUavs(SysDept dept);//add by liuxt 20230612 组织管理优化需求 查询组织选中的无人机

    @DataScope(deptColumn = "d.DEPT_ID",tenantColumn = "d.TENANT_ID",industryCode = "d.industry_code",acceptColumn="od.accept_node")
    List<SysDeptDeviceVO> getUavNest(SysDept dept);

    @DataScope(deptColumn = "d.DEPT_ID",tenantColumn = "d.TENANT_ID",industryCode = "d.industry_code",acceptColumn="od.accept_node")
    List<SysDeptDeviceVO> getHorns(SysDept dept);//add by liuxt 202306

    List<OrganAlarmTypeVO> getAlarmType(SysDept dept);

    List<DeviceVO> getCamerasByTenantIdUser(@Param("tenantId") String tenantId, @Param("industryCode") String industryCode, @Param("deptIds")List<String> deptIds, @Param("acceptNode") String acceptNode);
    List<DeviceVO> getCamerasByTenantId(@Param("tenantId") String tenantId, @Param("industryCode") String industryCode, @Param("acceptNode") String acceptNode);

    List<DeviceVO> getRadersByTenantIdUser( @Param("tenantId") String tenantId, @Param("industryCode") String industryCode,@Param("deptIds")List<String> deptIds, @Param("acceptNode") String acceptNode);
    List<DeviceVO> getRadersByTenantId( @Param("tenantId") String tenantId, @Param("industryCode") String industryCode,@Param("acceptNode") String acceptNode);

    List<DeviceVO> getSensorsByTenantIdUser( @Param("tenantId") String tenantId, @Param("industryCode") String industryCode,@Param("deptIds")List<String> deptIds,@Param("acceptNode") String acceptNode);
    List<DeviceVO> getSensorsByTenantId( @Param("tenantId") String tenantId, @Param("industryCode") String industryCode,@Param("acceptNode") String acceptNode);

    List<DeviceVO> getSatellitesByTenantIdUser( @Param("tenantId") String tenantId, @Param("industryCode") String industryCode,@Param("deptIds")List<String> deptIds,@Param("acceptNode") String acceptNode);
    List<DeviceVO> getSatellitesByTenantId( @Param("tenantId") String tenantId, @Param("industryCode") String industryCode,@Param("acceptNode") String acceptNode);
    List<SysDeptDeviceVO> getUavsByTenantIdUser( @Param("tenantId") String tenantId, @Param("industryCode") String industryCode,@Param("deptIds")List<String> deptIds,@Param("acceptNode") String acceptNode);
    List<SysDeptDeviceVO> getHornsByTenantIdUser( @Param("tenantId") String tenantId, @Param("industryCode") String industryCode,@Param("deptIds")List<String> deptIds,@Param("acceptNode") String acceptNode);
    List<SysDeptDeviceVO> getCamerasScop(@Param("tenantId") String tenantId, @Param("industryCode") String industryCode, @Param("acceptNode") String acceptNode);

    List<SysDeptDeviceVO> getRadersScop(@Param("tenantId") String tenantId, @Param("industryCode") String industryCode, @Param("acceptNode") String acceptNode);

    List<SysDeptDeviceVO> getSensorsScop(@Param("tenantId") String tenantId, @Param("industryCode") String industryCode, @Param("acceptNode") String acceptNode);

    List<SysDeptDeviceVO> getSatellitesScop(@Param("tenantId") String tenantId, @Param("industryCode") String industryCode, @Param("acceptNode") String acceptNode);

    List<SysDeptDeviceVO> getUavsScop(@Param("tenantId") String tenantId, @Param("industryCode") String industryCode, @Param("acceptNode") String acceptNode);//add by liuxt 20230612 组织管理优化需求 查询订购的无人机

    List<SysDeptDeviceVO> getUavNestScop(@Param("tenantId") String tenantId, @Param("industryCode") String industryCode, @Param("acceptNode") String acceptNode);//add by liuxt 20230612 组织管理优化需求 查询订购的无人机

    List<SysDeptDeviceVO> getHornsScop(@Param("tenantId") String tenantId, @Param("industryCode") String industryCode, @Param("acceptNode") String acceptNode);//add by liuxt 20230612 组织管理优化需求 查询订购的大喇叭

    List<SysDeptDeviceVO> getCamerasTemp(@Param("tenantId") String tenantId, @Param("industryCode") String industryCode,@Param("deptIds")List<String> deptIds);

    List<SysDeptDeviceVO> getCamerasTempAll(@Param("tenantId") String tenantId, @Param("industryCode") String industryCode);

    List<SysDeptDeviceVO> getSensorsTemp(@Param("tenantId") String tenantId, @Param("industryCode") String industryCode,@Param("deptIds")List<String> deptIds);

    List<SysDeptDeviceVO> getSensorsTempAll(@Param("tenantId") String tenantId, @Param("industryCode") String industryCode);

    List<SysDeptDeviceVO> getSatellitesTemp(@Param("tenantId") String tenantId, @Param("industryCode") String industryCode,@Param("deptIds")List<String> deptIds);

    List<SysDeptDeviceVO> getSatellitesTempAll(@Param("tenantId") String tenantId, @Param("industryCode") String industryCode);
    List<SysDeptDeviceVO> getStreetByRadarVO(@Param("tenantId") String tenantId, @Param("industryCode") String industryCode);

    List<SysDeptDeviceVO> getLERsScop(@Param("tenantId") String tenantId, @Param("industryCode")String industryCode, @Param("acceptNode") String acceptNode);

    @DataScope(acceptColumn="od.accept_node")
    List<SysDeptDeviceVO> getLERs(SysDept dept);

    List<SysDeptDeviceVO> getLERsByTenantIdUser(@Param("tenantId") String tenantId, @Param("industryCode") String industryCode,@Param("deptIds")List<String> deptIds, @Param("acceptNode") String acceptNode);

    List<SysDeptDeviceVO> getCamerasScopNew(@Param("tenantId") String tenantId, @Param("industryCode") String industryCode);

    @DataScope(deptColumn = "d.DEPT_ID",tenantColumn = "d.TENANT_ID",industryCode = "d.industry_code")
    List<SysDeptDeviceVO> getCamerasNew(SysDept dept);

    List<SysDeptDeviceVO> getLayers(SysDept dept);

    List<SysDeptDeviceVO> getLayerScop(String tenantId, String industryCode);

    List<SysDeptDeviceVO> getLayersByTenantIdUser(String tenantId, String industryCode, List<String> deptIds);
}
