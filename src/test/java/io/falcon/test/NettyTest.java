package io.falcon.test;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.junit.Test;

import java.net.SocketAddress;

/**
 * Created by cozybz@gmail.com on 2015/4/15.
 */
public class NettyTest {

    @Test
    public void runServer() {
        EventLoopGroup masterGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(masterGroup, workerGroup).channel(NioServerSocketChannel.class);
        bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                socketChannel.pipeline().addLast(new ChannelHandlerAdapter() {
                    @Override
                    public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
                        System.out.println("server close");
                        super.close(ctx, promise);
                    }

                    @Override
                    public void flush(ChannelHandlerContext ctx) throws Exception {
                        System.out.println("server flush");
                        super.flush(ctx);
                    }

                    @Override
                    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
                        System.out.println("server handlerAdded");
                        super.handlerAdded(ctx);
                    }

                    @Override
                    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
                        System.out.println("server handlerRemoved");
                        super.handlerRemoved(ctx);
                    }

                    @Override
                    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
                        System.out.println("server channelUnregistered");
                        super.channelUnregistered(ctx);
                    }

                    @Override
                    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                        System.out.println("server channelInactive");
                        super.channelInactive(ctx);
                    }

                    @Override
                    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
                        System.out.println("server channelWritabilityChanged");

                        super.channelWritabilityChanged(ctx);
                    }

                    @Override
                    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
                        System.out.println("server readcomplete");
                        super.channelReadComplete(ctx);
                    }

                    @Override
                    public void channelActive(ChannelHandlerContext ctx) throws Exception {
                        System.out.println("server channelActive");
                        super.channelActive(ctx);
                    }

                    @Override
                    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
                        System.out.println("server registered");
                        super.channelRegistered(ctx);
                    }

                    @Override
                    public void bind(ChannelHandlerContext ctx, SocketAddress localAddress, ChannelPromise future) throws Exception {
                        System.out.println("server bind");
                        super.bind(ctx, localAddress, future);
                    }

                    @Override
                    public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise future) throws Exception {
                        System.out.println("server connect");
                        super.connect(ctx, remoteAddress, localAddress, future);
                    }

                    @Override
                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                        System.out.println("server channelRead");
                        ByteBuf byteBuf = (ByteBuf) msg;
                        System.out.println(byteBuf.readInt());
                        super.channelRead(ctx, msg);
                    }

                    @Override
                    public void read(ChannelHandlerContext ctx) throws Exception {
                        System.out.println("server read");
                        super.read(ctx);
                    }

                    @Override
                    public void deregister(ChannelHandlerContext ctx, ChannelPromise future) throws Exception {
                        System.out.println("server deregister");
                        super.deregister(ctx, future);
                    }

                    @Override
                    public void disconnect(ChannelHandlerContext ctx, ChannelPromise future) throws Exception {
                        System.out.println("server disconnect");
                        super.disconnect(ctx, future);
                    }

                    @Override
                    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                        System.out.println("server write");
                        super.write(ctx, msg, promise);
                    }

                    @Override
                    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                        System.out.println("server exception");
                        super.exceptionCaught(ctx, cause);
                    }
                });
            }
        });

        bootstrap.option(ChannelOption.SO_BACKLOG, 1);

        try {
            bootstrap.bind(8080).sync().channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
            masterGroup.shutdownGracefully();
        }
    }

}

