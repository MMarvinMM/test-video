package com.chinatower.device.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chinatower.device.dataobject.RadarAlarmRegionDO;
import com.chinatower.device.entity.dto.RadarAlarmRegionDTO;
import com.chinatower.device.dto.RadarAlarmRegionSaveDTO;
import com.chinatower.device.entity.vo.RadarAlarmRegionVo;
import com.chinatower.device.entity.vo.RadarAlarmTemplateVO;
import com.chinatower.device.entity.vo.RadarRegionBriefVO;
import com.chinatower.device.entity.vo.UnionTemplateVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 雷达告警模板配置
 */
public interface RadarAlarmRegionMapper extends BaseMapper<RadarAlarmRegionDO> {

    /**
     * 分页查询雷达告警区域信息
     * @param radarAlarmRegionDTO
     * @return
     */
    List<RadarAlarmRegionVo> queryAlarmRegionList(@Param("dto") RadarAlarmRegionDTO radarAlarmRegionDTO);

    /**
     * 根据区域ID查询关联模板信息
     * @param radarAlarmRegionDTO
     * @return
     */
    List<RadarAlarmTemplateVO> queryAlarmTemplateByRegionId(@Param("param") RadarAlarmRegionDTO radarAlarmRegionDTO);

    /**
     * 根据客户信息查询雷达告警区域信息
     * @param tenantId 客户编码
     * @param industryCode 行业编码
     * @param regionTypeList 区域类型列表
     * @return 雷达告警区域信息
     */
    List<RadarRegionBriefVO> selectByTenantIndustryInfo(@Param("tenantId") String tenantId,
                                                        @Param("industryCode") String industryCode,
                                                        @Param("regionTypeList") List<Integer> regionTypeList);

    /**
     * 根据模板id查询关联的雷达告警区域id
     * @param templateId 模板id
     * @return 雷达告警区域id
     */
    List<Long> selectRelateRegionIdByTemplateId(@Param("templateId") Long templateId);

    /**
     * 根据区域id统计关联模板数量
     * @param regionId 区域id
     * @return 关联模板数量
     */
    int countTemplateInfoByRegionId(@Param("regionId") Long regionId);

    /**
     * 根据区域id统计关联开启模板数量
     * @param regionId 区域id
     * @return 关联模板数量
     */
    int countEnabledTemplateInfoByRegionId(@Param("regionId") Long regionId);

    /**
     * 根据区域id删除区域信息
     * @param regionId 区域id
     * @param userName 操作人
     * @return 影响行数
     */
    int deleteAlarmRegionById(@Param("regionId") Long regionId, @Param("userName") String userName);

    /**
     * 新增告警区域信息
     * @param param 参数
     * @return 影响行数
     */
    int addAlarmRegion(@Param("param")RadarAlarmRegionSaveDTO param);

    /**
     * 更新告警区域信息
     * @param param 参数
     * @return 影响行数
     */
    int updateAlarmRegion(@Param("param")RadarAlarmRegionSaveDTO param);

    int deleteRegionRelByTempId(@Param("templateId") Long templateId);

    /**
     * 根据告警区域id查询告警区域信息
     * @param regionId 告警区域id
     * @return 告警区域信息
     */
    RadarAlarmRegionSaveDTO queryAlarmRegionByRegionId(@Param("regionId") Long regionId);

    /**
     * 统计告警区域名称重复的数量
     * @param tenantId 客户id
     * @param industryCode 行业编码
     * @param regionName 告警区域名称
     * @param regionId 告警区域id
     * @return 数量
     */
    int countAlarmRegionSameName(@Param("tenantId")String tenantId, @Param("industryCode")String industryCode,
                                 @Param("regionName")String regionName, @Param("regionId")Long regionId);

    /**
     * 获取告警区域默认名称序号
     * @param tenantId 客户id
     * @param industryCode 行业编码
     * @return 告警区域默认名称序号
     */
    Long getAutoConfigNameIndex(@Param("tenantId")String tenantId, @Param("industryCode")String industryCode);

    /**
     * 根据区域ID查询所有关联的模板
     * @param regionIdList 区域ID集合
     * @return 所有关联模板信息
     */
    List<UnionTemplateVo> getUnionTemByRegionId(@Param("list")List<Long> regionIdList);
}