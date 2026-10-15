package com.chinatower.event.repository;

import com.chinatower.common.entity.Result;
import com.chinatower.common.core.dto.SendAlarmOrderDTO;
import com.chinatower.event.entity.dto.WarningOrderDTO;
import com.chinatower.event.entity.dto.WarningOrderLayerDTO;
import com.chinatower.event.entity.dto.WarningOrderRelDTO;
import com.chinatower.event.entity.vo.WarningOrderInfoVO;

import java.util.List;

/**
 * Copyright: Copyright(c) 2019 iwhalecloud
 * <p>
 * 类说明：TODO
 * <p>
 * 类名称: IWarningOrderForTradeRepository.java
 *
 * @author su.tingning
 * @version v1.0.0
 * @date 2022/3/24 10:21
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ------------------------------------------------------------
 */
public interface IWarningOrderForTradeRepository {

    /**
     * 行业调用流程流转
     * @param dto
     * @return
     */
    Result submitProcess(SendAlarmOrderDTO dto);

    /**
     * 查询告警图层
     * @param dto
     * @return
     */
    Result getWarningOrderLayer(WarningOrderLayerDTO dto);

    /**
     * 修改告警事件信息
     * @param dto
     * @return
     */
    Result modifyWarningOrderInfo(WarningOrderDTO dto);

    /**
     * 查询关联告警事件信息
     * @param dto
     * @return
     */
    Result<List<WarningOrderInfoVO>> warningOrderInfoRel(WarningOrderRelDTO dto) throws Exception ;
}
