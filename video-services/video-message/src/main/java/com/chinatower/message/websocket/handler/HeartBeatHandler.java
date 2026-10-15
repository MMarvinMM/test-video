package com.chinatower.message.websocket.handler;

import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.message.websocket.config.NettyConfig;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 若超时事件，则关闭这个客户端channel
 *
 * @Author: wjd
 * @Date: 2022/1/18
 */
@Slf4j
public class HeartBeatHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {//超时事件
            log.info("心跳检测超时");
            IdleStateEvent idleEvent = (IdleStateEvent) evt;
            if (idleEvent.state() == IdleState.READER_IDLE) {//读
                ctx.channel().close();  //关闭通道连接
                //删除userid  channel关系
                removeUserChannel(ctx);
            } else if (idleEvent.state() == IdleState.WRITER_IDLE) {//写

            } else if (idleEvent.state() == IdleState.ALL_IDLE) {//全部

            }
        }
        super.userEventTriggered(ctx, evt);
    }

    /**
     * 删除用户与channel的对应关系
     */
    private void removeUserChannel(ChannelHandlerContext ctx) {
        // 删除通道
        AttributeKey<String> key = AttributeKey.valueOf("userId");
        String userId = ctx.channel().attr(key).get();
        if (StringUtils.isNotEmpty(userId)) {
            List<Channel> channels = NettyConfig.getUserChannelListMap().get(userId);
            log.info("userId:{} 在当前节点绑定的channels:{}", userId, channels);
            channels.removeIf(next -> ctx.channel().equals(next));
        }
    }

}
