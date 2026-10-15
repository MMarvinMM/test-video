package com.chinatower.event.repository;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chinatower.event.dataobject.WarningOrderCollectionDO;
import com.chinatower.event.dto.AlarmOrderDTO;
import com.chinatower.event.entity.dto.BigScreenOrderListDTO;
import com.chinatower.common.core.dto.SendAlarmOrderDTO;
import com.chinatower.event.entity.vo.WarningOrderListVO;

/**
 * <p>
 * 告警信息收藏表 服务类
 * </p>
 *
 * @author AutoGen
 * @since 2022-01-27
 */
public interface IWarningOrderCollectionRepository extends IService<WarningOrderCollectionDO> {

    /**
     * 我的收藏-查询列表
     * @param dto
     * @return
     */
    List<WarningOrderListVO> qryWarningOrderCollection(AlarmOrderDTO dto);

    /**
     * 我的收藏-新增收藏
     * @param req
     */
    int addWarningOrderCollection(WarningOrderCollectionDO req) throws Exception;

    /**
     * 我的收藏-取消收藏
     * @param req
     */
    boolean delWarningOrderCollection(WarningOrderCollectionDO req) throws Exception;

    /**
     * 我的收藏-大屏展示
     * @param dto
     * @return
     */
    List<Map> qryWarningOrderCollectionForBigScreen(BigScreenOrderListDTO dto);

    /**
     * 我的收藏-大屏展示
     * @param dto
     * @return
     */
    int updateWarningOrderCollectionForBigScreen(SendAlarmOrderDTO dto) throws Exception;

}
