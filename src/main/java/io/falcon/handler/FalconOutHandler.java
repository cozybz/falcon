package io.falcon.handler;

import io.netty.channel.*;

/**
 * FalconOutHandler
 * Created by cozybz@gmail.com on 2015/4/20.
 */
public class FalconOutHandler extends ChannelHandlerAdapter {
    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        System.out.println("FalconOutHandler");
        ChannelFuture f = ctx.writeAndFlush("hahaha");
        f.addListener(ChannelFutureListener.CLOSE);
    }
}
