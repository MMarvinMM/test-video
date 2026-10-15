package com.chinatower.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chinatower.platform.entity.VmMessageSendInfoDO;
import com.chinatower.platform.entity.ISmsSendStatisticsDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author: songxinfeng
 * @Date: 2024/8/7 18:32
 */
public interface IMessageSendInfoMapper extends BaseMapper<VmMessageSendInfoDO> {


    /**
     * 查询未达到短信上限的客户
     * @param dto
     * @return
     */
    List<ISmsSendStatisticsDto> getSmsSendStatistics(ISmsSendStatisticsDto dto);


    /**
     * 更新短信发送上限次数
     * @param dto
     * @return
     */
    int updateSmsSendStatistics(ISmsSendStatisticsDto dto);

    /**
     * 批量新增短信用户发送统计表
     * @param ISmsSendStatisticsDtoList
     * @return
     */
    int insertSmsSendStatistics(@Param("list") List<ISmsSendStatisticsDto> ISmsSendStatisticsDtoList);
}
