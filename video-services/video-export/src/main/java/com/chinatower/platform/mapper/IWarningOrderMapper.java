package com.chinatower.platform.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chinatower.event.entity.dto.HandleAlarmImgDTO;
import com.chinatower.event.entity.dto.LeaderAuidtListDTO;
import com.chinatower.event.entity.dto.OrderLeaderNumDTO;
import com.chinatower.event.entity.dto.WarningInterceptDTO;
import com.chinatower.event.entity.vo.*;
import com.chinatower.event.dataobject.WarningOrderDO;
import com.chinatower.platform.dto.AlarmOrderDTO;
import com.chinatower.platform.enums.TableEnum;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author AutoGen
 * @since 2022-01-20
 */
@DS(TableEnum.PLATFORM)
public interface IWarningOrderMapper extends BaseMapper<WarningOrderDO> {

    /**
     * 工作台列表查询
     * @param alarmOrderListReq
     * @Auther yxb
     * @return
     */
    List<WarningOrderListVO> queryAlarmOrder(@Param("param") AlarmOrderDTO alarmOrderListReq);

    /**
     * 工单查询领导批示数量
     * @param resultList
     * @return
     */
    List<OrderLeaderNumDTO> getLeaderAuditNum(@Param("list") List<WarningOrderListVO> resultList);

    /**
     * 回收站列表
     *
     * @param dto
     * @return
     */
    List<InterceptVO> getInterceptList(@Param("param") WarningInterceptDTO dto);

    /**
     * 根据告警id查询告警列表
     * @param map
     * @return
     */
    List<Map> getWarningOrderList(Map map);

    /**
     * 领导批示列表
     *
     * @param dto
     * @return
     */
    List<LeaderAuidtListVO> getLeaderAuditList(@Param("param") LeaderAuidtListDTO dto);

    /**
     * 研判记录列表
     *
     * @param flowTableName
     * @return
     */
    List<FlowRecordVO> queryAlarmOrderFlowRecord(@Param("flowTableName")String flowTableName,@Param("orderIdList")List<String> orderIdList);

    /**
     * 根据工单号查询告警数据
     * @param alarmOrderListReq
     * @return
     */
    List<WarningOrderListVO> queryAlarmOrderByWarningOrderIds(@Param("param") AlarmOrderDTO alarmOrderListReq);


    List<WarningOrderListVO> queryOperateByWarningOrderIds(@Param("param") AlarmOrderDTO alarmOrderListReq);

    /**
     * 更新事件告警原图/框图数据
     * @param handleAlarmImgDTO
     */
    int updateWarningOrder(HandleAlarmImgDTO handleAlarmImgDTO);

    /**
     * 更新研判原图/框图数据
     * @param handleAlarmImgDTO
     */
    int updateAlarmOperate(HandleAlarmImgDTO handleAlarmImgDTO);

    /**
     * 更新压制原图/框图数据
     * @param handleAlarmImgDTO
     */
    int updateIntercept(HandleAlarmImgDTO handleAlarmImgDTO);}
