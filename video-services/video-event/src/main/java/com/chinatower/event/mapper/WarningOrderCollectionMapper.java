package com.chinatower.event.mapper;

import java.util.List;
import java.util.Map;

import com.chinatower.event.dataobject.WarningOrderCollectionDO;
import com.chinatower.event.dto.AlarmOrderDTO;
import com.chinatower.common.core.dto.SendAlarmOrderDTO;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chinatower.event.entity.dto.BigScreenOrderListDTO;
import com.chinatower.event.entity.vo.WarningOrderListVO;

/**
 * <p>
 * 告警信息收藏表 Mapper 接口
 * </p>
 *
 * @author AutoGen
 * @since 2022-01-27
 */
public interface WarningOrderCollectionMapper extends BaseMapper<WarningOrderCollectionDO> {

    /**
     * 查询收藏数据
     * @param dto
     * @return
     */
    List<WarningOrderListVO> qryWarningOrderCollection(@Param("param") AlarmOrderDTO dto);

    /**
     * 查询告警信息
     * @param dto
     * @return
     */
    List<WarningOrderCollectionDO> getWarningOrderByParam(AlarmOrderDTO dto);

    /**
     *查询回收站告警信息
     */
    WarningOrderCollectionDO getWarningInterceptByParam(AlarmOrderDTO dto);

    /**
     * 查询研判信息信息
     * @param dto
     * @return
     */
    WarningOrderCollectionDO getAlarmOperateOrderByParam(AlarmOrderDTO dto);

    /**
     * 查询大屏收藏数据
     * @param dto
     * @return
     */
    List<Map> qryWarningOrderCollectionForBigScreen(@Param("param") BigScreenOrderListDTO dto);

    /**
     * 事件处理 同步修改收藏里面的事件状态
     */
    int updateByPrimaryKeySelective(SendAlarmOrderDTO sendAlarmOrderDTO);

}
