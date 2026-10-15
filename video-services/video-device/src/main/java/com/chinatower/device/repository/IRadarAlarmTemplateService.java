package com.chinatower.device.repository;


import com.chinatower.common.entity.Result;
import com.chinatower.device.dto.*;
import com.chinatower.device.entity.dto.RadarAlarmTemplateDTO;
import com.chinatower.device.entity.vo.RadarAlarmTemplateVO;
import com.chinatower.device.entity.vo.RadarRegionSelectedVO;
import com.github.pagehelper.PageInfo;

/**
 * 雷达告警模板配置相关接口
 * @author lishihao
 * @date 2023-4-27
 */
public interface IRadarAlarmTemplateService {


    /**
     * 分页查询告警模板列表信息
     * @param dto 查询实体类
     * @return List<RadarAlarmTemplateVO>
     */
    Result<PageInfo<RadarAlarmTemplateVO>> queryRadarAlarmTemplate(RadarAlarmTemplateDTO dto);

    /**
     * 根据ID修改告警模板状态
     * @param dto 查询实体类
     * @return Result<String>
     */
    Result<String> updateTemplateStatusById(RadarAlarmTemplateVO dto);

    /**
     * 根据ID查询告警模板详细信息
     * @param dto 查询实体类
     * @return Result<RadarAlarmTemplateVO>
     */
    Result<RadarAlarmTemplateVO> getTemplateInfoById(RadarAlarmTemplateDTO dto);

    /**
     * 新增告警模板信息
     * @param radarAlarmTemplateReq 请求参数
     * @return Result<String>
     */
    Result<String> addTemplateInfo(RadarAlarmTemplateReq radarAlarmTemplateReq);

    /**
     * 修改告警模板信息
     * @param radarAlarmTemplateReq 请求参数
     * @return Result<String>
     */
    Result<String> updateTemplateInfo(RadarAlarmTemplateReq radarAlarmTemplateReq);

    /**
     * 根据模板id查询雷达告警区域信息
     * @param templateId 模板id
     * @param alarmTypeValue 告警类型编码
     * @return 雷达告警区域信息
     */
    Result<RadarRegionSelectedVO> queryAlarmRegionInfoByTemplateId(Long templateId, String alarmTypeValue);

    /**
     * 根据ID删除模板及其关联信息
     */
    Result<String> deleteTemplateById(RegionTemplateRelDTO dto);

    /**
     * 获取告警模板默认名称
     * @return 告警模板默认名称
     */
    Result<String> getAutoConfigName();
}
