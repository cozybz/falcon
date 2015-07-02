package io.falcon.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;

/**
 * FalconOutHandler
 * Created by cozybz@gmail.com on 2015/4/20.
 */
public class FalconOutHandler extends ChannelHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Long l = (Long) msg;
        ByteBuf buf = Unpooled.directBuffer(8);
        buf.writeLong(l);
        ctx.writeAndFlush(buf);
    }
}
