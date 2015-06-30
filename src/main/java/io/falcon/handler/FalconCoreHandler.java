package io.falcon.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

/**
 * FalconCoreHandler
 * Created by cozybz@gmail.com on 2015/4/20.
 */
public class FalconCoreHandler extends ChannelHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("FalconCoreHandler");
        String in = (String) msg;
        System.out.print("FalconCoreHandler " + in + " ");
        System.out.flush();
        ctx.fireChannelWritabilityChanged();
    }
}
