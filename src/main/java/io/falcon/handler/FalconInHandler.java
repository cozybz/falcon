package io.falcon.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

/**
 * FalconInHandler
 * Created by cozybz@gmail.com on 2015/4/20.
 */
public class FalconInHandler extends ChannelHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //System.out.println("FalconInHandler channelRead");
        ByteBuf in = (ByteBuf) msg;
        try {
            if (in.isReadable()) {
                ctx.fireChannelRead(in.readLong());
            }

        } finally {
            in.release();
        }
    }
}
