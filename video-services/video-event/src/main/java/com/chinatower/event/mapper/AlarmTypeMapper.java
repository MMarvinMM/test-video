package com.chinatower.event.mapper;

import java.util.List;
import java.util.Map;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.event.dataobject.AlarmTypeDO;
import com.chinatower.event.dto.ScreenDataConfigParticipationDTO;
import com.chinatower.event.entity.dto.AlarmSourceSubTypeDTO;
import com.chinatower.event.entity.vo.AlarmTypeVO;
import com.chinatower.event.vo.SysAlarmTypeIndustryRelVo;
import com.chinatower.industry.dto.AlgorithmInfoDO;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chinatower.common.core.dto.AlarmType;
import com.chinatower.common.core.dto.AlarmTypeClassDTO;
import com.chinatower.event.entity.dto.AlarmTypeDTO;
import com.chinatower.event.vo.AlarmTypeClassVO;
import com.chinatower.event.entity.vo.AlgorithmEventVO;

/**
 * <p>
 * 告警类型表 Mapper 接口
 * </p>
 *
 * @author AutoGen
 * @since 2022-01-26
 */
@DS(EbikeDataBaseConstant.BASE)
public interface AlarmTypeMapper extends BaseMapper<AlarmTypeDO> {

    /**
     * @Description 查询订购类型
     * @author hued
     * @Date 15:39 2021/7/7
     * @Param [body]
     **/
    List<AlarmTypeDO> findOrderAlarm(@Param("body") AlarmTypeDTO body);

    /**
     * 根据告警来源查询告警类型
     * @param body 参数
     * @return 告警类型信息
     */
    List<AlarmTypeDO> findOrderAlarmBySource(@Param("body") AlarmTypeDTO body);

    /**
     * @Description 查询所有订购类型
     * @Param [body]
     **/
    List<AlarmTypeDO> findOrderAlarmAll(@Param("body") AlarmTypeDTO body);

    /**
     * @Description 铁塔视角查询告警类型
     * @author hued
     * @Date 2021年7月10日 14:20:49
     * @Param [body]
     **/
    List<AlarmTypeDO> findOrderAlarmTT(@Param("body") AlarmTypeDTO body);

    /**
     * 根据告警类型名称关键字查询告警类型id
     * @param typeName
     * @return
     */
    List<String> findOrderAlarmByKeyWord(@Param("typeName") String typeName);
    /**
     *
     * @param list
     * @param alarmType
     * @return
     */
    List<AlgorithmEventVO> getAlgorithm(@Param("list") List<String> list, @Param("alarmType") String alarmType);

    /**
     *
     * @param body
     * @return
     */
    List<AlarmTypeClassVO> findOrderAlarmList(@Param("body") AlarmTypeClassDTO body);

    /**
     * @Description 查询告警类型列表
     * @author wjd
     * @Date 15:27 2021/7/7
     * @Param [body]
     **/
    List<AlarmType> findAlarmTypeList(@Param("body") AlarmType body);

    Integer devByTenantIdCount(@Param("body") AlarmSourceSubTypeDTO body);

    int uavByTenantIdCount(@Param("body") AlarmSourceSubTypeDTO body);

    Integer iotDevByTenantIdCount(@Param("body") AlarmSourceSubTypeDTO body);

    Integer serviceByTenantIdCount(@Param("body") AlarmSourceSubTypeDTO body);

    /**
     * 根据订购算法查询告警类型
     * @param body
     * @return
     */
    List<AlarmTypeVO> findAlarmTypeByOrder(@Param("body") AlarmSourceSubTypeDTO body);

    List<String> queryOrderByAlgorithmCode(@Param("body") AlarmSourceSubTypeDTO body);

    /**
     * 根据告警来源查询告警类型
     * @param body
     * @return
     */
    List<AlarmTypeVO> findAlarmTypeBySource(@Param("body") AlarmSourceSubTypeDTO body);

    /**
     * @description: 查询订购关联的告警类型（摄像机和AI告警）
     * @author: yongqi.han
     * @date: 2022/8/27
     * @param body
     * @return: List<AlarmTypeDO>
     */
    List<AlarmTypeDO> findAlgorithmAlarm(@Param("body") AlarmTypeDTO body);


    List<Map> getAlarmIconList();

    int modifyIsVisible(@Param("body") SysAlarmTypeIndustryRelVo body);

    int checkSysAlarmTypeIndustryRelVoCount(@Param("body") AlarmTypeClassDTO body);

    int createIsVisible(@Param("body") SysAlarmTypeIndustryRelVo body);

    /**
     * 新增告警类型--人工上报
     * @param alarmTypeList
     * @return
     */
    int addAlarmType(@Param("alarmTypeList") List<AlarmTypeDO> alarmTypeList);

    void delAlarmTypeBatch(List<Long> list);

    Long selectScreenDataConfigCountByIdAndType(String screenId, String screenType, String tenantId, String industryCode, String appVerCode);

    List<ScreenDataConfigParticipationDTO> selectScreenDataConfig(Long screenConfigId);

    List<AlarmTypeVO> queryCustomModelAlarmTypeInfo(@Param("tenantId") String tenantId,
                                                    @Param("industryCode")String industryCode,
                                                    @Param("algorithmCodeList")List<String> algorithmCodeList);

    List<AlgorithmInfoDO> selectModelAlgorithmInfoList();

    /**
     * 查询客户已订购大模型的下游任务列表
     *
     * @param body 租户/行业/算法编码/名称模糊
     * @return 下游任务（typeValue=编码）
     */
    List<AlarmTypeDO> findOrderSubSceneAlarm(@Param("body") AlarmTypeDTO body);
}
