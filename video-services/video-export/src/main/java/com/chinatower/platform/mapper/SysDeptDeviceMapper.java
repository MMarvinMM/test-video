package com.chinatower.platform.mapper;


import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.platform.annotation.DataScope;
import com.chinatower.platform.entity.admin.SysDept;
import com.chinatower.platform.enums.TableEnum;
import com.chinatower.system.api.model.SysDeptDeviceVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@DS(TableEnum.BASE)
@Mapper
public interface SysDeptDeviceMapper {

    List<SysDeptDeviceVO> getCameras(SysDept dept);

    List<SysDeptDeviceVO> getRaders(SysDept dept);

    List<SysDeptDeviceVO> getSensors(SysDept dept);

    List<SysDeptDeviceVO> getSatellites(SysDept dept);

    List<SysDeptDeviceVO> getUavs(SysDept dept);

    List<String> getUserNameByDeptId(SysDept dept);

    List<Map<String, String>> getCamerasOrganDetails(Map param);

    List<Map<String, String>> getSensorsOrganDetails(Map param);

    List<Map<String, String>> getRadersOrganDetails(Map param);

    List<Map<String, String>> getSatellitesOrganDetails(Map param);

    List<Map<String, String>> getOrganUserDetails(Map map);
}
