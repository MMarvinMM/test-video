package com.chinatower.receive.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.bdm.api.entity.CheckpointVehicleLicensePlate;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import com.chinatower.receive.vo.dto.ReceiveAlarmDTO;
import com.chinatower.receive.vo.dto.WarnOrderStatusDTO;
import com.chinatower.receive.vo.entity.AlarmOrderReceiveDTO;
import com.chinatower.receive.vo.entity.UserInfoVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @Author: songxinfeng
 * @Date: 2024/6/27 15:50
 */
@Mapper
public interface WarningOrderMapper {

    /**
     * 保存AI告警信息-批量保存
     * @param receiveAlarmDTO
     * @return
     */
    int insertWarningOrders(ReceiveAlarmDTO receiveAlarmDTO);

    /**
     * 创建表
     * @param tableNewName
     * @param tableOldName
     * @return
     */
    int createNewTable(@Param("tableNewName") String tableNewName, @Param("tableOldName") String tableOldName);

    /**
     * 事项数量
     * @param tenantIds
     * @return
     */
    @DS(EbikeDataBaseConstant.BASE)
    List<UserInfoVO> getUserIdByTenantIdBatch(@Param("tenantIds") List<String> tenantIds);

    /**
     * 查询告警数据
     * @param a
     * @return
     */
    WarnOrderStatusDTO getAlarmOrder(AlarmOrderReceiveDTO a);

    /**
     * 查询卡口车牌库
     * @return
     */
    @DS(EbikeDataBaseConstant.BASE)
    List<CheckpointVehicleLicensePlate> queryLicensePlateList(Map map);

    /**
     * 获取算法配置中配置的黑白名单json
     * @param map
     * @return
     */
    @DS(EbikeDataBaseConstant.BASE)
    List<CheckpointVehicleLicensePlate> getBlackWhiteJson(Map map);

    /**
     * 查询智能体扩展字段（回调合并气象等已落库数据用）
     */
    String selectAgentExtendField(@Param("tableName") String tableName,
                                  @Param("warningOrderId") String warningOrderId);

    /**
     * 回写 AI 告警分级研判结果
     */
    int updateAiJudgeResult(@Param("tableName") String tableName,
                            @Param("warningOrderId") String warningOrderId,
                            @Param("riskLevel") String riskLevel,
                            @Param("sceneCode") String sceneCode,
                            @Param("agentExtendField") String agentExtendField,
                            @Param("aiJudgeStatus") Integer aiJudgeStatus);

    /**
     * 同步更新收藏表 AI 研判结果
     */
    int updateAiJudgeResultOnCollection(@Param("warningOrderId") String warningOrderId,
                                        @Param("riskLevel") String riskLevel,
                                        @Param("sceneCode") String sceneCode,
                                        @Param("agentExtendField") String agentExtendField,
                                        @Param("aiJudgeStatus") Integer aiJudgeStatus);

    /**
     * 同步更新收藏表未研判标记与场景模板
     */
    int updateAiJudgePendingOnCollection(@Param("warningOrderId") String warningOrderId,
                                         @Param("sceneTemplate") String sceneTemplate,
                                         @Param("aiJudgeStatus") Integer aiJudgeStatus);

    /**
     * 上报成功后标记为未研判，并写入场景模板
     */
    int updateAiJudgePending(@Param("tableName") String tableName,
                             @Param("warningOrderId") String warningOrderId,
                             @Param("sceneTemplate") String sceneTemplate,
                             @Param("aiJudgeStatus") Integer aiJudgeStatus);

    /**
     * 仅更新研判状态
     */
    int updateAiJudgeStatus(@Param("tableName") String tableName,
                            @Param("warningOrderId") String warningOrderId,
                            @Param("aiJudgeStatus") Integer aiJudgeStatus);
}
