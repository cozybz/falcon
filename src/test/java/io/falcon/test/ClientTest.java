package io.falcon.test;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
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
        b.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new ChannelHandlerAdapter() {
                    @Override
                    public void close(ChannelHandlerContext ctx, ChannelPromise future) throws Exception {
                        System.out.println("client close");
                        super.close(ctx, future);
                    }

                    @Override
                    public void disconnect(ChannelHandlerContext ctx, ChannelPromise future) throws Exception {
                        System.out.println("client disconnect");
                        super.disconnect(ctx, future);
                    }

                    @Override
                    public void deregister(ChannelHandlerContext ctx, ChannelPromise future) throws Exception {
                        System.out.println("client deregister");
                        super.deregister(ctx, future);
                    }

                    @Override
                    public void channelActive(ChannelHandlerContext ctx) throws Exception {
                        System.out.println("client channelActive");
                        ByteBuf byteBuf = ctx.alloc().buffer();
                        byteBuf.writeInt(0);
                        ctx.writeAndFlush(byteBuf).addListener(ChannelFutureListener.CLOSE);
                    }

                    @Override
                    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
                        System.out.println("client registered");
                        super.channelRegistered(ctx);
                    }

                    @Override
                    public void bind(ChannelHandlerContext ctx, SocketAddress localAddress, ChannelPromise future) throws Exception {
                        System.out.println("client bind");
                        super.bind(ctx, localAddress, future);
                    }

                    @Override
                    public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise future) throws Exception {
                        System.out.println("client connect");
                        super.connect(ctx, remoteAddress, localAddress, future);
                    }

                    @Override
                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                        System.out.println("client read");
                        super.channelRead(ctx, msg);
                    }

                    @Override
                    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                        System.out.println("client write");
                        super.write(ctx, msg, promise);
                    }

                    @Override
                    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                        System.out.println("client exception");
                        super.exceptionCaught(ctx, cause);
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
