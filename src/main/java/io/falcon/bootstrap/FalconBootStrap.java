package io.falcon.bootstrap;

import io.falcon.handler.FalconCoreHandler;
import io.falcon.handler.FalconInHandler;
import io.falcon.handler.FalconOutHandler;
import io.falcon.service.FalconService;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * FalconBootStrap
 * Created by cozybz@gmail.com on 2015/4/15.
 */
public class FalconBootStrap {
    public static void startService(FalconService service, int port) {
        EventLoopGroup masterGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();

        bootstrap.group(masterGroup, workerGroup).channel(NioServerSocketChannel.class);

        bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                socketChannel.pipeline().addLast(new FalconInHandler());
                socketChannel.pipeline().addLast(new FalconCoreHandler());
                socketChannel.pipeline().addLast(new FalconOutHandler());
            }
        });

        bootstrap.option(ChannelOption.SO_BACKLOG, 128).childOption(ChannelOption.SO_KEEPALIVE, true);

        try {
            ChannelFuture future = bootstrap.bind(port).sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
            masterGroup.shutdownGracefully();
        }
    }
}
