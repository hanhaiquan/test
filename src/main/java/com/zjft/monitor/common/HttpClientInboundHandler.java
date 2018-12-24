package com.zjft.monitor.common;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpContent;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created by hqhan on 2018/12/10.
 */
public class HttpClientInboundHandler extends ChannelInboundHandlerAdapter {

    private static Log log= LogFactory.getLog(HttpClientInboundHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof HttpContent) {
            HttpContent content = (HttpContent) msg;
            ByteBuf buf = content.content();
            log.info("收到服务器响应："+buf.toString(io.netty.util.CharsetUtil.UTF_8));
            buf.release();
        }
    }
}
