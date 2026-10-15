package com.chinatower.airline.service;

import com.chinatower.device.entity.dto.AirPlanAddOrEditReq;
import com.chinatower.device.entity.dto.AirPlanPageReq;
import com.chinatower.device.entity.dto.AirTaskEnableReq;
import com.chinatower.airline.dto.response.AirPlanRep;
import com.chinatower.common.core.web.page.TableDataInfo;
import com.chinatower.common.entity.Result;

import java.util.List;

/**
 * @Author lize
 * @Date 2024/11/4 9:59
 */
public interface AirPlanService {


    /**
     * 计划管理列表查询
     * @param vo
     * @return
     */
    TableDataInfo findByCondition(AirPlanPageReq vo);

    /**
     * 根据id查询详情
     * @param planId
     * @return
     */
    AirPlanRep findAirPlanById(Integer planId);

    /**
     * 新增/编辑计划管理
     * @param vo
     * @return
     */
    Result addOrEditAirPlan (AirPlanAddOrEditReq vo,String optType);

    /**
     * 根据planId删除计划管理
     * @param vo
     * @return
     */
    Result deleteByPlanId(AirPlanAddOrEditReq vo);

    /**
     * 系统根据计划新增的数量自动生成一个名称“新建计划N”，可修改。
     * @param tenantId
     * @param industryCode
     * @return
     */
    String getPlanName(String tenantId,String industryCode);

    /**
     *唯一性校验，重复时输入框高亮显示，同时在输入框下方显示提示“名称已存在！请重新输入。
     * @param tenantId
     * @param industryCode
     * @param planName
     * @return
     */
    Result planNameIsRepeat(String tenantId, String industryCode, String planName,Integer id);

    /**
     * 是否启用按钮 更新状态
     * @param airTaskReq
     * @return
     */
    Result updateIsItEnabled(AirTaskEnableReq airTaskReq);

    /**
     * 查询当前登录人历史填写的安全员（最新10条），支持按姓名模糊搜索
     *
     * 下拉展示：安全员 + 联系电话（不脱敏）
     */
    Result querySafetyOfficerMemory(String safetyOfficer);

    /**
     *  HMS 飞行告警短信：根据无人机编码查询当前飞行计划的安全员手机号（去重）
     * @param uavCode
     * @return
     */
    Result<List<String>> selectSafetyOfficerPhonesByFlyingUav(String uavCode);

    /**
     * HMS 飞行告警短信：根据无人机编码查询无人机名称
     * @param uavCode
     * @return
     */
    Result<String> selectUavNameByUavCode(String uavCode);

    /**
     *计划名称自动填充为“复制的计划名称_复制N”
     * @param planId
     * @param tenantId
     * @param industryCode
     * @return
     */
    String copyPlanName(String planId, String tenantId, String industryCode);

    /**
     *
     * @param originalTaskRecordIds
     */
    void editTaskType(String originalTaskRecordIds);

    /**
     *
     * @param originalTaskRecordIds
     * @param photoId
     * @param originalTaskRecordId
     */
    void insertReFlyRef(String originalTaskRecordIds, String photoId, String originalTaskRecordId);
}
