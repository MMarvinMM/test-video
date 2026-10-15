package com.chinatower.event.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.chinatower.event.entity.dto.WarningOrderDTO;
import com.chinatower.event.entity.dto.WarningOrderLayerDTO;
import com.chinatower.event.entity.dto.WarningOrderRelDTO;
import com.chinatower.event.entity.vo.OrderInfoVO;
import com.chinatower.event.entity.vo.WarningOrderListVO;

/**
 * Copyright: Copyright(c) 2019 iwhalecloud
 * <p>
 * 类说明：TODO
 * <p>
 * 类名称: WarningOrderForTradeMapper.java
 *
 * @author su.tingning
 * @version v1.0.0
 * @date 2022/4/12 15:30
 * <p>
 * Modification History:
 * Date         Author          Version            Description
 * ------------------------------------------------------------
 */
@Mapper
public interface WarningOrderForTradeMapper {

    /**
     * 查询告警图层
     * @param dto
     * @return
     */
    List<WarningOrderListVO> getWarningOrderLayer(@Param("param")WarningOrderLayerDTO dto);

    /**
     *
     * @param dto
     * @return
     */
    int updateWarningOrderForTrade(WarningOrderDTO dto);

    /**
     * 查询告警图层
     * @param dto
     * @return
     */
    List<OrderInfoVO> warningOrderInfoRel(@Param("param")WarningOrderRelDTO dto);
}
