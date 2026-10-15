package com.chinatower.event.mapper;

import java.util.List;
import java.util.Map;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.event.api.model.ApiAlarmOrderRecordModel;
import com.chinatower.event.dto.current.CheckCurrentReq;
import com.chinatower.common.core.dto.ProcessDTO;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chinatower.event.dataobject.OrderFlowRecordDO;
import com.chinatower.event.dto.AlarmOrderRecordDTO;
import com.chinatower.event.dto.OrderFlowRecordDTO;
import com.chinatower.event.vo.AlarmOrderRecordVO;
import com.chinatower.event.vo.OrderFlowRecordVO;

/**
 * <p>
 * 告警事件工单流程表 Mapper 接口
 * </p>
 *
 * @author AutoGen
 * @since 2022-02-11
 */
public interface OrderFlowRecordMapper extends BaseMapper<OrderFlowRecordDO> {

    int insertAlarmOrderRecordList(AlarmOrderRecordDTO dto);

    int insertAlarmOrderRecordListByList(@Param("list") List<AlarmOrderRecordDTO> list);

    /**
     * 创建表
     * @param tableNewName
     * @param tableOldName
     * @return
     */
    int createNewTable(@Param("tableNewName") String tableNewName, @Param("tableOldName") String tableOldName);

    /**
     *  根据事件ID查询当前环节流程记录ID
     * @param map
     * @return
     */
    String getIdByOrderId(Map map);

    /**
     * 根据条件查询是否存在数据
     * @param vo
     * @return
     */
    int getAlarmOrderRecordCount(AlarmOrderRecordVO vo);

    /**
     * 根据条件查询流程环节数据
     * @param vo
     * @return
     */
    AlarmOrderRecordVO getAlarmOrderRecordInfo(AlarmOrderRecordVO vo);

    /**
     * 更新流程环节记录表
     * @param vo
     * @return
     */
    int updateAlarmOrderRecord(AlarmOrderRecordVO vo);

    /**
     * 更新流程环节记录表
     * @param list
     * @return
     */
    int updateAlarmOrderRecordByList(@Param("list") List<AlarmOrderRecordVO> list);

    /**
     * 插入流程环节记录表
     * @param vo
     * @return
     */
    int insertAlarmOrderRecord(AlarmOrderRecordVO vo);


    /**
     * 查询信息表数据
     * @param vo
     * @return
     */
    List<AlarmOrderRecordVO> getAlarmOrderRecordInfoNew(AlarmOrderRecordVO vo);

    /**
     * 查询回退环节
     * @param vo
     * @return
     */
    List<String> queryRollbackLink(ApiAlarmOrderRecordModel vo);


    /**
     * 通过主键查询核实环节详情
     * @param dto
     * @return
     */
    @DS(EbikeDataBaseConstant.PLATFORM)
    OrderFlowRecordVO getPendingCheckById(OrderFlowRecordDTO dto);

    /**
     * 获取指定条件的处理环节数据
     * @param map
     * @return
     */
    @DS(EbikeDataBaseConstant.PLATFORM)
    OrderFlowRecordVO getRecord(OrderFlowRecordDTO map);

    ProcessDTO queryLastProcess(@Param("tableName")String tableName,@Param("orderId")String  orderId,@Param("linkId")String linkId);

    /**
     * 查询流程记录的下个环节处理人
     * @param alarmOrderRecordVO
     * @return
     */
    OrderFlowRecordDO getNextRecordByCurentLink(OrderFlowRecordDTO alarmOrderRecordVO);
    
    /**
     * 查询当前环节是否开启回退
     *
     * @return
     * @author chenmf
     * @date 2022/6/9
     * @version
     */
    List<String> checkRollbackLink(@Param("processTableName")String processTableName, @Param("warnTableName")String warnTableName, @Param("orderId")String orderId);

    /**
     * 查询当前环节是否开启回退(初始化配置)
     *
     * @return
     * @author chenmf
     * @date 2022/6/9
     * @version
     */
    List<String> checkRollbackLinkByIndustry(@Param("processTableName")String processTableName, @Param("warnTableName")String warnTableName, @Param("orderId")String orderId);

    /**
     * 更新删除状态根据事件ID
     * @param dto
     * @return
     */
    int updateDeleteFlagByOrderId(@Param("param") OrderFlowRecordDTO dto);

    /**
     * 校验工单是否在当前环节
     * @return
     */
    String getCurrentByOrderIdAndLinkId(CheckCurrentReq req);
}
