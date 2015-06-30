package io.falcon.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

/**
 * FalconInHandler
 * Created by cozybz@gmail.com on 2015/4/20.
 */
public class FalconInHandler extends ChannelHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("FalconInHandler");
        ByteBuf in = (ByteBuf) msg;
        try {
            if (in.isReadable()) {
                System.out.print("FalconInHandler " + (char) in.readByte() + "\n");
                System.out.flush();
            }
            ctx.fireChannelRead("abc");
        } finally {
            in.release();
        }
    }
}
