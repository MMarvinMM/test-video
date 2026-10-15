package com.chinatower.event.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chinatower.event.dataobject.ProcessApproveDO;
import com.chinatower.common.core.dto.ProcessApproveDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 事件审批表 Mapper 接口
 * </p>
 *
 * @author AutoGen
 * @since 2022-02-15
 */
public interface ProcessApproveMapper extends BaseMapper<ProcessApproveDO> {


    /**
     * 插入审核单表
     * @param a
     * @return
     */
    int insertOrderApprove(ProcessApproveDTO a);

    /**
     * 查询事件审批
     * @param a
     * @return
     */
    ProcessApproveDTO getOrderApprove(ProcessApproveDTO a);

    /**
     * 更新事件审批
     * @param a
     * @return
     */
    int updateOrderApprove(ProcessApproveDTO a);

    /**
     * 批量插入审核单表
     * @param list
     * @return
     */
    int insertOrderApproveList(@Param("list") List<ProcessApproveDTO> list);

    /**
     * 查询审批时间列表
     * @param a
     * @return
     */
    List<ProcessApproveDTO> getOrderApproveList(ProcessApproveDTO a);

    /**
     * 核实数据
     * @param processOrderId
     * @return
     */
    List<Map> getApproveInfo(String processOrderId);

}
