package com.chinatower.device.repository;


import com.baomidou.mybatisplus.extension.service.IService;
import com.chinatower.common.entity.Result;
import com.chinatower.device.dataobject.RadarAlarmRegionDO;
import com.chinatower.device.entity.dto.RadarAlarmRegionDTO;
import com.chinatower.device.dto.RegionTemplateRelDTO;
import com.chinatower.device.dto.RadarAlarmRegionDeleteDTO;
import com.chinatower.device.dto.RadarAlarmRegionSaveDTO;
import com.chinatower.device.entity.vo.RadarAlarmRegionVo;
import com.chinatower.device.entity.vo.RadarAlarmTemplateVO;
import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * 雷达告警区域相关接口
 *
 * @author lishihao
 * @date 2023-4-27
 */
public interface IRadarAlarmRegionService extends IService<RadarAlarmRegionDO> {


    /**
     * 分页查询告警区域列表信息
     * @param dto 查询实体类
     * @return List<RadarAlarmRegionVo>
     */
    Result<PageInfo<RadarAlarmRegionVo>> queryRadarAlarmRegion(RadarAlarmRegionDTO dto);

    /**
     * 根据区域ID查询关联的模板信息
     * @param dto 查询实体类
     * @return Result<List<RadarAlarmTemplateVO>>
     */
    Result<List<RadarAlarmTemplateVO>> queryAlarmTemplateByRegionId(RadarAlarmRegionDTO dto);

    /**
     * 根据模板告警类型返回告警区域信息
     * @param req 请求参数
     * @return Result<List<RadarAlarmRegionDO>>
     */
    Result<List<RadarAlarmRegionDO>> queryRegionByAlarmType(RegionTemplateRelDTO req);
    /**
     * 新增雷达告警区域信息
     * @param param 请求参数
     * @return 返回值
     */
    Result<String> addAlarmRegion(RadarAlarmRegionSaveDTO param);

    /**
     * 更新雷达告警区域信息
     * @param param 请求参数
     * @return 返回值
     */
    Result<String> updateAlarmRegion(RadarAlarmRegionSaveDTO param);

    /**
     * 删除雷达告警区域信息
     * @param param 请求参数
     * @return 返回值
     */
    Result<String> deleteAlarmRegion(RadarAlarmRegionDeleteDTO param);

    /**
     * 根据告警区域id查询告警区域信息
     * @param regionId 告警区域id
     * @return 告警区域信息
     */
    Result<RadarAlarmRegionSaveDTO> queryAlarmRegionByRegionId(Long regionId);

    /**
     * 获取告警区域默认名称
     * @return 告警区域默认名称
     */
    Result<Long> getAutoConfigNameIndex();
}
