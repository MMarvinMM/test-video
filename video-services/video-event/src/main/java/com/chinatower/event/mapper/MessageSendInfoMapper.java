package com.chinatower.event.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chinatower.event.dataobject.MessageSendInfoDO;
import com.chinatower.event.dto.MessagePushDTO;
import com.chinatower.event.dto.delaymessage.OrderDelayMessage;
import com.chinatower.event.dto.smssendstatistics.SmsSendStatisticsDto;
import com.chinatower.event.entity.dto.DelayMessageDto;
import com.chinatower.event.vo.UserInfoVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 消息发送通知信息表 Mapper 接口
 * </p>
 *
 * @author AutoGen
 * @since 2022-02-25
 */
public interface MessageSendInfoMapper extends BaseMapper<MessageSendInfoDO> {

    /**
     * 新增消息通知
     * @param dto
     * @return
     */
    int insertMessage(MessagePushDTO dto);

    /**
     * 获取当前租户管理员和铁塔超级管理员账号
     * @param tenantId
     * @return
     */
    List<UserInfoVO> getUserIdByAdmin(String tenantId);

    /**
     * 批量新增延迟消息
     * @param orderDelayMessageList
     * @return
     */
    int insertOrderDelayList(@Param("list") List<OrderDelayMessage> orderDelayMessageList);

    /**
     * 批量修改延迟消息状态
     * @param list
     * @return
     */
    int updateOrderDelayList(@Param("list") List<DelayMessageDto> list);

    /**
     * 查询未达到短信上限的客户
     * @param dto
     * @return
     */
    List<SmsSendStatisticsDto> getSmsSendStatistics(SmsSendStatisticsDto dto);

    /**
     * 更新短信发送上限次数
     * @param dto
     * @return
     */
    int updateSmsSendStatistics(SmsSendStatisticsDto dto);

    /**
     * 批量新增短信用户发送统计表
     * @param smsSendStatisticsDtoList
     * @return
     */
    int insertSmsSendStatistics(@Param("list") List<SmsSendStatisticsDto> smsSendStatisticsDtoList);

}
