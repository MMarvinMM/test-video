package com.chinatower.receive.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chinatower.event.dataobject.WarningOrderCollectionDO;
import com.chinatower.event.entity.vo.WarningOrderListVO;
import com.chinatower.receive.vo.entity.AlarmOrderReceiveDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author: songxinfeng
 * @Date: 2024/7/2 18:49
 */
public interface WarningOrderCollectionMapper extends BaseMapper<WarningOrderCollectionDO> {

    /**
     * 查询收藏数据
     * @param dto
     * @return
     */
    List<WarningOrderListVO> qryWarningOrderCollection(@Param("param") AlarmOrderReceiveDTO dto);
}
