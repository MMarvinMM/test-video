package com.chinatower.event.repository;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chinatower.common.entity.Result;
import com.chinatower.event.api.model.ApiAlarmOrderRecordModel;
import com.chinatower.event.dataobject.OrderFlowRecordDO;
import com.chinatower.event.dto.OrderFlowRecordDTO;
import com.chinatower.event.dto.SelectLastProcess.SelectLastRrocessReq;
import com.chinatower.event.dto.current.CheckCurrentReq;
import com.chinatower.event.dto.current.CheckCurrentResp;
import com.chinatower.event.entity.dto.FlowLinkAttrModel;
import com.chinatower.event.vo.OrderFlowRecordVO;

import java.util.List;

/**
 * <p>
 * 告警事件工单流程表 服务类
 * </p>
 *
 * @author AutoGen
 * @since 2022-02-11
 */
public interface IOrderFlowRecordRepository extends IService<OrderFlowRecordDO> {

    /**
     * 查询回退环节
     * @param vo
     * @return
     */
    List<FlowLinkAttrModel> queryRollbackLink(ApiAlarmOrderRecordModel vo);

    /**
     * 查询最后一个处理过环节处理人信息
     * @param dto
     * @return
     */
    Result queryLastProcess(SelectLastRrocessReq dto);


    /***
     * 通过主键获取处理环节信息
     * @param dto
     * @return
     */
    Result<OrderFlowRecordVO> getRecordById(OrderFlowRecordDTO dto);

    /**
     * 校验工单是否在当前环节
     * @return
     */
    Result<CheckCurrentResp> getCurrentByOrderIdAndLinkId(CheckCurrentReq req);

}
