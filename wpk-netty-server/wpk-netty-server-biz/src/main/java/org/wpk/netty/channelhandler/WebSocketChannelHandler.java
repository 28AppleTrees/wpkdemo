package org.wpk.netty.channelhandler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
// Sharable 共享handler, 必须保证该handler线程安全
@ChannelHandler.Sharable
public class WebSocketChannelHandler extends SimpleChannelInboundHandler<WebSocketFrame> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame webSocketFrame) throws Exception {
        // ping
        if (webSocketFrame instanceof PingWebSocketFrame) {
            // 响应pong, retain()用于增加 ByteBuf 的引用计数, 使 ByteBuf 在 retain() 被调用的上下文中仍然可用, 避免 ByteBuf 被提前释放
            ctx.writeAndFlush(new PongWebSocketFrame(webSocketFrame.content().retain()));
            return;
        }
        // 文本消息
        if (webSocketFrame instanceof TextWebSocketFrame) {
            TextWebSocketFrame textWebSocketFrame = (TextWebSocketFrame) webSocketFrame;
            String text = textWebSocketFrame.text();
            log.info("接收消息:" + text);
            return;
        }
        // 二进制消息, 通常统一数据格式二进制, 然后解析, 可以借用Protobuf
        if (webSocketFrame instanceof BinaryWebSocketFrame) {
//            BinaryWebSocketFrame binaryWebSocketFrame = (BinaryWebSocketFrame) webSocketFrame;
//            ByteBuf content = binaryWebSocketFrame.content();

            log.info("接收二进制消息");
            return;
        }
        // 释放 webSocketFrame
        ReferenceCountUtil.release(webSocketFrame);
    }

    /**
     * 事件监听器
     *
     * @param ctx
     * @param evt
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        // 监听ws握手完成
        if (evt instanceof WebSocketServerProtocolHandler.HandshakeComplete) {
            log.info("握手完成");
        }
        // 父类继续处理事件
        super.userEventTriggered(ctx, evt);
    }


    /**
     * 链接建立
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("channel激活");
        super.channelActive(ctx);
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("channel异常关闭", cause);
        ctx.close();
    }

    /**
     * 链接关闭
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("channel非活跃");
        super.channelInactive(ctx);
    }

}
