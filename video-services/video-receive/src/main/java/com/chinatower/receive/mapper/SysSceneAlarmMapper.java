package com.chinatower.receive.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import com.chinatower.receive.vo.dto.OrderCustomerVO;
import com.chinatower.receive.vo.entity.SceneAlarmRelationVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 场景告警关系表 Mapper 接口
 * </p>
 *
 * @author AutoGen
 * @since 2024-01-01
 */
@Mapper
public interface SysSceneAlarmMapper {

    /**
     * 根据taskId查询场景和告警子项对应关系
     * 查询流程：
     * 1. 从vm_algorithm_config表通过taskId找到scene_code（场景编码，逗号分隔）
     * 2. 从sys_scene_alarm_rel表通过scene_id找到alarm_type（告警子项）
     * 3. 从sys_alarm_type表通过scene_id（对应type_value）找到type_value和type_name
     * 4. 最终返回场景名称、场景编码、告警子项对应关系列表
     *
     * @param taskId        任务ID
     * @param alarmTypeList 告警子项列表，用于过滤（可选）
     * @return 场景告警关系列表
     */
    @DS(EbikeDataBaseConstant.BASE)
    List<SceneAlarmRelationVO> selectSceneAlarmRelationByTaskId(@Param("taskId") String taskId,
                                                                @Param("alarmTypeList") List<String> alarmTypeList,
                                                                @Param("customers") List<OrderCustomerVO> customers);
}

