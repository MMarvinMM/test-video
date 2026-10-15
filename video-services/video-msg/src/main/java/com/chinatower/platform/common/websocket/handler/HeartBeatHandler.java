//package com.chinatower.platform.common.websocket.handler;
//
//import cn.hutool.core.util.StrUtil;
//import com.chinatower.platform.common.websocket.config.NettyConfig;
//import com.chinatower.redis.enums.RedisDBType;
//import com.chinatower.redis.util.RedisUtil;
//import com.github.pagehelper.util.StringUtil;
//import io.netty.channel.ChannelHandlerContext;
//import io.netty.channel.ChannelInboundHandlerAdapter;
//import io.netty.handler.timeout.IdleState;
//import io.netty.handler.timeout.IdleStateEvent;
//import io.netty.util.AttributeKey;
//import lombok.extern.slf4j.Slf4j;
//
///**
// * 若超时事件，则关闭这个客户端channel
// *
// * @Author: wjd
// * @Date: 2022/1/18
// */
//@Slf4j
//public class HeartBeatHandler extends ChannelInboundHandlerAdapter {
//
//    @Override
//    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
//        if (evt instanceof IdleStateEvent) {//超时事件
//            log.info("心跳检测超时");
//            IdleStateEvent idleEvent = (IdleStateEvent) evt;
//            if (idleEvent.state() == IdleState.READER_IDLE) {//读
//                ctx.channel().close();  //关闭通道连接
//                removeUserId(ctx); //释放资源
//            } else if (idleEvent.state() == IdleState.WRITER_IDLE) {//写
//
//            } else if (idleEvent.state() == IdleState.ALL_IDLE) {//全部
//
//            }
//        }
//        super.userEventTriggered(ctx, evt);
//    }
//
//    /**
//     * 删除用户与channel的对应关系
//     *
//     * @param ctx
//     */
//    private void removeUserId(ChannelHandlerContext ctx) {
//        AttributeKey<String> key = AttributeKey.valueOf("userId");
//        String userId = ctx.channel().attr(key).get();
//
//        //断连需要释放资源
//        if (StringUtil.isNotEmpty(userId)) {
//            //redis删除用户
//            RedisUtil.delete(userId, RedisDBType.PRIMARY);
//            //删除通道与用户关系
//            NettyConfig.getUserChannelMap().remove(userId);
//        }
//
//    }
//}
