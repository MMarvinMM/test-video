//package com.chinatower.platform.common.websocket.handler;
//
//import cn.hutool.json.JSONObject;
//import cn.hutool.json.JSONUtil;
//import com.baomidou.mybatisplus.core.toolkit.IdWorker;
//import com.chinatower.platform.common.websocket.NettyServer;
//import com.chinatower.platform.common.websocket.config.NettyConfig;
//import com.chinatower.platform.project.utils.WebToolUtils;
//import com.chinatower.redis.enums.RedisDBType;
//import com.chinatower.redis.util.RedisUtil;
//import com.github.pagehelper.util.StringUtil;
//import io.netty.channel.ChannelHandler;
//import io.netty.channel.ChannelHandlerContext;
//import io.netty.channel.SimpleChannelInboundHandler;
//import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
//import io.netty.util.AttributeKey;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//
//
///**
// * TextWebSocketFrame类型， 表示一个文本帧
// * @Author: wjd
// * @Date: 2022/1/18
// **/
//@Component
//@ChannelHandler.Sharable
//public class WebSocketHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
//    private static final Logger log = LoggerFactory.getLogger(NettyServer.class);
//
//    @Value("${server.port}")
//    private int port;
//
//    /**
//     * 一旦连接，第一个被执行
//     * @param ctx
//     * @throws Exception
//     */
//    @Override
//    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
//
//        log.info("handlerAdded 被调用"+ctx.channel().id().asLongText());
//        // 添加到channelGroup 通道组
//        NettyConfig.getChannelGroup().add(ctx.channel());
//
//
//
//
//    }
//
//    /**
//     * 读取数据
//     */
//    @Override
//    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
//        log.info("服务器收到消息：{}",msg.text());
//
//        // 获取用户ID
////        String uid = SecurityUtils.getLoginUser().getUser().getUserId();  //
//
//        JSONObject jsonObject = JSONUtil.parseObj(msg.text());
//        String uid = jsonObject.getStr("uid");
//
//        //生成10位随机数
//        String num = IdWorker.getIdStr().substring(8,18);
//
//        //key
//        String ukey = uid + "-" + num;
//
//        // 将用户ID作为自定义属性加入到channel中，方便随时channel中获取用户ID
//        AttributeKey<String> key = AttributeKey.valueOf("userId");
//        ctx.channel().attr(key).setIfAbsent(ukey);
//
//        // 关联channel
//        NettyConfig.getUserChannelMap().put(ukey,ctx.channel());
//
//        //获取本机IP
//        String hostAddress = WebToolUtils.getLocalIP()+ ":" + port;
//
//        //存储到redis
//        RedisUtil.hPut(uid,ukey,hostAddress, RedisDBType.PRIMARY);
//
//
//        // 回复消息
//        ctx.channel().writeAndFlush(new TextWebSocketFrame("服务器连接成功！"));
//    }
//
//    @Override
//    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
//        log.info("handlerRemoved 被调用"+ctx.channel().id().asLongText());
//        // 删除通道
//        NettyConfig.getChannelGroup().remove(ctx.channel());
//        removeUserId(ctx);
//    }
//
//    @Override
//    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
//        log.info("异常：{}",cause.getMessage());
//        // 删除通道
//        NettyConfig.getChannelGroup().remove(ctx.channel());
//        removeUserId(ctx);
//        ctx.close();
//    }
//
//    /**
//     * 删除用户与channel的对应关系
//     * @param ctx
//     */
//    private void removeUserId(ChannelHandlerContext ctx){
//        AttributeKey<String> key = AttributeKey.valueOf("userId");
//        String userId = ctx.channel().attr(key).get();
//
//        // 断连需要释放资源
//        if(StringUtil.isNotEmpty(userId)){
//
//            String uid = userId.split("-")[0];
//
//            // redis删除用户
//            RedisUtil.hDelete(uid,RedisDBType.PRIMARY,userId);
//
//            // 删除用户通道关系
//            NettyConfig.getUserChannelMap().remove(userId);
//
//        }
//
//
//    }
//}
