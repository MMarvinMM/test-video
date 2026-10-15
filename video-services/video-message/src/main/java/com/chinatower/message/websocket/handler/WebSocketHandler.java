package com.chinatower.message.websocket.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chinatower.common.core.utils.StringUtils;
import com.chinatower.message.websocket.NettyServer;
import com.chinatower.message.websocket.config.NettyConfig;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;


/**
 * WebSocket
 * @Author: wjd
 * @Date: 2022/1/18
 **/
@Component
@ChannelHandler.Sharable
public class WebSocketHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    private static final Logger log = LoggerFactory.getLogger(NettyServer.class);

    @Value("${server.port}")
    private int port;

    /**
     * 一旦连接，第一个被执行
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        log.info("handlerAdded 被调用"+ctx.channel().id().asLongText());
    }

    /**
     * 读取数据
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        log.info("服务器收到消息：{}",msg.text());

        if ("ping".equals(msg.text())) {
            // 回复消息
            ctx.channel().writeAndFlush(new TextWebSocketFrame("pong"));
        } else {
            JSONObject jsonObject = JSONObject.parseObject(msg.text());
            //c82ff36eccd842e4a2f4088371875d47-900000
            String uid = jsonObject.getString("uid");
            //修改uid key存储逻辑

            // 将用户ID作为自定义属性加入到channel中，方便随时channel中获取用户ID
            AttributeKey<String> key = AttributeKey.valueOf("userId");
            ctx.channel().attr(key).setIfAbsent(uid);
            /*
            修改user channel map 数据结构    {userid: [channel1, channel2]}
            不存在  新增list;存在 list中新增channel
             */
            NettyConfig.getUserChannelListMap().computeIfAbsent(uid, k -> new ArrayList<>()).add(ctx.channel());
            // 回复消息
            ctx.channel().writeAndFlush(new TextWebSocketFrame("服务器连接成功！"));
        }
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        log.info("handlerRemoved 被调用, channelId={}", ctx.channel().id().asLongText());
        // 删除通道
        removeUserChannel(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.info("异常：{}",cause.getMessage());
        // 删除通道
        removeUserChannel(ctx);
    }

    private void removeUserChannel(ChannelHandlerContext ctx) {
        // 删除通道
        AttributeKey<String> key = AttributeKey.valueOf("userId");
        String userId = ctx.channel().attr(key).get();
        if (StringUtils.isNotEmpty(userId)) {
            List<Channel> channels = NettyConfig.getUserChannelListMap().get(userId);
            log.info("userId:{} 在当前节点绑定的channels:{}", userId, channels);
            channels.removeIf(next -> ctx.channel().equals(next));
            log.info("userId:{} 在当前节点移除channel后剩余的绑定channels:{}", userId, channels);

        }
    }

}
