package com.chinatower.event.repository;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chinatower.common.entity.Result;
import com.chinatower.event.dataobject.AlarmTypeDO;
import com.chinatower.common.core.dto.AlarmTypeClassDTO;
import com.chinatower.event.dataobject.AlarmTypeLevelDO;
import com.chinatower.event.dto.ScreenDataConfigParticipationDTO;
import com.chinatower.event.entity.dto.AlarmSourceSubTypeDTO;
import com.chinatower.event.entity.dto.AlarmTypeDTO;
import com.chinatower.event.entity.dto.AlarmTypeTagInfoDTO;
import com.chinatower.event.entity.vo.AlarmSourceSubTypeVO;
import com.chinatower.event.entity.vo.AlarmTypeTagInfoVO;
import com.chinatower.event.entity.vo.AlarmTypeVO;
import com.chinatower.event.vo.AlarmLevelVO;
import com.chinatower.event.vo.AlarmTypeClassVO;
import com.chinatower.event.entity.vo.AlgorithmEventVO;
import com.chinatower.industry.dto.AlgorithmInfoDO;
import com.github.pagehelper.PageInfo;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 告警类型表 服务类
 * </p>
 *
 * @author AutoGen
 * @since 2022-01-26
 */
public interface IAlarmTypeRepository extends IService<AlarmTypeDO> {
    /**
     * @Description 查询订购类型
     * @author wjd
     * @Date 15:43 2021/7/7
     * @Param [body]
     **/
    public List<AlarmTypeDO> findOrderAlarm(AlarmTypeDTO body);

    /**
     * 根据告警来源查询告警类型
     * @param body 参数
     * @return 告警类型信息
     */
    Result<PageInfo<AlarmTypeDO>> findOrderAlarmBySource(AlarmTypeDTO body);

    /**
     * @Description 查询租户行业下所有订购类型
     * @Param [body]
     **/
    public List<AlarmTypeDO> findOrderAlarmAll(AlarmTypeDTO body);

    /**
     * 告警类型列表查询
     * @param dto
     * @return
     */
    List<AlarmTypeClassVO> findList(AlarmTypeClassDTO dto);

    /**
     * 获取所有租户的压制配置
     *
     * @return
     * @author chenmf
     * @date 2022/3/19
     * @version
     */
    List<AlgorithmEventVO> getAlgorithm(List<String> tenantIdList, String alarmType);

    /**
     * 根据告警类型编码查询告警标签关联信息
     * @param param 参数
     * @return 返回值
     */
    List<AlarmTypeTagInfoVO> selectRelateTagInfoByAlarmType(AlarmTypeTagInfoDTO param);

    /**
     * 根据客户信息查询告警类型标签信息
     * @return 告警类型标签信息
     */
    List<AlarmTypeTagInfoVO> selectTagInfoByTenant();

    /**
     * 查询客户的告警来源及告警类型
     * @param body
     * @return
     */
    List<AlarmSourceSubTypeVO> findAlarmSourceSubType(AlarmSourceSubTypeDTO body);

    /**
     * 查询客户的告警来源及告警类型
     * @param body
     * @return
     */
    List<AlarmTypeVO> findAlarmTypeByTenant(AlarmSourceSubTypeDTO body);

    /**
     * 查询客户的告警来源
     * @param body
     * @return
     */
    List<String> findAlarmSource(AlarmSourceSubTypeDTO body);

    /**
     * @description:查询订购关联的告警类型（摄像机和AI告警）
     * @author: yongqi.han
     * @date: 2022/8/27
     * @param body
     * @return: List<AlarmTypeDO>
     */
    List<AlarmTypeDO> findAlgorithmAlarm(AlarmTypeDTO body);

    List<Map> getAlarmIconList();

    Result modifyIsVisible(AlarmTypeClassDTO body);

    /**
     * 新增告警类型--人工上报
     * @param alarmTypeList
     * @return
     */
    Result<String> addAlarmType(List<AlarmTypeDO> alarmTypeList);

    /**
     * 校验告警类型名称是否重复
     * @param param 告警类型名称
     * @return 返回信息 1-名称已存在 0-名称未存在
     */
    Result<Integer> checkAlarmType(AlarmTypeDO param);

    void delAlarmTypeBatch(List<Long> list);

    /**
     * 获取告警等级
     * @param body 告警类型编码
     * @return 返回告警等级
     */
    AlarmLevelVO getAlarmLevelByTypeValue(AlarmTypeLevelDO body);

    /**
     * 修改告警等级
     * @param body 告警类型等级DO对象
     * @return 修改结果
     */
    Result<String> updateAlarmLevelByTypeValue(AlarmTypeLevelDO body);

    /**
     * @Description: 根据主键查询大屏配置的一键告警类型
     * @Author: wangyt
     * @Date: 2025/1/23
     * @param screenDataConfigId
     * @return java.util.List<com.chinatower.event.dto.ScreenDataConfigParticipationDTO>
     **/
    List<ScreenDataConfigParticipationDTO> selectScreenDataConfig(Long screenDataConfigId);

    /**
     * @Description: 查询开启大屏配置的主键
     * @Author: wangyt
     * @Date: 2025/1/23
     * @param screenId
     * @param screenType
     * @return java.lang.Long
     **/
    Long selectScreenDataConfigCount(String screenId, String screenType);

    /**
     * 查询大模型算法信息
     * @return 大模型算法信息
     */
    List<AlgorithmInfoDO> queryModelAlgorithmInfoList();

    /**
     * 查询客户已订购大模型的下游任务列表（方案 G V2.0 下拉）。
     * 未订购大模型时返回空列表，前端据此隐藏「下游任务」筛选项。
     *
     * @param body 可传 typeName 模糊搜索
     * @return 下游任务编码/名称
     */
    List<AlarmTypeDO> findOrderSubSceneAlarm(AlarmTypeDTO body);
}
