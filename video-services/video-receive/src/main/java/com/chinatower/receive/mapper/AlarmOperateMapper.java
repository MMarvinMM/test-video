package com.chinatower.receive.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chinatower.common.core.domain.AlarmOperateDO;
import com.chinatower.event.entity.dto.AlarmOperateDTO;
import com.chinatower.event.entity.vo.ReceiveAlarmVO;
import com.chinatower.receive.vo.dto.TypeCountDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author: songxinfeng
 * @Date: 2024/7/2 17:41
 */
public interface AlarmOperateMapper extends BaseMapper<AlarmOperateDO> {


    /**
     * 批量保存运营研判表
     *
     * @param list
     * @return
     */
    int insertAlarmOperates(List<ReceiveAlarmVO> list);

    /**
     * 根据主键获取运营研判详情
     *
     * @param warningOrderId
     * @return
     */
    AlarmOperateDTO getAlarmOperate(String warningOrderId);

    /**
     * 批量查询运营研判表
     *
     * @param warningOrderIdList
     * @return
     */
    List<AlarmOperateDTO> getAlarmOperateList(@Param("list") List<String> warningOrderIdList);

    /**
     * 根据执行记录ID查询未手动处理的预警数量
     *
     * @param executionId
     * @return
     */
    Long countByExecutionIdAndNotManual(@Param("executionId") Long executionId);

    /**
     * 根据执行记录ID分组统计判断结果数量
     * @param executionIds
     * @return
     */
    List<TypeCountDTO> groupByJudgeResultCount(@Param("executionIds") List<Long> executionIds);

}
