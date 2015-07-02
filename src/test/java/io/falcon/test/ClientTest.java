package io.falcon.test;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.junit.Test;

import java.net.SocketAddress;

/**
 * Created by cozybz@gmail.com on 2015/4/16.
 */
public class ClientTest {
    @Test
    public void runClient() {
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        Bootstrap b = new Bootstrap();
        b.group(workerGroup);
        b.channel(NioSocketChannel.class);
        b.option(ChannelOption.SO_KEEPALIVE, true);
        b.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
        b.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new ChannelHandlerAdapter() {
                    @Override
                    public void channelActive(ChannelHandlerContext ctx) throws Exception {
                        //System.out.println("client channelActive");
                        ByteBuf byteBuf = Unpooled.directBuffer(8);
                        byteBuf.writeLong(0);
                        ctx.writeAndFlush(byteBuf);
                    }

                    @Override
                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                        //System.out.println("client read");
                        ByteBuf in = (ByteBuf) msg;
                        if (in.isReadable()) {
                            ctx.writeAndFlush(in);
                        }
                    }
                });
            }
        });
        try {
            b.connect("127.0.0.1", 8080).sync().channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
        }
    }
}
